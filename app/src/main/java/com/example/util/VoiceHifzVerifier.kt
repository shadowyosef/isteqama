package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

data class HifzWordResult(val originalWord: String, val isMatched: Boolean)

object VoiceHifzVerifier {
    private var recognizer: SpeechRecognizer? = null

    fun normalizeArabic(input: String): String {
        return input
            .replace(Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED\\u0640]"), "")
            .replace("أ", "ا").replace("إ", "ا").replace("آ", "ا").replace("ٱ", "ا")
            .replace("ة", "ه").replace("ى", "ي")
            .replace(Regex("[^\\p{IsArabic}a-zA-Z0-9\\s]"), " ")
            .trim().replace(Regex("\\s+"), " ")
    }

    fun evaluateRecitation(expectedAyah: String, spokenText: String): Pair<List<HifzWordResult>, Int> {
        val expectedTokens = expectedAyah.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (expectedTokens.isEmpty()) return emptyList<HifzWordResult>() to 0
        val spokenSet = normalizeArabic(spokenText).split(" ").filter { it.isNotBlank() }.toSet()
        val results = expectedTokens.map { word ->
            val clean = normalizeArabic(word)
            val ok = clean.isNotEmpty() && (clean in spokenSet || spokenSet.any { it.contains(clean) || clean.contains(it) })
            HifzWordResult(word, ok)
        }
        val pct = (results.count { it.isMatched } * 100) / results.size
        return results to pct
    }

    fun startListening(
        context: Context,
        onResult: (String) -> Unit,
        onListeningChanged: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError(AppStrings.get("التعرف الصوتي غير مدعوم على هذا الجهاز", "Speech recognition not available"))
            return
        }
        stop()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) = onListeningChanged(true)
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() = onListeningChanged(false)
                override fun onError(error: Int) {
                    onListeningChanged(false)
                    onError(AppStrings.get("لم يتم التقاط الصوت بوضوح، حاول مجدداً ($error)", "Voice error ($error), try again"))
                }
                override fun onResults(results: Bundle?) {
                    onListeningChanged(false)
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    matches?.firstOrNull()?.let(onResult)
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    matches?.firstOrNull()?.let(onResult)
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        recognizer?.startListening(intent)
    }

    fun stop() {
        try { recognizer?.stopListening(); recognizer?.destroy() } catch (_: Exception) {}
        recognizer = null
    }
}
