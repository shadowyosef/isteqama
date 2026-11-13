package com.example.service

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.model.TasbeehCardShape
import com.example.model.TasbeehScheduleModel
import com.example.model.TasbeehThemeColor
import com.example.util.AppStrings

object FloatingToastViewHelper {
    fun getStrokeColor(theme: TasbeehThemeColor): Int = when (theme) {
        TasbeehThemeColor.GOLD -> 0xFFD4AF37.toInt()
        TasbeehThemeColor.OCEAN -> 0xFF1976D2.toInt()
        TasbeehThemeColor.PURPLE -> 0xFF8E24AA.toInt()
        TasbeehThemeColor.ROSE -> 0xFFE91E63.toInt()
        else -> 0xFF2E7D32.toInt()
    }

    fun createToastView(context: Context, model: TasbeehScheduleModel, subText: TextView): View {
        val stroke = getStrokeColor(model.themeColor)
        val radius = if (model.cardShape == TasbeehCardShape.PILL) 999f else if (model.cardShape == TasbeehCardShape.SLEEK) 12f else 28f
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(32, 18, 32, 18)
            background = GradientDrawable().apply { setColor(0xF00A2E20.toInt()); cornerRadius = radius; setStroke(2, stroke) }
            addView(TextView(context).apply {
                text = if (AppStrings.isArabic) model.phraseAr else model.phraseEn
                setTextColor(Color.WHITE); textSize = 15f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER
            })
            addView(subText)
        }
    }
}
