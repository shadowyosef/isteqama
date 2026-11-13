package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.service.LockOverlayService
import com.example.util.LockPersistenceManager

class LockWatchdogReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (LockPersistenceManager.isLockActive(context) && !com.example.util.LockScheduleTracker.isGlobalCooldownActive(context)) {
            try {
                LockOverlayService.startService(context)
            } catch (_: Exception) {}
            LockPersistenceManager.enforceLockReopen(context)
        }
    }
}
