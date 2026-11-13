package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.example.R
import com.example.data.DailyProgressRepository
import com.example.data.FastingTrackerRepository
import com.example.data.PrayerTimesRepository
import com.example.data.TasbeehRepository
import com.example.util.AppStrings

class IbadahDashboardWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        initRepos(context)
        for (id in appWidgetIds) {
            appWidgetManager.updateAppWidget(id, buildViews(context, WidgetUtils.isExpanded(appWidgetManager, id)))
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?) {
        initRepos(context)
        appWidgetManager.updateAppWidget(appWidgetId, buildViews(context, WidgetUtils.isExpanded(appWidgetManager, appWidgetId, newOptions)))
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        initRepos(context)
        when (intent.action) {
            ACTION_ADD_TASBEEH -> {
                val cur = TasbeehRepository.getCurrentItem()
                TasbeehRepository.increment()
                DailyProgressRepository.recordTasbeehProgress(1, cur.phraseAr)
                updateAll(context)
                InteractiveTasbeehWidgetProvider.updateAll(context)
            }
            ACTION_ADD_QURAN_PAGE -> {
                WidgetUtils.addQuranPage(context)
                updateAll(context)
            }
            ACTION_TOGGLE_FAST -> {
                FastingTrackerRepository.toggleFastedToday(context)
                updateAll(context)
            }
        }
    }

    private fun initRepos(context: Context) {
        DailyProgressRepository.init(context)
        PrayerTimesRepository.init(context)
        FastingTrackerRepository.init(context)
    }

    private fun buildViews(context: Context, isExpanded: Boolean): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_ibadah_dashboard)
        val isAr = AppStrings.isArabic
        val day = DailyProgressRepository.getRecord(DailyProgressRepository.todayKey())
        val prayers = PrayerTimesRepository.prayerTimes.value
        val prayedCount = prayers.count { it.type.isObligatoryPrayer && it.isPrayed }
        val isFasting = FastingTrackerRepository.isFastedToday()
        val (khatmaPct, todayPages) = WidgetUtils.getKhatmaPercentAndPages(context)

        views.setTextViewText(R.id.tv_ibadah_title, if (isAr) "متابعة العبادة اليومية" else "Daily Worship Tracker")
        views.setTextViewText(R.id.btn_ibadah_open_stats, if (isAr) "الإحصائيات" else "Stats")
        views.setTextViewText(R.id.tv_ibadah_prayers_val, "$prayedCount/5")
        views.setTextViewText(R.id.tv_ibadah_tasbeeh_val, "${day.tasbeehCount}")
        views.setTextViewText(R.id.tv_ibadah_pages_val, "$todayPages")
        views.setTextViewText(R.id.tv_ibadah_fast_val, if (isFasting) "✓" else "○")

        bindBroadcast(context, views, R.id.btn_ibadah_add_tasbeeh, ACTION_ADD_TASBEEH, 601)
        bindBroadcast(context, views, R.id.btn_ibadah_add_page, ACTION_ADD_QURAN_PAGE, 602)
        bindBroadcast(context, views, R.id.btn_ibadah_toggle_fast, ACTION_TOGGLE_FAST, 603)

        // Button-only deep links — NO root click listener!
        views.setOnClickPendingIntent(R.id.btn_ibadah_open_stats, WidgetUtils.createOpenTabPendingIntent(context, 604, tabIndex = 4))

        views.setViewVisibility(R.id.widget_ibadah_expanded, if (isExpanded) View.VISIBLE else View.GONE)
        if (isExpanded) {
            views.setTextViewText(R.id.tv_ibadah_hijri, WidgetUtils.getHijriDateLabel(isAr))
            views.setTextViewText(R.id.btn_ibadah_open_quran, if (isAr) "المصحف 📖" else "Quran 📖")
            views.setOnClickPendingIntent(R.id.btn_ibadah_open_quran, WidgetUtils.createOpenTabPendingIntent(context, 605, tabIndex = 1))
            views.setTextViewText(
                R.id.tv_ibadah_khatma_label,
                (if (isAr) "تقدم الختمة: " else "Khatma Progress: ") + "$khatmaPct% • " +
                    (if (isAr) "أقفال: ${day.locksCompleted}" else "Locks: ${day.locksCompleted}")
            )
            views.setProgressBar(R.id.pb_ibadah_khatma, 100, khatmaPct, false)
        }
        return views
    }

    private fun bindBroadcast(context: Context, views: RemoteViews, viewId: Int, actionStr: String, reqCode: Int) {
        val intent = Intent(context, IbadahDashboardWidgetProvider::class.java).apply { action = actionStr }
        val pi = PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        views.setOnClickPendingIntent(viewId, pi)
    }

    companion object {
        const val ACTION_ADD_TASBEEH = "com.example.widget.ACTION_IBADAH_TASBEEH"
        const val ACTION_ADD_QURAN_PAGE = "com.example.widget.ACTION_IBADAH_PAGE"
        const val ACTION_TOGGLE_FAST = "com.example.widget.ACTION_IBADAH_FAST"

        fun updateAll(context: Context) {
            val wm = AppWidgetManager.getInstance(context)
            val ids = wm.getAppWidgetIds(ComponentName(context, IbadahDashboardWidgetProvider::class.java))
            val provider = IbadahDashboardWidgetProvider()
            provider.initRepos(context)
            for (id in ids) wm.updateAppWidget(id, provider.buildViews(context, WidgetUtils.isExpanded(wm, id)))
        }
    }
}
