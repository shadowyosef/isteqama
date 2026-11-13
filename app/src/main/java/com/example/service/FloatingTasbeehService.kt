package com.example.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast

class FloatingTasbeehService : Service() {
    private var windowManager: WindowManager? = null
    private var floatingView: View? = null

    companion object {
        fun start(context: Context) {
            if (Settings.canDrawOverlays(context)) {
                context.startService(Intent(context, FloatingTasbeehService::class.java))
            } else {
                Toast.makeText(context, "يرجى تفعيل صلاحية الظهور فوق التطبيقات أولاً", Toast.LENGTH_SHORT).show()
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, FloatingTasbeehService::class.java))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 250
        }

        val view = if (FloatingOverlayPreferences.selectedStyle == FloatingOverlayStyle.COMPACT_BUBBLE) {
            FloatingOverlayBubbleHelper.createBubble(this, onTap = {})
        } else {
            FloatingOverlayViewHelper.createInteractiveWidget(
                this,
                onCount = {},
                onNext = {},
                onClose = { stopSelf() }
            )
        }
        floatingView = view

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager?.updateViewLayout(view, params)
                    true
                }
                else -> false
            }
        }

        try {
            windowManager?.addView(view, params)
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        floatingView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {}
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
