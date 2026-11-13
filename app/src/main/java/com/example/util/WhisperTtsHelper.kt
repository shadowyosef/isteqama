package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

object WhisperTtsHelper {
    private const val PREFS = "whisper_tts_prefs"
    private const val KEY_GLOBAL_ENABLED = "whisper_global_enabled"
    private var tts: TextToSpeech? = null
    private var isReady = false

    fun isGlobalEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_GLOBAL_ENABLED, false)

    fun setGlobalEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_GLOBAL_ENABLED, enabled).apply()
    }

    fun speakSoftly(context: Context, text: String, forceForItem: Boolean = false) {
        if (!forceForItem && !isGlobalEnabled(context)) return
        val cleanText = text.replace(Regex("[۝﴿﴾]"), "").trim()
        if (cleanText.isBlank()) return
        try {
            if (tts == null) {
                tts = TextToSpeech(context.applicationContext) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        isReady = true
                        configureAndSpeak(cleanText)
                    }
                }
            } else if (isReady) {
                configureAndSpeak(cleanText)
            }
        } catch (_: Exception) {}
    }

    private fun configureAndSpeak(text: String) {
        val engine = tts ?: return
        val loc = if (AppStrings.isArabic) Locale("ar") else Locale.US
        engine.language = loc
        engine.setPitch(0.9f)
        engine.setSpeechRate(0.85f)
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "whisper_${System.currentTimeMillis()}")
    }
}
