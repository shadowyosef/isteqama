package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.service.LockOverlayService
import com.example.util.LockPersistenceManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED || action.contains("QUICKBOOT")) {
            try {
                LockOverlayService.startService(context)
            } catch (_: Exception) {}
            if (LockPersistenceManager.isLockActive(context)) {
                LockPersistenceManager.enforceLockReopen(context)
            }
        }
    }
}
