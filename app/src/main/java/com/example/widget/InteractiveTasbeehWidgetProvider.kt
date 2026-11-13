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
import com.example.data.TasbeehRepository
import com.example.util.AppStrings
import com.example.util.TasbeehHapticsHelper

class InteractiveTasbeehWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        DailyProgressRepository.init(context)
        for (id in appWidgetIds) {
            val expanded = WidgetUtils.isExpanded(appWidgetManager, id)
            appWidgetManager.updateAppWidget(id, buildTasbeehRemoteViews(context, expanded))
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?) {
        val expanded = WidgetUtils.isExpanded(appWidgetManager, appWidgetId, newOptions)
        appWidgetManager.updateAppWidget(appWidgetId, buildTasbeehRemoteViews(context, expanded))
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        DailyProgressRepository.init(context)
        when (intent.action) {
            ACTION_COUNT -> {
                val cur = TasbeehRepository.getCurrentItem()
                TasbeehHapticsHelper.performCountFeedback(context, cur.count + 1 >= cur.target)
                TasbeehRepository.increment()
                DailyProgressRepository.recordTasbeehProgress(1, cur.phraseAr)
                updateAll(context)
                IbadahDashboardWidgetProvider.updateAll(context)
            }
            ACTION_NEXT -> {
                val list = TasbeehRepository.phrases.value
                TasbeehRepository.selectedIndex.value = (TasbeehRepository.selectedIndex.value + 1) % list.size.coerceAtLeast(1)
                updateAll(context)
            }
            ACTION_PREV -> {
                val list = TasbeehRepository.phrases.value
                val curIdx = TasbeehRepository.selectedIndex.value
                TasbeehRepository.selectedIndex.value = if (curIdx - 1 < 0) list.lastIndex.coerceAtLeast(0) else curIdx - 1
                updateAll(context)
            }
            ACTION_RESET -> {
                TasbeehRepository.resetCurrent()
                updateAll(context)
            }
            ACTION_QUICK_PHRASE -> {
                val targetIndex = intent.getIntExtra(EXTRA_QUICK_IDX, 0)
                val list = TasbeehRepository.phrases.value
                if (list.isNotEmpty()) {
                    TasbeehRepository.selectedIndex.value = targetIndex.coerceIn(0, list.lastIndex)
                }
                updateAll(context)
            }
        }
    }

    private fun buildTasbeehRemoteViews(context: Context, isExpanded: Boolean): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_tasbeeh_interactive)
        val cur = TasbeehRepository.getCurrentItem()
        val phrases = TasbeehRepository.phrases.value
        val idx = TasbeehRepository.selectedIndex.value
        val isAr = AppStrings.isArabic

        views.setTextViewText(R.id.tv_tasbeeh_phrase, if (isAr) cur.phraseAr else cur.phraseEn)
        views.setTextViewText(R.id.tv_tasbeeh_count, "${cur.count}")
        views.setTextViewText(R.id.tv_tasbeeh_sub, (if (isAr) "اضغط للتسبيح • الهدف: " else "Tap • Goal: ") + "${cur.target}")
        views.setTextViewText(R.id.tv_tasbeeh_index, "${idx + 1} / ${phrases.size}")
        views.setProgressBar(R.id.pb_tasbeeh_goal, cur.target.coerceAtLeast(1), cur.count.coerceAtMost(cur.target), false)

        bindAction(context, views, R.id.btn_tasbeeh_tap_area, ACTION_COUNT, 401)
        bindAction(context, views, R.id.btn_tasbeeh_next, ACTION_NEXT, 402)
        bindAction(context, views, R.id.btn_tasbeeh_prev, ACTION_PREV, 403)
        bindAction(context, views, R.id.btn_tasbeeh_reset, ACTION_RESET, 404)

        // Button-only app launch -> opens Routines -> Tasbeeh subtab (1)
        views.setTextViewText(R.id.btn_tasbeeh_open, if (isAr) "فتح" else "Open")
        views.setOnClickPendingIntent(
            R.id.btn_tasbeeh_open,
            WidgetUtils.createOpenTabPendingIntent(context, 405, tabIndex = 2, subTab = 1)
        )

        views.setViewVisibility(R.id.widget_tasbeeh_expanded, if (isExpanded) View.VISIBLE else View.GONE)
        if (isExpanded) {
            val todayTotal = DailyProgressRepository.getRecord(DailyProgressRepository.todayKey()).tasbeehCount
            views.setTextViewText(
                R.id.tv_tasbeeh_daily_total,
                (if (isAr) "إجمالي تسبيح اليوم: " else "Today's Total: ") + todayTotal
            )
            bindQuickPhrase(context, views, R.id.btn_tasbeeh_quick_1, 0, 411)
            bindQuickPhrase(context, views, R.id.btn_tasbeeh_quick_2, (phrases.size / 2).coerceAtLeast(1), 412)
            bindQuickPhrase(context, views, R.id.btn_tasbeeh_quick_3, phrases.lastIndex.coerceAtLeast(0), 413)
        }
        return views
    }

    private fun bindAction(context: Context, views: RemoteViews, viewId: Int, actionStr: String, reqCode: Int) {
        val intent = Intent(context, InteractiveTasbeehWidgetProvider::class.java).apply { action = actionStr }
        val pi = PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        views.setOnClickPendingIntent(viewId, pi)
    }

    private fun bindQuickPhrase(context: Context, views: RemoteViews, viewId: Int, index: Int, reqCode: Int) {
        val intent = Intent(context, InteractiveTasbeehWidgetProvider::class.java).apply {
            action = ACTION_QUICK_PHRASE
            putExtra(EXTRA_QUICK_IDX, index)
        }
        val pi = PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        views.setOnClickPendingIntent(viewId, pi)
    }

    companion object {
        const val ACTION_COUNT = "com.example.widget.ACTION_TASBEEH_COUNT"
        const val ACTION_NEXT = "com.example.widget.ACTION_TASBEEH_NEXT"
        const val ACTION_PREV = "com.example.widget.ACTION_TASBEEH_PREV"
        const val ACTION_RESET = "com.example.widget.ACTION_TASBEEH_RESET"
        const val ACTION_QUICK_PHRASE = "com.example.widget.ACTION_TASBEEH_QUICK"
        const val EXTRA_QUICK_IDX = "extra_quick_idx"

        fun updateAll(context: Context) {
            val wm = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, InteractiveTasbeehWidgetProvider::class.java)
            val provider = InteractiveTasbeehWidgetProvider()
            for (id in wm.getAppWidgetIds(cn)) {
                wm.updateAppWidget(id, provider.buildTasbeehRemoteViews(context, WidgetUtils.isExpanded(wm, id)))
            }
        }
    }
}
