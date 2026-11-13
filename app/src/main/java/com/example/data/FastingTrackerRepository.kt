package com.example.data

import android.content.Context
import android.os.Build
import com.example.model.VoluntaryFastType
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.*

object FastingTrackerRepository {
    private const val PREFS = "fasting_tracker_prefs"
    val completedFasts = MutableStateFlow<Set<String>>(emptySet())

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = prefs.getString("completed_dates", "") ?: ""
        completedFasts.value = raw.split(",").filter { it.isNotBlank() }.toSet()
    }

    fun toggleFastedToday(context: Context, dateIso: String = getTodayIso()): Boolean {
        val current = completedFasts.value.toMutableSet()
        val isNowCompleted = if (current.contains(dateIso)) {
            current.remove(dateIso)
            false
        } else {
            current.add(dateIso)
            StatsRepository.recordDhikrCount(5)
            true
        }
        completedFasts.value = current
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("completed_dates", current.joinToString(",")).apply()
        return isNowCompleted
    }

    fun isFastedToday(): Boolean = completedFasts.value.contains(getTodayIso())

    fun getTodayIso(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    fun getTodayFastingRecommendation(cal: Calendar = Calendar.getInstance()): Pair<VoluntaryFastType?, String> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val hijri = android.icu.util.IslamicCalendar(cal.time)
            val hDay = hijri.get(android.icu.util.IslamicCalendar.DAY_OF_MONTH)
            val hMonth = hijri.get(android.icu.util.IslamicCalendar.MONTH) // 0=Muharram, 11=Dhul-Hijjah
            when {
                hMonth == 11 && hDay == 9 -> return VoluntaryFastType.ARAFAT to "اليوم التاسع من ذي الحجة (يوم عرفة)، صيامه يكفّر سنتين."
                hMonth == 0 && (hDay == 9 || hDay == 10) -> return VoluntaryFastType.ASHURA to "اليوم ${hDay} محرم (تاسوعاء/عاشوراء)، يكفر السنة الماضية."
                hDay in 13..15 -> return VoluntaryFastType.WHITE_DAYS to "اليوم $hDay هـ من الأيام البيض المباركة."
            }
        }
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> VoluntaryFastType.MONDAY_THURSDAY to "اليوم الإثنين، تُعرض فيه الأعمال على الله."
            Calendar.THURSDAY -> VoluntaryFastType.MONDAY_THURSDAY to "اليوم الخميس، يُستحب صيامه سنةً عن النبي ﷺ."
            else -> null to "صيام يوم في سبيل الله يباعد الوجه عن النار سبعين خريفاً."
        }
    }

    fun getTomorrowRecommendation(): Pair<VoluntaryFastType, String>? {
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val (type, note) = getTodayFastingRecommendation(tomorrow)
        return type?.let { it to note }
    }
}
