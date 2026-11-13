package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.DailyProgressRepository
import com.example.data.TasbeehRepository
import com.example.service.LockNotificationHelper

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_INCREMENT_TASBEEH) {
            DailyProgressRepository.init(context)
            TasbeehRepository.increment()
            LockNotificationHelper.updateNotification(context)
        }
    }

    companion object {
        const val ACTION_INCREMENT_TASBEEH = "com.example.action.NOTIF_INCREMENT_TASBEEH"
    }
}
