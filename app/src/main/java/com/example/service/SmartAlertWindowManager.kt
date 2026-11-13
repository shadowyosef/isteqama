package com.example.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.example.data.SmartAlertPreferences
import com.example.model.SmartAlertMode

object SmartAlertWindowManager {
    private var windowManager: WindowManager? = null
    private var currentBannerView: View? = null

    fun showSmartAlert(context: Context, appName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            return
        }
        if (currentBannerView != null) return

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager = wm

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 120
        }

        val mode = SmartAlertPreferences.smartAlertMode.value
        val bannerView = if (mode == SmartAlertMode.COUNTDOWN_GRACE) {
            CountdownGraceBannerHelper.createCountdownBanner(context, appName) { dismiss() }
        } else {
            FloatingSpiritualBannerHelper.createFloatingAlertBanner(context, appName) { dismiss() }
        }

        try {
            wm.addView(bannerView, params)
            currentBannerView = bannerView
        } catch (_: Exception) {}
    }

    fun dismiss() {
        currentBannerView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {}
        }
        currentBannerView = null
    }
}
