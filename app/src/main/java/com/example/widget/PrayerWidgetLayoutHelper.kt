package com.example.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import com.example.R
import com.example.data.FastingTrackerRepository
import com.example.data.PrayerCountdownHelper
import com.example.data.PrayerTimesRepository
import com.example.model.PrayerTimeItem
import com.example.model.PrayerType
import com.example.util.AppStrings

object PrayerWidgetLayoutHelper {

    fun buildPrayerRemoteViews(context: Context, items: List<PrayerTimeItem>, isExpanded: Boolean = false): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_prayer_layout)
        val loc = PrayerTimesRepository.currentLocation.value
        val isAr = AppStrings.isArabic

        val (nextItem, remainingMs) = PrayerCountdownHelper.getNextPrayerAndRemaining(items)
        val nextName = if (nextItem != null) (if (isAr) nextItem.type.nameAr else nextItem.type.nameEn) else ""
        val cityText = (if (isAr) loc.nameAr else loc.nameEn) + if (nextName.isNotBlank()) " • $nextName" else ""
        views.setTextViewText(R.id.tv_prayer_city, cityText)

        val safeMs = if (remainingMs > 0L) remainingMs else 1000L
        views.setChronometerCountDown(R.id.tv_prayer_next_countdown, true)
        views.setChronometer(R.id.tv_prayer_next_countdown, SystemClock.elapsedRealtime() + safeMs, "%s", true)
        if (remainingMs > 0L) {
            WidgetUtils.scheduleRolloverAlarm(context, PrayerWidgetProvider::class.java, PrayerWidgetProvider.ACTION_ROLLOVER_UPDATE, remainingMs, 290)
        }

        views.setTextViewText(R.id.btn_prayer_open, if (isAr) "فتح الصلاة" else "Prayers")
        views.setOnClickPendingIntent(R.id.btn_prayer_open, WidgetUtils.createOpenTabPendingIntent(context, 201, tabIndex = 0))

        val prayerMap = items.associateBy { it.type }
        bindPrayerChip(context, views, prayerMap[PrayerType.FAJR], PrayerType.FAJR, R.id.btn_prayer_fajr, R.id.tv_fajr_time, R.id.iv_fajr_check, 301)
        bindPrayerChip(context, views, prayerMap[PrayerType.DHUHR], PrayerType.DHUHR, R.id.btn_prayer_dhuhr, R.id.tv_dhuhr_time, R.id.iv_dhuhr_check, 302)
        bindPrayerChip(context, views, prayerMap[PrayerType.ASR], PrayerType.ASR, R.id.btn_prayer_asr, R.id.tv_asr_time, R.id.iv_asr_check, 303)
        bindPrayerChip(context, views, prayerMap[PrayerType.MAGHRIB], PrayerType.MAGHRIB, R.id.btn_prayer_maghrib, R.id.tv_maghrib_time, R.id.iv_maghrib_check, 304)
        bindPrayerChip(context, views, prayerMap[PrayerType.ISHA], PrayerType.ISHA, R.id.btn_prayer_isha, R.id.tv_isha_time, R.id.iv_isha_check, 305)

        views.setViewVisibility(R.id.widget_prayer_expanded, if (isExpanded) View.VISIBLE else View.GONE)
        if (isExpanded) {
            val sunrise = prayerMap[PrayerType.SUNRISE]?.timeFormatted ?: "--:--"
            val done = items.count { it.type.isObligatoryPrayer && it.isPrayed }
            val total = items.count { it.type.isObligatoryPrayer }.coerceAtLeast(5)
            views.setTextViewText(R.id.tv_prayer_hijri_sunrise, "${WidgetUtils.getHijriDateLabel(isAr)} • ☀ $sunrise")
            views.setTextViewText(R.id.tv_prayer_done_summary, "$done / $total")
            val (_, fastNote) = FastingTrackerRepository.getTodayFastingRecommendation()
            views.setTextViewText(R.id.tv_prayer_fasting_note, fastNote)
            views.setOnClickPendingIntent(R.id.btn_prayer_qibla, WidgetUtils.createOpenTabPendingIntent(context, 202, tabIndex = 0, openQibla = true))
            val refreshPi = PendingIntent.getBroadcast(
                context, 203,
                Intent(context, PrayerWidgetProvider::class.java).apply { action = PrayerWidgetProvider.ACTION_REFRESH_PRAYERS },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_prayer_refresh, refreshPi)
        }
        return views
    }

    private fun bindPrayerChip(
        context: Context,
        views: RemoteViews,
        item: PrayerTimeItem?,
        type: PrayerType,
        chipId: Int,
        timeId: Int,
        checkId: Int,
        requestCode: Int
    ) {
        if (item == null) return
        views.setTextViewText(timeId, item.timeFormatted)
        if (item.isPrayed) {
            views.setInt(chipId, "setBackgroundResource", R.drawable.bg_widget_chip_prayed)
            views.setImageViewResource(checkId, R.drawable.ic_check_circle)
        } else if (!item.isPassed) {
            views.setInt(chipId, "setBackgroundResource", R.drawable.bg_widget_chip)
            views.setImageViewResource(checkId, R.drawable.ic_lock_small)
        } else {
            views.setInt(chipId, "setBackgroundResource", R.drawable.bg_widget_chip)
            views.setImageViewResource(checkId, R.drawable.ic_check_circle)
        }
        val toggleIntent = Intent(context, PrayerWidgetProvider::class.java).apply {
            action = PrayerWidgetProvider.ACTION_TOGGLE_PRAYER
            putExtra(PrayerWidgetProvider.EXTRA_PRAYER_TYPE, type.id)
        }
        val pi = PendingIntent.getBroadcast(context, requestCode, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        views.setOnClickPendingIntent(chipId, pi)
    }
}
