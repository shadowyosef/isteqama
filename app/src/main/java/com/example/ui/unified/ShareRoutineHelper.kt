package com.example.ui.unified

import android.content.Context
import android.content.Intent
import com.example.model.UnifiedRoutine

object ShareRoutineHelper {
    fun share(context: Context, routine: UnifiedRoutine) {
        val shareText = """
            ✨ روتين إيماني من تطبيق استقامة (Istaqama) ✨
            
            📌 ${routine.titleAr} (${routine.titleEn})
            🎯 الهدف: ${routine.targetCount}
            📖 التفاصيل: ${routine.detailsSummary}
            ⏰ موعد القفل والتنبيه: ${String.format("%02d:%02d", routine.triggerHour, routine.triggerMinute)}
            
            قال رسول الله ﷺ: «أَحَبُّ الأَعْمَالِ إِلَى اللهِ أَدْوَمُهَا وَإِنْ قَلَّ».
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, routine.titleAr)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        val chooser = Intent.createChooser(intent, "مشاركة الروتين الإيماني")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun shareText(context: Context, title: String, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "✨ استقامة (Istaqama) ✨\n\n$title\n$text")
        }
        val chooser = Intent.createChooser(intent, "مشاركة الورد الإيماني")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
