package com.example.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.local.entity.ScreenLockEntity
import com.example.receiver.LockWatchdogReceiver
import com.example.ui.lock.ActiveLockActivity

object LockPersistenceManager {
    private const val PREFS_NAME = "istaqama_active_lock_state"
    private const val KEY_IS_LOCKED = "is_active_locked"
    private const val KEY_LOCK_ID = "active_lock_id"
    private const val KEY_LOCK_TITLE = "active_lock_title"
    private const val KEY_LOCK_TYPE = "active_lock_type"
    private const val KEY_TARGET_AMOUNT = "active_lock_target"
    private const val KEY_CONFIG_JSON = "active_lock_config_json"
    private const val KEY_STRICTNESS = "active_lock_strictness"
    private const val WATCHDOG_REQ_CODE = 9981

    fun saveActiveLock(context: Context, lock: ScreenLockEntity) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_IS_LOCKED, true)
            .putString(KEY_LOCK_ID, lock.id)
            .putString(KEY_LOCK_TITLE, lock.title)
            .putString(KEY_LOCK_TYPE, lock.lockType)
            .putInt(KEY_TARGET_AMOUNT, lock.targetAmount)
            .putString(KEY_CONFIG_JSON, lock.contentConfigJson)
            .putString(KEY_STRICTNESS, lock.strictnessMode)
            .commit()
        scheduleWatchdog(context)
    }

    fun clearActiveLock(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        cancelWatchdog(context)
    }

    fun isLockActive(context: Context): Boolean {
        if (LockScheduleTracker.isGlobalCooldownActive(context)) return false
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_IS_LOCKED, false)) return false
        val id = prefs.getString(KEY_LOCK_ID, null) ?: return false
        val existing = com.example.data.ScreenLockRepository.locks.value.find { it.id == id }
        if (existing != null && !existing.isEnabled) {
            clearActiveLock(context)
            return false
        }
        if (id.startsWith("lock_routine_")) {
            val rId = id.removePrefix("lock_routine_")
            val routine = com.example.data.UnifiedRoutineRepository.routines.value.find { it.id == rId }
            if (routine != null && !routine.isLockActive) {
                clearActiveLock(context)
                return false
            }
        }
        return true
    }

    fun restoreActiveLock(context: Context): ScreenLockEntity? {
        if (!isLockActive(context)) return null
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val id = prefs.getString(KEY_LOCK_ID, null) ?: return null
        val title = prefs.getString(KEY_LOCK_TITLE, "ورد الحماية") ?: "ورد الحماية"
        val type = prefs.getString(KEY_LOCK_TYPE, "QURAN") ?: "QURAN"
        val target = prefs.getInt(KEY_TARGET_AMOUNT, 7)
        val configJson = prefs.getString(KEY_CONFIG_JSON, "") ?: ""
        val strictness = prefs.getString(KEY_STRICTNESS, "STRICT_KIOSK") ?: "STRICT_KIOSK"
        return ScreenLockEntity(
            id = id,
            title = title,
            lockType = type,
            targetAmount = target,
            contentConfigJson = configJson,
            strictnessMode = strictness
        )
    }

    fun scheduleWatchdog(context: Context, delayMillis: Long = 1000L) {
        if (!isLockActive(context)) return
        val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, LockWatchdogReceiver::class.java).apply {
            action = "com.example.ACTION_LOCK_WATCHDOG_CHECK"
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pi = PendingIntent.getBroadcast(context, WATCHDOG_REQ_CODE, intent, flags)
        val triggerAt = System.currentTimeMillis() + delayMillis
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            } else {
                am.set(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
        } catch (_: Exception) {
            am.set(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    fun cancelWatchdog(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, LockWatchdogReceiver::class.java).apply {
            action = "com.example.ACTION_LOCK_WATCHDOG_CHECK"
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pi = PendingIntent.getBroadcast(context, WATCHDOG_REQ_CODE, intent, flags)
        am.cancel(pi)
    }

    fun enforceLockReopen(context: Context) {
        if (isLockActive(context)) {
            val restored = restoreActiveLock(context)
            if (restored != null) {
                ActiveLockActivity.launch(context, restored)
            } else {
                ActiveLockActivity.launch(context)
            }
            scheduleWatchdog(context, 2000L)
        }
    }
}
