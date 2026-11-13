package com.example.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.data.DailyProgressRepository
import com.example.data.PrayerRecordRepository
import com.example.data.PrayerTimesRepository
import com.example.model.PrayerDailyRecord
import com.example.model.PrayerStatus
import com.example.model.PrayerType

class PrayerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        PrayerTimesRepository.init(context)
        DailyProgressRepository.init(context)
        val items = PrayerTimesRepository.prayerTimes.value
        for (id in appWidgetIds) {
            val expanded = WidgetUtils.isExpanded(appWidgetManager, id)
            appWidgetManager.updateAppWidget(id, PrayerWidgetLayoutHelper.buildPrayerRemoteViews(context, items, expanded))
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?) {
        PrayerTimesRepository.init(context)
        val items = PrayerTimesRepository.prayerTimes.value
        val expanded = WidgetUtils.isExpanded(appWidgetManager, appWidgetId, newOptions)
        appWidgetManager.updateAppWidget(appWidgetId, PrayerWidgetLayoutHelper.buildPrayerRemoteViews(context, items, expanded))
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_ROLLOVER_UPDATE, ACTION_REFRESH_PRAYERS -> {
                PrayerTimesRepository.init(context)
                DailyProgressRepository.init(context)
                updateAllWidgets(context)
            }
            ACTION_TOGGLE_PRAYER -> {
                val prayerId = intent.getStringExtra(EXTRA_PRAYER_TYPE) ?: return
                val pType = PrayerType.values().find { it.id == prayerId } ?: return
                PrayerTimesRepository.init(context)
                DailyProgressRepository.init(context)
                val item = PrayerTimesRepository.prayerTimes.value.find { it.type == pType } ?: return

                val (canMark, reason) = PrayerRecordRepository.canMarkPrayer(item)
                if (!canMark) {
                    Toast.makeText(context, reason, Toast.LENGTH_SHORT).show()
                    return
                }
                if (!item.isPrayed) {
                    PrayerRecordRepository.markOrUpdatePrayer(
                        context,
                        PrayerDailyRecord(DailyProgressRepository.todayKey(), pType.id, true, PrayerStatus.ON_TIME)
                    )
                } else {
                    PrayerRecordRepository.deletePrayerMark(context, DailyProgressRepository.todayKey(), pType)
                }
                updateAllWidgets(context)
                IbadahDashboardWidgetProvider.updateAll(context)
            }
        }
    }

    companion object {
        const val ACTION_TOGGLE_PRAYER = "com.example.widget.ACTION_TOGGLE_PRAYER"
        const val ACTION_ROLLOVER_UPDATE = "com.example.widget.ACTION_PRAYER_ROLLOVER"
        const val ACTION_REFRESH_PRAYERS = "com.example.widget.ACTION_PRAYER_REFRESH"
        const val EXTRA_PRAYER_TYPE = "extra_prayer_type"

        fun updateAllWidgets(context: Context) {
            val wm = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, PrayerWidgetProvider::class.java)
            val ids = wm.getAppWidgetIds(cn)
            val items = PrayerTimesRepository.prayerTimes.value
            for (id in ids) {
                val expanded = WidgetUtils.isExpanded(wm, id)
                wm.updateAppWidget(id, PrayerWidgetLayoutHelper.buildPrayerRemoteViews(context, items, expanded))
            }
        }
    }
}
