package com.example.data

import android.content.Context
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object DailyProgressRepository {
    private const val PREFS_NAME = "istaqama_daily_progress_prefs"
    private const val KEY_PROGRESS_MAP = "progress_map_json"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private var appContext: Context? = null

    val progressMap = MutableStateFlow<Map<String, DailyProgressRecord>>(emptyMap())

    fun todayKey(): String = dateFormat.format(Date())

    fun init(context: Context) {
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_PROGRESS_MAP, null) ?: return
        try {
            val root = JSONObject(jsonStr)
            val map = mutableMapOf<String, DailyProgressRecord>()
            root.keys().forEach { dateKey ->
                val dayObj = root.getJSONObject(dateKey)
                map[dateKey] = DailyProgressJsonHelper.parseDayRecord(dateKey, dayObj)
            }
            progressMap.value = map
        } catch (_: Exception) {}
    }

    private fun persist() {
        val ctx = appContext ?: return
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val root = JSONObject()
        progressMap.value.forEach { (dateKey, record) ->
            root.put(dateKey, DailyProgressJsonHelper.serializeDayRecord(record))
        }
        prefs.edit().putString(KEY_PROGRESS_MAP, root.toString()).apply()
    }

    fun getRecord(dateKey: String): DailyProgressRecord {
        return progressMap.value[dateKey] ?: DailyProgressRecord(dateKey = dateKey)
    }

    fun recordPrayer(context: Context? = null, record: PrayerDailyRecord) {
        val cur = getRecord(record.dateKey)
        val updatedPrayers = cur.prayersMarked.toMutableMap()
        if (record.isPrayed && record.status != PrayerStatus.NOT_PRAYED) {
            updatedPrayers[record.prayerId] = record
        } else {
            updatedPrayers.remove(record.prayerId)
        }
        val act = TinyActivityItem(
            category = ActivityCategory.PRAYER,
            titleAr = "صلاة ${record.prayerType?.nameAr ?: record.prayerId}",
            titleEn = "${record.prayerType?.nameEn ?: record.prayerId} Prayer",
            detailsAr = record.status.labelAr + if (record.notes.isNotBlank()) " • ${record.notes}" else "",
            detailsEn = record.status.labelEn + if (record.notes.isNotBlank()) " • ${record.notes}" else "",
            numericValue = if (record.isPrayed) 1 else 0
        )
        val updated = cur.copy(prayersMarked = updatedPrayers, activities = listOf(act) + cur.activities)
        progressMap.value = progressMap.value + (record.dateKey to updated)
        persist()
    }

    fun recordQuranProgress(ayahs: Int, minutes: Int, surahNumber: Int? = null) {
        val key = todayKey()
        val cur = getRecord(key)
        val surahs = if (surahNumber != null && surahNumber !in cur.surahsRead) cur.surahsRead + surahNumber else cur.surahsRead
        val act = TinyActivityItem(
            category = ActivityCategory.QURAN,
            titleAr = "تلاوة قرآن كريم",
            titleEn = "Quran Recitation",
            detailsAr = "$ayahs آية • $minutes دقيقة",
            detailsEn = "$ayahs ayahs • $minutes mins",
            numericValue = ayahs
        )
        val updated = cur.copy(ayahsRead = cur.ayahsRead + ayahs, quranMinutes = cur.quranMinutes + minutes, surahsRead = surahs, activities = listOf(act) + cur.activities)
        progressMap.value = progressMap.value + (key to updated)
        persist()
    }

    fun recordTasbeehProgress(count: Int, phrase: String = "") {
        val key = todayKey()
        val cur = getRecord(key)
        val act = TinyActivityItem(
            category = ActivityCategory.TASBEEH,
            titleAr = if (phrase.isNotBlank()) "تسبيح: $phrase" else "تسبيح وذكر",
            titleEn = if (phrase.isNotBlank()) "Tasbeeh: $phrase" else "Tasbeeh Dhikr",
            detailsAr = "+$count تسبيحة",
            detailsEn = "+$count dhikr",
            numericValue = count
        )
        val updated = cur.copy(tasbeehCount = cur.tasbeehCount + count, activities = listOf(act) + cur.activities)
        progressMap.value = progressMap.value + (key to updated)
        persist()
    }

    fun recordLockProgress(titleAr: String, titleEn: String) {
        val key = todayKey()
        val cur = getRecord(key)
        val act = TinyActivityItem(
            category = ActivityCategory.LOCK_ROUTINE,
            titleAr = "إتمام قفل: $titleAr",
            titleEn = "Completed Lock: $titleEn",
            detailsAr = "تم إنجاز القفل بنجاح",
            detailsEn = "Successfully finished lock goal",
            numericValue = 1
        )
        val updated = cur.copy(locksCompleted = cur.locksCompleted + 1, activities = listOf(act) + cur.activities)
        progressMap.value = progressMap.value + (key to updated)
        persist()
    }
}
