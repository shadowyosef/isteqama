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
import com.example.data.TasbeehRepository

object FloatingOverlayViewHelper {

    fun createInteractiveWidget(
        context: Context,
        onCount: () -> Unit,
        onNext: () -> Unit,
        onClose: () -> Unit
    ): View {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 20, 24, 20)
            val bg = GradientDrawable().apply {
                setColor(0xEE0A1F18.toInt())
                cornerRadius = 32f
                setStroke(2, 0xFFD4AF37.toInt())
            }
            background = bg
        }

        val item = TasbeehRepository.getCurrentItem()

        val titleTv = TextView(context).apply {
            text = item.phraseAr
            setTextColor(0xFFD4AF37.toInt())
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        root.addView(titleTv)

        val countBtn = Button(context).apply {
            text = "${item.count} / ${item.target}"
            setTextColor(Color.WHITE)
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            val btnBg = GradientDrawable().apply {
                setColor(0xFF1E5E45.toInt())
                cornerRadius = 24f
            }
            background = btnBg
            setOnClickListener {
                TasbeehRepository.increment()
                val updated = TasbeehRepository.getCurrentItem()
                text = "${updated.count} / ${updated.target}"
                titleTv.text = updated.phraseAr
                onCount()
            }
        }
        root.addView(countBtn)

        val actionsLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 0)
        }

        val nextBtn = TextView(context).apply {
            text = "التالي ⏭"
            setTextColor(Color.WHITE)
            textSize = 12f
            setPadding(16, 8, 16, 8)
            setOnClickListener {
                val nextIdx = (TasbeehRepository.selectedIndex.value + 1) % TasbeehRepository.phrases.value.size
                TasbeehRepository.selectedIndex.value = nextIdx
                val updated = TasbeehRepository.getCurrentItem()
                titleTv.text = updated.phraseAr
                countBtn.text = "${updated.count} / ${updated.target}"
                onNext()
            }
        }
        actionsLayout.addView(nextBtn)

        val closeBtn = TextView(context).apply {
            text = "إغلاق ✕"
            setTextColor(0xFFFF7043.toInt())
            textSize = 12f
            setPadding(16, 8, 16, 8)
            setOnClickListener { onClose() }
        }
        actionsLayout.addView(closeBtn)

        root.addView(actionsLayout)
        return root
    }
}
