package com.example.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.example.MainActivity
import com.example.data.DailyProgressRepository
import java.util.Calendar

object WidgetUtils {
    private const val WIDGET_QURAN_PAGES_PREFS = "istaqama_widget_ibadah_prefs"

    fun isExpanded(wm: AppWidgetManager, widgetId: Int, overrideOpts: Bundle? = null): Boolean {
        val opts = overrideOpts ?: wm.getAppWidgetOptions(widgetId) ?: return false
        val minH = opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
        val minW = opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)
        return minH >= 145 || minW >= 290
    }

    fun getHijriDateLabel(isAr: Boolean): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val hijri = android.icu.util.IslamicCalendar(Calendar.getInstance().time)
            val d = hijri.get(android.icu.util.IslamicCalendar.DAY_OF_MONTH)
            val m = hijri.get(android.icu.util.IslamicCalendar.MONTH)
            val y = hijri.get(android.icu.util.IslamicCalendar.YEAR)
            val monthsAr = listOf("محرم", "صفر", "ربيع ١", "ربيع ٢", "جمادى ١", "جمادى ٢", "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة")
            val monthsEn = listOf("Muharram", "Safar", "Rabi I", "Rabi II", "Jumada I", "Jumada II", "Rajab", "Sha'ban", "Ramadan", "Shawwal", "Dhul Qadah", "Dhul Hijjah")
            val mName = if (isAr) monthsAr.getOrElse(m) { "" } else monthsEn.getOrElse(m) { "" }
            return "$d $mName $y"
        }
        return if (isAr) "التقويم الهجري" else "Hijri Calendar"
    }

    fun getKhatmaPercentAndPages(context: Context): Pair<Int, Int> {
        val prefs = context.getSharedPreferences("khatma_planner_prefs", Context.MODE_PRIVATE)
        val readPages = prefs.getInt("khatma_pages_read", 0)
        val todayPages = context.getSharedPreferences(WIDGET_QURAN_PAGES_PREFS, Context.MODE_PRIVATE)
            .getInt("pages_${DailyProgressRepository.todayKey()}", 0)
        val effective = maxOf(readPages, todayPages)
        val pct = ((effective.coerceIn(0, 604) * 100f) / 604f).toInt().coerceIn(0, 100)
        return pct to todayPages
    }

    fun addQuranPage(context: Context) {
        val dayKey = "pages_${DailyProgressRepository.todayKey()}"
        val wPrefs = context.getSharedPreferences(WIDGET_QURAN_PAGES_PREFS, Context.MODE_PRIVATE)
        wPrefs.edit().putInt(dayKey, wPrefs.getInt(dayKey, 0) + 1).apply()

        val kPrefs = context.getSharedPreferences("khatma_planner_prefs", Context.MODE_PRIVATE)
        kPrefs.edit().putInt("khatma_pages_read", (kPrefs.getInt("khatma_pages_read", 0) + 1).coerceAtMost(604)).apply()
        DailyProgressRepository.recordQuranProgress(10, 2)
    }

    fun createOpenTabPendingIntent(
        context: Context,
        reqCode: Int,
        tabIndex: Int,
        subTab: Int = -1,
        surahId: Int = -1,
        openQibla: Boolean = false
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TARGET_TAB", tabIndex)
            if (subTab >= 0) putExtra("EXTRA_SUB_TAB", subTab)
            if (surahId in 1..114) putExtra("EXTRA_OPEN_SURAH_ID", surahId)
            if (openQibla) putExtra("EXTRA_OPEN_QIBLA", true)
        }
        return PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun scheduleRolloverAlarm(context: Context, receiverClass: Class<*>, actionStr: String, delayMs: Long, reqCode: Int) {
        if (delayMs <= 0L) return
        val am = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, receiverClass).apply { action = actionStr }
        val pi = PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val triggerAt = System.currentTimeMillis() + delayMs + 1500L
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            } else {
                am.set(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
        } catch (_: Exception) {}
    }
}
