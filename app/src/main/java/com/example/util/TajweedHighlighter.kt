package com.example.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight

object TajweedHighlighter {
    private val colorMadd = Color(0xFF00897B)       // Teal-Emerald for Madd
    private val colorGhunnah = Color(0xFFD97706)    // Warm Amber-Gold for Ghunnah/Shaddah
    private val colorQalqalah = Color(0xFFD81B60)   // Rose-Coral for Qalqalah

    private val qalqalahLetters = setOf('ق', 'ط', 'ب', 'ج', 'د')
    private val diacritics = setOf('\u064E', '\u064F', '\u0650', '\u0651', '\u0652', '\u064B', '\u064C', '\u064D', '\u0670', '\u0653')

    fun highlight(text: String, enabled: Boolean): AnnotatedString {
        if (!enabled) return AnnotatedString(text)
        return buildAnnotatedString {
            append(text)
            var i = 0
            while (i < text.length) {
                val ch = text[i]
                val next1 = text.getOrNull(i + 1)
                val next2 = text.getOrNull(i + 2)
                when {
                    (ch == 'ن' || ch == 'م') && next1 == '\u0651' -> {
                        val end = (i + 2).coerceAtMost(text.length)
                        addStyle(SpanStyle(color = colorGhunnah, fontWeight = FontWeight.Bold), i, end)
                    }
                    ch in qalqalahLetters && next1 == '\u0652' -> {
                        val end = (i + 2).coerceAtMost(text.length)
                        addStyle(SpanStyle(color = colorQalqalah, fontWeight = FontWeight.Bold), i, end)
                    }
                    ch == 'آ' || ch == '\u0653' || (ch == '\u0670') || ((ch == 'ا' || ch == 'و' || ch == 'ي') && next1 == '\u0653') -> {
                        val end = if (next1 == '\u0653' && next2 != null) (i + 2) else (i + 1)
                        addStyle(SpanStyle(color = colorMadd, fontWeight = FontWeight.SemiBold), i, end.coerceAtMost(text.length))
                    }
                    ch == 'ن' && next1 == '\u0652' && (next2 in diacritics || next2 == ' ') -> {
                        addStyle(SpanStyle(color = colorGhunnah), i, (i + 2).coerceAtMost(text.length))
                    }
                }
                i++
            }
        }
    }

    fun maskWordsForHifz(text: String): String {
        val words = text.split(" ")
        if (words.size <= 2) return text
        return words.mapIndexed { idx, word ->
            if (idx % 2 == 1) "_____" else word
        }.joinToString(" ")
    }
}
