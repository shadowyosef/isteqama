package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import com.example.R
import com.example.data.DailyProgressRepository
import com.example.data.ScreenLockRepository
import com.example.data.TasbeehRepository
import com.example.util.AppStrings

class DhikrWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, WidgetUtils.isExpanded(appWidgetManager, appWidgetId))
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?) {
        updateAppWidget(context, appWidgetManager, appWidgetId, WidgetUtils.isExpanded(appWidgetManager, appWidgetId, newOptions))
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_INCREMENT_TASBEEH -> {
                setWidgetCount(context, getWidgetCount(context) + 1)
                TasbeehRepository.increment()
                DailyProgressRepository.init(context)
                DailyProgressRepository.recordTasbeehProgress(1, TasbeehRepository.getCurrentItem().phraseAr)
                updateAll(context)
            }
            ACTION_REFRESH_LOCKS -> updateAll(context)
        }
    }

    companion object {
        const val ACTION_INCREMENT_TASBEEH = "com.example.widget.ACTION_INCREMENT_TASBEEH"
        const val ACTION_REFRESH_LOCKS = "com.example.widget.ACTION_DHIKR_REFRESH"
        private const val PREFS_NAME = "dhikr_widget_prefs"
        private const val KEY_WIDGET_COUNT = "widget_count"

        fun getWidgetCount(context: Context): Int =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_WIDGET_COUNT, 0)

        fun setWidgetCount(context: Context, count: Int) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putInt(KEY_WIDGET_COUNT, count).apply()
        }

        fun updateAll(context: Context) {
            val wm = AppWidgetManager.getInstance(context)
            val allIds = wm.getAppWidgetIds(ComponentName(context, DhikrWidgetProvider::class.java))
            for (id in allIds) updateAppWidget(context, wm, id, WidgetUtils.isExpanded(wm, id))
        }

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, isExpanded: Boolean = false) {
            ScreenLockRepository.init(context)
            DailyProgressRepository.init(context)
            val isAr = AppStrings.isArabic
            val views = RemoteViews(context.packageName, R.layout.widget_dhikr_layout)

            val (nearestLock, diffMillis) = ScreenLockRepository.getRemainingMillisToNextLock(context)
            if (nearestLock != null && diffMillis > 0L) {
                views.setViewVisibility(R.id.widget_countdown, View.VISIBLE)
                views.setViewVisibility(R.id.widget_countdown_empty, View.GONE)
                views.setChronometerCountDown(R.id.widget_countdown, true)
                views.setChronometer(R.id.widget_countdown, SystemClock.elapsedRealtime() + diffMillis, "%s", true)
                WidgetUtils.scheduleRolloverAlarm(context, DhikrWidgetProvider::class.java, ACTION_REFRESH_LOCKS, diffMillis, 190)
            } else {
                views.setViewVisibility(R.id.widget_countdown, View.GONE)
                views.setViewVisibility(R.id.widget_countdown_empty, View.VISIBLE)
                views.setTextViewText(R.id.widget_countdown_empty, if (isAr) "لا قفل قادم" else "No active lock")
            }

            views.setTextViewText(R.id.widget_title, nearestLock?.title ?: (if (isAr) "قفل الورد والتسبيح" else "Dhikr & Locks"))
            val contentText = when (nearestLock?.lockType?.uppercase()) {
                "QURAN" -> if (isAr) "الورد القرآني: قراءة الآيات وتدبرها" else "Quran Ward: Recite & reflect"
                "DHIKR" -> if (isAr) "أذكار الصباح والمساء والاستغفار" else "Daily Adhkar & Istighfar"
                else -> if (isAr) "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ" else "SubhanAllah wa Bihamdihi"
            }
            views.setTextViewText(R.id.widget_content, contentText)
            views.setTextViewText(R.id.widget_counter_label, if (isAr) "التسبيح: ${getWidgetCount(context)}" else "Count: ${getWidgetCount(context)}")

            // Button-only deep links (Locker tab = 3, Adhkar subtab = 2/0) — no root click listener!
            views.setTextViewText(R.id.widget_btn_open, if (isAr) "الأقفال" else "Locks")
            views.setOnClickPendingIntent(R.id.widget_btn_open, WidgetUtils.createOpenTabPendingIntent(context, 101, tabIndex = 3))

            val incPi = PendingIntent.getBroadcast(
                context, 102,
                Intent(context, DhikrWidgetProvider::class.java).apply { action = ACTION_INCREMENT_TASBEEH },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_increment, incPi)

            views.setViewVisibility(R.id.widget_dhikr_expanded, if (isExpanded) View.VISIBLE else View.GONE)
            if (isExpanded) {
                val doneLocks = DailyProgressRepository.getRecord(DailyProgressRepository.todayKey()).locksCompleted
                views.setTextViewText(R.id.widget_locks_done, (if (isAr) "أقفال اليوم المنجزة: " else "Locks Completed Today: ") + doneLocks)
                views.setTextViewText(R.id.widget_btn_open_adhkar, if (isAr) "الأذكار" else "Adhkar")
                views.setOnClickPendingIntent(R.id.widget_btn_open_adhkar, WidgetUtils.createOpenTabPendingIntent(context, 103, tabIndex = 2, subTab = 0))
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
