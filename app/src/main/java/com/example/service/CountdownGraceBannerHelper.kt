package com.example.service

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.ui.lock.SystemOverlayWindowManager
import com.example.util.AppStrings

object CountdownGraceBannerHelper {

    fun createCountdownBanner(
        context: Context,
        appName: String,
        onFinishedOrDismiss: () -> Unit
    ): View {
        var secondsLeft = 30
        val handler = Handler(Looper.getMainLooper())

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 20, 28, 20)
            val bg = GradientDrawable().apply {
                setColor(0xF01A1005.toInt())
                cornerRadius = 28f
                setStroke(2, 0xFFE5A93C.toInt())
            }
            background = bg
        }

        val titleTv = TextView(context).apply {
            text = if (AppStrings.isArabic) "مهلة استدراك قبل قفل الشاشة" else "Grace Period Before Lock"
            setTextColor(0xFFFFC107.toInt())
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        root.addView(titleTv)

        val countdownTv = TextView(context).apply {
            text = if (AppStrings.isArabic) "متبقي $secondsLeft ثانية لتطبيق ($appName)" else "$secondsLeft seconds left for ($appName)"
            setTextColor(Color.WHITE)
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 10)
        }
        root.addView(countdownTv)

        val jumpBtn = Button(context).apply {
            text = if (AppStrings.isArabic) "بدء الورد وتجنب القفل 📖" else "Start Ward Now 📖"
            setTextColor(Color.WHITE)
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            val btnBg = GradientDrawable().apply {
                setColor(0xFF1E5E45.toInt())
                cornerRadius = 20f
            }
            background = btnBg
            setOnClickListener {
                handler.removeCallbacksAndMessages(null)
                SystemOverlayWindowManager.launchOverlay(context)
                onFinishedOrDismiss()
            }
        }
        root.addView(jumpBtn)

        val runnable = object : Runnable {
            override fun run() {
                secondsLeft--
                if (secondsLeft <= 0) {
                    countdownTv.text = if (AppStrings.isArabic) "انتهت المهلة - بدء القفل!" else "Time expired - Locking!"
                    SystemOverlayWindowManager.launchOverlay(context)
                    onFinishedOrDismiss()
                } else {
                    countdownTv.text = if (AppStrings.isArabic) "متبقي $secondsLeft ثانية لتطبيق ($appName)" else "$secondsLeft seconds left for ($appName)"
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.postDelayed(runnable, 1000)

        return root
    }
}
