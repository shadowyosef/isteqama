package com.example.util

import com.example.data.QuranRepository

data class HifzChallengeData(
    val surahName: String,
    val ayahNumber: Int,
    val words: List<String>,
    val blankIndices: List<Int>,
    val candidates: List<String>
)

object HifzChallengeHelper {
    fun buildChallenge(surahIds: List<Int>): HifzChallengeData {
        val poolSurahs = surahIds.ifEmpty { listOf(1, 67, 112, 113, 114) }
        val candidatesAyahs = poolSurahs.flatMap { sId ->
            val sInfo = QuranRepository.getSurah(sId)
            QuranRepository.getAyahsForSurah(sId)
                .map { Triple(sInfo?.nameAr ?: "سورة $sId", it.numberInSurah, it.textArabic.split(Regex("\\s+")).filter { w -> w.length >= 2 }) }
                .filter { it.third.size >= 4 }
        }
        val chosen = candidatesAyahs.randomOrNull() ?: Triple(
            "الفاتحة", 2, listOf("الْحَمْدُ", "لِلَّهِ", "رَبِّ", "الْعَالَمِينَ")
        )
        val words = chosen.third
        val firstBlank = (1 until words.lastIndex).random()
        val secondBlank = ((firstBlank + 1)..words.lastIndex).randomOrNull()?.takeIf { it != firstBlank } ?: 0
        val blanks = listOf(firstBlank, secondBlank).distinct().sorted()
        val missingWords = blanks.map { words[it] }
        val distractors = listOf("الرَّحْمَٰنِ", "الْعَظِيمِ", "الْمُبِينِ", "الْحَكِيمِ", "السَّمِيعُ", "الْغَفُورُ")
            .filter { it !in missingWords }
            .shuffled()
            .take(3)
        return HifzChallengeData(
            surahName = chosen.first,
            ayahNumber = chosen.second,
            words = words,
            blankIndices = blanks,
            candidates = (missingWords + distractors).distinct().shuffled()
        )
    }
}
