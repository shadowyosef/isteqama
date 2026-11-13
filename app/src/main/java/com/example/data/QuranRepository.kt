package com.example.data

import com.example.model.Ayah
import com.example.model.SurahInfo

object QuranRepository {
    fun getAllSurahs(): List<SurahInfo> = QuranMetadataPart2.allSurahs

    fun getSurah(surahNumber: Int): SurahInfo? =
        getAllSurahs().find { it.number == surahNumber }

    fun getAyahsForSurah(surahNumber: Int): List<Ayah> =
        QuranDataProvider.getSurahAyahs(surahNumber)

    fun getAyah(surahNumber: Int, ayahNumber: Int): Ayah? =
        getAyahsForSurah(surahNumber).find { it.numberInSurah == ayahNumber }

    fun searchSurahs(query: String): List<SurahInfo> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return getAllSurahs()
        return getAllSurahs().filter {
            it.nameAr.contains(q) || it.nameEn.lowercase().contains(q) ||
                    it.number.toString() == q
        }
    }

    fun getJuzNumber(page: Int): Int = ((page - 1) / 20) + 1

    fun getAyahsForPage(page: Int): List<Ayah> =
        QuranDataProvider.getAyahsForPage(page)
}
