package com.example.service

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.example.data.TasbeehRepository

object FloatingOverlayBubbleHelper {

    fun createBubble(
        context: Context,
        onTap: () -> Unit
    ): View {
        val root = FrameLayout(context).apply {
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(0xEE0F382C.toInt())
                setStroke(3, 0xFFD4AF37.toInt())
            }
            background = bg
        }

        val item = TasbeehRepository.getCurrentItem()
        val tv = TextView(context).apply {
            text = "${item.count}"
            setTextColor(Color.WHITE)
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        val params = FrameLayout.LayoutParams(130, 130).apply {
            gravity = Gravity.CENTER
        }
        root.addView(tv, params)

        root.setOnClickListener {
            TasbeehRepository.increment()
            val updated = TasbeehRepository.getCurrentItem()
            tv.text = "${updated.count}"
            onTap()
        }

        return root
    }
}
