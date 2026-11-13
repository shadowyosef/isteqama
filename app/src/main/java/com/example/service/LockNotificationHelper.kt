package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.AppPreferencesManager
import com.example.data.PrayerCountdownHelper
import com.example.data.PrayerTimesRepository
import com.example.data.TasbeehRepository
import com.example.receiver.NotificationActionReceiver
import com.example.ui.lock.ActiveLockActivity
import com.example.util.AppStrings
import com.example.util.LockPersistenceManager

object LockNotificationHelper {
    const val CHANNEL_ID = "lock_overlay_channel"
    const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "حماية الورد ومواقيت الصلاة", NotificationManager.IMPORTANCE_LOW).apply {
                description = "عرض عداد الصلاة القادمة واستمرار الحماية"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    fun buildNotification(context: Context): Notification {
        val isLocked = LockPersistenceManager.isLockActive(context)
        val isAr = AppStrings.isArabic
        val (nextPrayer, remainingMs) = PrayerCountdownHelper.getNextPrayerAndRemaining(PrayerTimesRepository.prayerTimes.value)
        val dhikrCount = TasbeehRepository.totalLifetimeCount.value
        val prayerSummary = if (nextPrayer != null) {
            val name = if (isAr) nextPrayer.type.nameAr else nextPrayer.type.nameEn
            val rem = PrayerCountdownHelper.formatCountdown(remainingMs, isAr)
            if (isAr) "القادم: $name ($rem) • التسبيح: $dhikrCount" else "Next: $name ($rem) • Dhikr: $dhikrCount"
        } else {
            if (isAr) "المراقبة نشطة • التسبيح: $dhikrCount" else "Guard Active • Dhikr: $dhikrCount"
        }

        val title = if (isLocked) {
            if (isAr) "استقامة • قفل الورد الإيماني نشط" else "Istaqama • Lock Active"
        } else {
            if (isAr) "استقامة • $prayerSummary" else "Istaqama • $prayerSummary"
        }
        val text = if (isLocked) {
            if (isAr) "انقر لإتمام الورد وفك قفل الشاشة" else "Tap to complete ward & unlock"
        } else {
            if (AppPreferencesManager.isPersistentNotification.value) prayerSummary
            else if (isAr) "حماية الورد نشطة" else "Guard running"
        }

        val targetClass = if (isLocked) ActiveLockActivity::class.java else MainActivity::class.java
        val pi = PendingIntent.getActivity(
            context, 0,
            Intent(context, targetClass).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP) },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val tasbeehPi = PendingIntent.getBroadcast(
            context, 77,
            Intent(context, NotificationActionReceiver::class.java).apply { action = NotificationActionReceiver.ACTION_INCREMENT_TASBEEH },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pi)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (!isLocked) {
            builder.addAction(
                android.R.drawable.ic_input_add,
                if (isAr) "+1 تسبيح ($dhikrCount)" else "+1 Tasbeeh ($dhikrCount)",
                tasbeehPi
            )
        }
        return builder.build()
    }

    fun updateNotification(context: Context) {
        try {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(NOTIFICATION_ID, buildNotification(context))
        } catch (_: Exception) {}
    }
}
