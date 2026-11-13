package com.example.util

import android.content.Context
import com.example.data.QuranRepository
import com.example.model.Ayah

object AudioSequenceHelper {

    fun getNextAyahTarget(surah: Int, ayah: Int, loopSurah: Boolean): Pair<Int, Ayah>? {
        val surahAyahs = QuranRepository.getAyahsForSurah(surah)
        return if (ayah < surahAyahs.size) {
            surah to surahAyahs[ayah]
        } else if (loopSurah && surahAyahs.isNotEmpty()) {
            surah to surahAyahs[0]
        } else if (surah < 114) {
            val nextSurahAyahs = QuranRepository.getAyahsForSurah(surah + 1)
            if (nextSurahAyahs.isNotEmpty()) (surah + 1) to nextSurahAyahs[0] else null
        } else null
    }

    fun getPrevAyahTarget(surah: Int, ayah: Int): Pair<Int, Ayah>? {
        val surahAyahs = QuranRepository.getAyahsForSurah(surah)
        return if (ayah > 1 && surahAyahs.size >= ayah - 1) {
            surah to surahAyahs[ayah - 2]
        } else if (surah > 1) {
            val prevAyahs = QuranRepository.getAyahsForSurah(surah - 1)
            if (prevAyahs.isNotEmpty()) (surah - 1) to prevAyahs.last() else null
        } else null
    }
}
