package com.example.service

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.Build

object AutoSilentPrayerManager {
    private const val PREFS = "auto_silent_prayer_prefs"

    fun isAutoSilentEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean("enabled", false)
    }

    fun setAutoSilentEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean("enabled", enabled).apply()
    }

    fun getDurationMinutes(context: Context): Int {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt("duration_mins", 20)
    }

    fun setDurationMinutes(context: Context, mins: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putInt("duration_mins", mins).apply()
    }

    fun applySilentMode(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (notificationManager?.isNotificationPolicyAccessGranted == true) {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                } else {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                }
            } else {
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
            }
        } catch (_: Exception) {
            audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
        }
    }

    fun restoreNormalMode(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        try {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        } catch (_: Exception) {}
    }
}
