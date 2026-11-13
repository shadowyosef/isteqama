package com.example.ui.lock

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.data.AppPreferencesManager

object LockKioskHelper {

    fun setupLockWindow(activity: Activity) {
        val mode = AppPreferencesManager.lockEnforcementMode.value
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (mode == "SYSTEM_OVERLAY") {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            activity.window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            WindowCompat.setDecorFitsSystemWindows(activity.window, true)
        } else {
            WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            activity.setShowWhenLocked(true)
            activity.setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            activity.window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        if (mode != "SYSTEM_OVERLAY") hideSystemBars(activity)
    }

    fun hideSystemBars(activity: Activity) {
        if (AppPreferencesManager.lockEnforcementMode.value == "SYSTEM_OVERLAY") return
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }

    fun startKioskLock(activity: Activity) {
        if (AppPreferencesManager.lockEnforcementMode.value == "NATIVE_PINNING") {
            try { activity.startLockTask() } catch (_: Exception) {}
        }
    }

    fun stopKioskLock(activity: Activity) {
        if (AppPreferencesManager.lockEnforcementMode.value == "NATIVE_PINNING") {
            try { activity.stopLockTask() } catch (_: Exception) {}
        }
    }
}
