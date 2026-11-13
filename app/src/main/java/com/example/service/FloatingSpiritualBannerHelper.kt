package com.example.service

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.ui.lock.SystemOverlayWindowManager
import com.example.util.AppStrings
import kotlinx.coroutines.*

object FloatingSpiritualBannerHelper {

    fun createFloatingAlertBanner(
        context: Context,
        appName: String,
        onDismiss: () -> Unit
    ): View {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 20, 28, 20)
            val bg = GradientDrawable().apply {
                setColor(0xF00A1F18.toInt())
                cornerRadius = 28f
                setStroke(2, 0xFFD4AF37.toInt())
            }
            background = bg
        }

        val titleTv = TextView(context).apply {
            text = if (AppStrings.isArabic) "استقامة: تذكير إيماني لحفظ الوقت" else "Istaqama: Time Preservation Alert"
            setTextColor(0xFFD4AF37.toInt())
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        root.addView(titleTv)

        val descTv = TextView(context).apply {
            text = if (AppStrings.isArabic) "أنت تحاول فتح تطبيق ($appName). تذكر وردك اليومي أولاً."
                   else "Opening ($appName). Remember your spiritual ward first."
            setTextColor(Color.WHITE)
            textSize = 13f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 12)
        }
        root.addView(descTv)

        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val jumpBtn = Button(context).apply {
            text = if (AppStrings.isArabic) "إتمام الورد الآن 📖" else "Start Ward Now 📖"
            setTextColor(Color.WHITE)
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            val btnBg = GradientDrawable().apply {
                setColor(0xFF1E5E45.toInt())
                cornerRadius = 20f
            }
            background = btnBg
            setOnClickListener {
                SystemOverlayWindowManager.launchOverlay(context)
                onDismiss()
            }
        }
        row.addView(jumpBtn)

        val dismissBtn = TextView(context).apply {
            text = if (AppStrings.isArabic) "إغلاق ✕" else "Dismiss ✕"
            setTextColor(0xFFAAAAAA.toInt())
            textSize = 12f
            setPadding(20, 10, 10, 10)
            setOnClickListener { onDismiss() }
        }
        row.addView(dismissBtn)

        root.addView(row)
        return root
    }
}
