package com.example.ui.quran

import android.content.Context
import android.content.Intent
import android.graphics.*
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object QuranCardShareHelper {

    fun shareAyahAsText(context: Context, ayahText: String, surahName: String, ayahNumber: Int) {
        val shareBody = "﷽\n\"$ayahText\"\n[$surahName: آية $ayahNumber]\n\nتمت المشاركة من تطبيق الاستقامة"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "$surahName - $ayahNumber")
            putExtra(Intent.EXTRA_TEXT, shareBody)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "مشاركة الآية الكريمة").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun shareAyahAsImageCard(context: Context, ayahText: String, surahName: String, ayahNumber: Int) {
        shareOrnateStoryCard(context, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", ayahText, "﴿ $surahName : $ayahNumber ﴾")
    }

    fun shareOrnateStoryCard(context: Context, headerText: String, mainText: String, footerText: String) {
        try {
            val w = 1080
            val h = 1350
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val bgPaint = Paint().apply {
                shader = LinearGradient(0f, 0f, 0f, h.toFloat(), Color.parseColor("#072216"), Color.parseColor("#0D3B26"), Shader.TileMode.CLAMP)
            }
            canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

            val outerPaint = Paint().apply { color = Color.parseColor("#D4AF37"); style = Paint.Style.STROKE; strokeWidth = 10f; isAntiAlias = true }
            val innerPaint = Paint().apply { color = Color.parseColor("#80D4AF37"); style = Paint.Style.STROKE; strokeWidth = 4f; isAntiAlias = true }
            canvas.drawRoundRect(RectF(42f, 42f, w - 42f, h - 42f), 44f, 44f, outerPaint)
            canvas.drawRoundRect(RectF(66f, 66f, w - 66f, h - 66f), 32f, 32f, innerPaint)

            listOf(92f to 92f, w - 92f to 92f, 92f to h - 92f, w - 92f to h - 92f).forEach { (cx, cy) ->
                drawCanvasKhatamStar(canvas, cx, cy, 24f, outerPaint)
            }

            val goldPaint = Paint().apply { color = Color.parseColor("#F5D061"); textSize = 42f; textAlign = Paint.Align.CENTER; isAntiAlias = true }
            val bodyPaint = Paint().apply { color = Color.WHITE; textSize = 50f; textAlign = Paint.Align.CENTER; isAntiAlias = true }
            val brandPaint = Paint().apply { color = Color.parseColor("#A5D6A7"); textSize = 32f; textAlign = Paint.Align.CENTER; isAntiAlias = true }

            canvas.drawText(headerText, w / 2f, 220f, goldPaint)
            canvas.drawLine(w * 0.25f, 265f, w * 0.75f, 265f, innerPaint)

            val lines = wrapText(mainText, 27)
            var y = (h / 2f) - ((lines.size - 1) * 42f)
            lines.take(9).forEach { line ->
                canvas.drawText(line, w / 2f, y, bodyPaint)
                y += 84f
            }

            canvas.drawText(footerText, w / 2f, h - 195f, goldPaint)
            canvas.drawText("تطبيق الاستقامة • Istaqama", w / 2f, h - 120f, brandPaint)

            val cachePath = File(context.cacheDir, "images").apply { mkdirs() }
            val file = File(cachePath, "story_card_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "مشاركة البطاقة المزخرفة").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: Exception) {
            shareAyahAsText(context, mainText, footerText, 1)
        }
    }

    private fun wrapText(text: String, maxChars: Int): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        text.split(" ").forEach { w ->
            if (current.length + w.length > maxChars) {
                result.add(current.toString().trim())
                current = StringBuilder()
            }
            current.append(w).append(" ")
        }
        if (current.isNotEmpty()) result.add(current.toString().trim())
        return result
    }

    private fun drawCanvasKhatamStar(canvas: Canvas, cx: Float, cy: Float, r: Float, paint: Paint) {
        canvas.drawRect(cx - r * 0.7f, cy - r * 0.7f, cx + r * 0.7f, cy + r * 0.7f, paint)
        canvas.save()
        canvas.rotate(45f, cx, cy)
        canvas.drawRect(cx - r * 0.7f, cy - r * 0.7f, cx + r * 0.7f, cy + r * 0.7f, paint)
        canvas.restore()
    }
}
