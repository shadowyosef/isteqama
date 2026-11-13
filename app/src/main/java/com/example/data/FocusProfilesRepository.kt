package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class FocusProfileMode(val id: String, val labelAr: String, val labelEn: String, val descAr: String, val descEn: String) {
    NONE("none", "عادي", "Normal", "لا توجد قيود تركيز إضافية", "No extra focus restrictions"),
    STUDY_WORK("study", "الدراسة والعمل", "Study & Work", "تقييد التطبيقات الترفيهية أثناء المهام", "Limit distracting apps during focus"),
    QIYAM_SECLUSION("qiyam", "خلوة وقيام", "Qiyam Seclusion", "هدوء روحي ومنع الإشعارات المشتتة", "Quiet spiritual seclusion mode"),
    FRIDAY_KAHF("friday", "وضع الجمعة والكهف", "Friday Mode", "تذكير بالكهف والصلاة على النبي ﷺ", "Kahf & Salawat reminders")
}

object FocusProfilesRepository {
    private const val PREF_NAME = "focus_profiles_prefs"
    private var prefs: android.content.SharedPreferences? = null

    val activeProfile = MutableStateFlow(FocusProfileMode.NONE)
    val autoFridayMode = MutableStateFlow(true)
    val autoPrayerFocus = MutableStateFlow(true)
    val appDailyLimitMins = MutableStateFlow<Map<String, Int>>(emptyMap())
    val appUsageSecondsToday = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val bypassUntilMs = mutableMapOf<String, Long>()
    private var usageDateKey = todayStr()

    fun init(context: Context) {
        if (prefs != null) return
        val p = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs = p
        val savedId = p.getString("active_profile", FocusProfileMode.NONE.id)
        activeProfile.value = FocusProfileMode.entries.find { it.id == savedId } ?: FocusProfileMode.NONE
        autoFridayMode.value = p.getBoolean("auto_friday", true)
        autoPrayerFocus.value = p.getBoolean("auto_prayer", true)
        appDailyLimitMins.value = decodeIntMap(p.getString("limits_json", null))
        usageDateKey = p.getString("usage_date", todayStr()) ?: todayStr()
        if (usageDateKey == todayStr()) {
            appUsageSecondsToday.value = decodeIntMap(p.getString("usage_json", null))
        } else {
            usageDateKey = todayStr()
            appUsageSecondsToday.value = emptyMap()
        }
    }

    fun setProfile(context: Context, mode: FocusProfileMode) {
        init(context)
        activeProfile.value = mode
        prefs?.edit()?.putString("active_profile", mode.id)?.apply()
    }

    fun setAutoFridayMode(context: Context, enabled: Boolean) {
        init(context)
        autoFridayMode.value = enabled
        prefs?.edit()?.putBoolean("auto_friday", enabled)?.apply()
    }

    fun setAutoPrayerFocus(context: Context, enabled: Boolean) {
        init(context)
        autoPrayerFocus.value = enabled
        prefs?.edit()?.putBoolean("auto_prayer", enabled)?.apply()
    }

    fun setAppDailyLimit(context: Context, packageName: String, minutes: Int) {
        init(context)
        val next = appDailyLimitMins.value.toMutableMap()
        if (minutes <= 0) next.remove(packageName) else next[packageName] = minutes
        appDailyLimitMins.value = next
        prefs?.edit()?.putString("limits_json", encodeIntMap(next))?.apply()
    }

    fun grantTemporaryBypass(packageName: String, minutes: Int = 2) {
        bypassUntilMs[packageName] = System.currentTimeMillis() + minutes * 60_000L
    }

    fun isTemporaryBypassActive(packageName: String): Boolean {
        val until = bypassUntilMs[packageName] ?: return false
        return System.currentTimeMillis() < until
    }

    fun recordForegroundSeconds(context: Context, packageName: String, deltaSec: Int = 1): Boolean {
        init(context)
        val today = todayStr()
        if (usageDateKey != today) {
            usageDateKey = today
            appUsageSecondsToday.value = emptyMap()
        }
        val currentMap = appUsageSecondsToday.value.toMutableMap()
        val updatedSec = (currentMap[packageName] ?: 0) + deltaSec
        currentMap[packageName] = updatedSec
        appUsageSecondsToday.value = currentMap
        if (updatedSec % 10 == 0) {
            prefs?.edit()?.putString("usage_date", today)?.putString("usage_json", encodeIntMap(currentMap))?.apply()
        }
        val capMinutes = appDailyLimitMins.value[packageName] ?: return false
        return updatedSec >= capMinutes * 60 && !isTemporaryBypassActive(packageName)
    }

    fun effectiveProfile(cal: Calendar = Calendar.getInstance()): FocusProfileMode {
        if (activeProfile.value != FocusProfileMode.NONE) return activeProfile.value
        val isFriday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        if (autoFridayMode.value && isFriday && hour in 5..18) {
            return FocusProfileMode.FRIDAY_KAHF
        }
        return FocusProfileMode.NONE
    }

    private fun todayStr(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun encodeIntMap(map: Map<String, Int>): String {
        val obj = JSONObject()
        map.forEach { (k, v) -> obj.put(k, v) }
        return obj.toString()
    }

    private fun decodeIntMap(raw: String?): Map<String, Int> {
        if (raw.isNullOrBlank()) return emptyMap()
        return try {
            val obj = JSONObject(raw)
            obj.keys().asSequence().associateWith { obj.optInt(it, 0) }
        } catch (_: Exception) { emptyMap() }
    }
}
