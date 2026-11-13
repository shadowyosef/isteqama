package com.example.model

data class Ayah(
    val globalNumber: Int,
    val surahNumber: Int,
    val numberInSurah: Int,
    val textArabic: String,
    val tafseer: String,
    val juz: Int,
    val page: Int
) {
    val audioUrl: String
        get() = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/$globalNumber.mp3"
    val textUthmani: String
        get() = textArabic
}
