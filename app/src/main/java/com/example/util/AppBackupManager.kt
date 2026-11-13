package com.example.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.model.AppBackupMetadata
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object AppBackupManager {
    private const val PREFS_NAME = "istaqama_backup_prefs"
    private const val KEY_AUTO_BACKUP = "auto_backup_enabled"
    private const val KEY_LAST_AUTO = "last_auto_backup_time"

    private fun getBackupsDir(context: Context): File {
        val dir = File(context.filesDir, "backups")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun isAutoBackupEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(KEY_AUTO_BACKUP, true)
    }

    fun setAutoBackupEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_AUTO_BACKUP, enabled).apply()
    }

    fun createBackup(context: Context): Result<File> = runCatching {
        val root = JSONObject()
        root.put("version", 1)
        root.put("createdAt", System.currentTimeMillis())
        root.put("device", android.os.Build.MODEL)

        val prefNames = listOf(
            "istaqama_app_prefs", "istaqama_prayer_prefs", "istaqama_daily_progress_prefs",
            "istaqama_tasbeeh_schedules", "istaqama_locks_prefs", "istaqama_toast_pos_prefs",
            "istaqama_custom_dhikr_prefs", "istaqama_smart_alerts"
        )
        val prefsObj = JSONObject()
        prefNames.forEach { name ->
            val p = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            val all = p.all
            val inner = JSONObject()
            all.forEach { (k, v) -> inner.put(k, v) }
            prefsObj.put(name, inner)
        }
        root.put("sharedPreferences", prefsObj)

        val timeStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(getBackupsDir(context), "istaqama_backup_$timeStr.json")
        file.writeText(root.toString(2))
        file
    }

    fun listBackups(context: Context): List<AppBackupMetadata> {
        val dir = getBackupsDir(context)
        val files = dir.listFiles { f -> f.extension == "json" } ?: return emptyList()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return files.sortedByDescending { it.lastModified() }.map { f ->
            AppBackupMetadata(
                fileName = f.name,
                filePath = f.absolutePath,
                timestampMillis = f.lastModified(),
                formattedDate = format.format(Date(f.lastModified())),
                sizeBytes = f.length(),
                recordsCount = 1
            )
        }
    }

    fun restoreBackup(context: Context, jsonContent: String): Result<Boolean> = runCatching {
        val root = JSONObject(jsonContent)
        val prefsObj = root.optJSONObject("sharedPreferences") ?: return@runCatching false
        prefsObj.keys().forEach { name ->
            val p = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            val editor = p.edit()
            val inner = prefsObj.getJSONObject(name)
            inner.keys().forEach { k ->
                when (val v = inner.get(k)) {
                    is Boolean -> editor.putBoolean(k, v)
                    is Int -> editor.putInt(k, v)
                    is Long -> editor.putLong(k, v)
                    is Double -> editor.putFloat(k, v.toFloat())
                    is String -> editor.putString(k, v)
                }
            }
            editor.commit()
        }
        reloadRepositories(context)
        true
    }

    private fun reloadRepositories(context: Context) {
        com.example.data.AppPreferencesManager.init(context)
        com.example.data.PrayerTimesRepository.init(context)
        com.example.data.DailyProgressRepository.init(context)
        com.example.data.ScreenLockRepository.init(context)
    }

    fun deleteBackup(filePath: String): Boolean {
        val f = File(filePath)
        return if (f.exists()) f.delete() else false
    }

    fun exportBackup(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun checkAndPerformAutoBackup(context: Context) {
        if (!isAutoBackupEnabled(context)) return
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastTime = prefs.getLong(KEY_LAST_AUTO, 0L)
        val dayMillis = 24 * 3600 * 1000L
        if (System.currentTimeMillis() - lastTime > dayMillis) {
            createBackup(context)
            prefs.edit().putLong(KEY_LAST_AUTO, System.currentTimeMillis()).apply()
            pruneOldBackups(context, keepCount = 5)
        }
    }

    private fun pruneOldBackups(context: Context, keepCount: Int) {
        val dir = getBackupsDir(context)
        val files = dir.listFiles { f -> f.extension == "json" }?.sortedByDescending { it.lastModified() } ?: return
        if (files.size > keepCount) {
            files.drop(keepCount).forEach { it.delete() }
        }
    }
}
