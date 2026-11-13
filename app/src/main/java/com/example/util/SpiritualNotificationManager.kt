package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.SmartAlertPreferences
import com.example.data.local.entity.ScreenLockEntity
import com.example.model.*
import com.example.ui.lock.ActiveLockActivity

object SpiritualNotificationManager {
    const val CHANNEL_ID = "spiritual_reminders_channel"

    fun init(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "تذكيرات الورد والذكر الذكية",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات لمواعيد قراءة القرآن وأوراد الأذكار والتسبيح"
                enableVibration(SmartAlertPreferences.alertSoundStyle.value != AlertSoundStyle.SILENT)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun sendReminder(context: Context, title: String, message: String, isQuran: Boolean = false) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TARGET_TAB", if (isQuran) 0 else 1)
        }
        val pi = PendingIntent.getActivity(context, title.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        postNotification(context, 2000 + (title.hashCode() % 1000), title, message, pi, if (AppStrings.isArabic) "فتح" else "Open")
    }

    fun sendTasbeehReminder(context: Context, model: TasbeehScheduleModel) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TARGET_TAB", 1)
            putExtra("EXTRA_SUB_TAB", 1)
            putExtra("EXTRA_TASBEEH_ID", model.id)
        }
        val pi = PendingIntent.getActivity(context, model.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val title = if (AppStrings.isArabic) "تذكير تسبيح: ${model.phraseAr}" else "Tasbeeh Alert: ${model.phraseEn}"
        val msg = if (AppStrings.isArabic) "الهدف: ${model.targetCount} مرة - اضغط للذكر" else "Goal: ${model.targetCount}x - Tap to recite"
        postNotification(context, 3000 + (model.id.hashCode() % 1000), title, msg, pi, if (AppStrings.isArabic) "ابدأ التسبيح" else "Start Tasbeeh")
    }

    fun sendRoutineReminder(context: Context, routine: UnifiedRoutine) {
        val intent = if (routine.isLockActive) {
            Intent(context, ActiveLockActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("EXTRA_ROUTINE_ID", routine.id)
            }
        } else {
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("EXTRA_TARGET_TAB", 1)
                putExtra("EXTRA_SUB_TAB", 0)
                putExtra("EXTRA_ROUTINE_ID", routine.id)
            }
        }
        val pi = PendingIntent.getActivity(context, routine.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val title = if (AppStrings.isArabic) "موعد الورد: ${routine.titleAr}" else "Routine: ${routine.titleEn}"
        val msg = if (routine.isLockActive) (if (AppStrings.isArabic) "قفل الشاشة مفعل - اضغط للإتمام" else "Screen lock active") else routine.detailsSummary
        postNotification(context, 4000 + (routine.id.hashCode() % 1000), title, msg, pi, if (AppStrings.isArabic) "افتح الورد" else "Open Routine")
    }

    fun sendLockReminder(context: Context, lock: ScreenLockEntity) {
        val intent = Intent(context, ActiveLockActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_LOCK_ID", lock.id)
            putExtra("EXTRA_LOCK_TITLE", lock.title)
            putExtra("EXTRA_LOCK_TYPE", lock.lockType)
            putExtra("EXTRA_LOCK_TARGET", lock.targetAmount)
            putExtra("EXTRA_LOCK_CONFIG", lock.contentConfigJson)
        }
        val pi = PendingIntent.getActivity(context, lock.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val title = if (AppStrings.isArabic) "حان موعد القفل الإيماني: ${lock.title}" else "Lock Active: ${lock.title}"
        val msg = if (AppStrings.isArabic) "أتمم المهمة لفك قفل الجهاز" else "Complete task to unlock device"
        postNotification(context, 5000 + (lock.id.hashCode() % 1000), title, msg, pi, if (AppStrings.isArabic) "ابدأ فك القفل" else "Unlock Now")
    }

    private fun postNotification(context: Context, id: Int, title: String, message: String, pi: PendingIntent, actionLabel: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .addAction(android.R.drawable.ic_media_play, actionLabel, pi)
        if (SmartAlertPreferences.alertSoundStyle.value != AlertSoundStyle.SILENT) {
            builder.setVibrate(longArrayOf(0, 300, 200, 300))
        }
        manager.notify(id, builder.build())
    }
}
