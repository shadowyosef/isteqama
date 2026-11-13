package com.example.util

import android.content.Context
import android.icu.util.IslamicCalendar
import com.example.model.PrayerTimeItem
import com.example.model.PrayerType
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Calendar
import java.util.Locale

enum class MuadhinVoice(val id: String, val labelAr: String, val labelEn: String) {
    MAKKAH("makkah", "الحرم المكي", "Makkah"),
    MADINAH("madinah", "الحرم النبوي", "Madinah"),
    AQSA("aqsa", "المسجد الأقصى", "Al-Aqsa"),
    AFASY("afasy", "مشاري العفاسي", "Al-Afasy"),
    TAKBIR("takbir", "تكبير قصير", "Short Takbir")
}

object PrayerSeasonsHelper {
    private const val PREFS = "prayer_seasons_prefs"
    val selectedMuadhin = MutableStateFlow(MuadhinVoice.MAKKAH)
    val preAdhan15mEnabled = MutableStateFlow(true)
    val lastThirdAlarmEnabled = MutableStateFlow(false)

    fun init(context: Context) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val mId = p.getString("muadhin", MuadhinVoice.MAKKAH.id)
        selectedMuadhin.value = MuadhinVoice.entries.find { it.id == mId } ?: MuadhinVoice.MAKKAH
        preAdhan15mEnabled.value = p.getBoolean("pre_adhan_15", true)
        lastThirdAlarmEnabled.value = p.getBoolean("last_third_alarm", false)
    }

    fun setMuadhin(context: Context, voice: MuadhinVoice) {
        selectedMuadhin.value = voice
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("muadhin", voice.id).apply()
    }

    fun setPreAdhan15m(context: Context, enabled: Boolean) {
        preAdhan15mEnabled.value = enabled
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean("pre_adhan_15", enabled).apply()
    }

    fun setLastThirdAlarm(context: Context, enabled: Boolean) {
        lastThirdAlarmEnabled.value = enabled
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean("last_third_alarm", enabled).apply()
    }

    fun getSeasonalBanner(isAr: Boolean, cal: Calendar = Calendar.getInstance()): String? {
        val hijri = IslamicCalendar().apply { timeInMillis = cal.timeInMillis }
        val month = hijri.get(IslamicCalendar.MONTH)
        val day = hijri.get(IslamicCalendar.DAY_OF_MONTH)
        return when {
            month == IslamicCalendar.RAMADAN -> if (isAr) "رمضان مبارك (يوم $day) • أذكار الإمساك والسحور والتراويح" else "Ramadan Day $day • Suhoor, Iftar & Taraweeh"
            month == IslamicCalendar.DHU_AL_HIJJAH && day in 1..10 -> if (isAr) "عشر ذي الحجة (يوم $day) • أكثروا من التكبير والعمل الصالح" else "Dhul-Hijjah Day $day • Blessed 10 Days"
            day in 13..15 -> if (isAr) "الأيام البيض ($day هـ) • يُستحب الصيام" else "White Days ($day Hijri) • Sunnah Fasting"
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY -> if (isAr) "جمعة مباركة • قراءة سورة الكهف والصلاة على النبي ﷺ" else "Jumu'ah • Read Al-Kahf & Salawat"
            else -> null
        }
    }

    fun calculateLastThirdStart(prayerTimes: List<PrayerTimeItem>): String {
        val fajr = prayerTimes.firstOrNull { it.type == PrayerType.FAJR } ?: return "03:30"
        val maghrib = prayerTimes.firstOrNull { it.type == PrayerType.MAGHRIB } ?: return "03:30"
        val maghribMin = maghrib.hour * 60 + maghrib.minute
        val fajrMin = fajr.hour * 60 + fajr.minute
        val nightDuration = (24 * 60 - maghribMin) + fajrMin
        val lastThirdTotalMin = (maghribMin + (nightDuration * 2) / 3) % (24 * 60)
        return String.format(Locale.US, "%02d:%02d", lastThirdTotalMin / 60, lastThirdTotalMin % 60)
    }

    fun getForbiddenNaflReason(nowHour: Int, nowMin: Int, prayerTimes: List<PrayerTimeItem>, isAr: Boolean): String? {
        val nowM = nowHour * 60 + nowMin
        val fajr = prayerTimes.firstOrNull { it.type == PrayerType.FAJR }
        val sunrise = prayerTimes.firstOrNull { it.type == PrayerType.SUNRISE }
        val dhuhr = prayerTimes.firstOrNull { it.type == PrayerType.DHUHR }
        val asr = prayerTimes.firstOrNull { it.type == PrayerType.ASR }
        val maghrib = prayerTimes.firstOrNull { it.type == PrayerType.MAGHRIB }
        if (fajr != null && sunrise != null) {
            val f = fajr.hour * 60 + fajr.minute
            val s = sunrise.hour * 60 + sunrise.minute + 15
            if (nowM in (f + 1)..s) return if (isAr) "وقت نهي عن النفل المطلق: بعد الفجر حتى ارتفاع الشمس" else "Forbidden Nafl time: Fajr to Sunrise + 15m"
        }
        if (dhuhr != null) {
            val d = dhuhr.hour * 60 + dhuhr.minute
            if (nowM in (d - 10) until d) return if (isAr) "وقت نهي: قبيل الظهر (الاستواء) بـ 10 دقائق" else "Forbidden Nafl time: 10m before Dhuhr"
        }
        if (asr != null && maghrib != null) {
            val a = asr.hour * 60 + asr.minute
            val m = maghrib.hour * 60 + maghrib.minute
            if (nowM in (a + 1) until m) return if (isAr) "وقت نهي عن النفل المطلق: من بعد العصر حتى الغروب" else "Forbidden Nafl time: Asr until Maghrib"
        }
        return null
    }
}
