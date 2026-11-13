package com.example.model

data class TasbeehPhraseItem(
    val phraseAr: String,
    val phraseEn: String = "",
    val targetCount: Int = 33
) {
    fun serialize(): String {
        val safeAr = phraseAr.replace("~", "-").replace(";", "-")
        val safeEn = phraseEn.replace("~", "-").replace(";", "-")
        return "$safeAr~$safeEn~$targetCount"
    }

    companion object {
        fun deserialize(raw: String): TasbeehPhraseItem {
            val parts = raw.split("~")
            val ar = parts.getOrNull(0) ?: ""
            val en = parts.getOrNull(1) ?: ar
            val count = parts.getOrNull(2)?.toIntOrNull() ?: 33
            return TasbeehPhraseItem(ar, en, count)
        }
    }
}
