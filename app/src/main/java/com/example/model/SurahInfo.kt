package com.example.model

data class SurahInfo(
    val number: Int,
    val nameAr: String,
    val nameEn: String,
    val versesCount: Int,
    val revelationType: String, // Meccan or Medinan
    val pageStart: Int,
    val pageEnd: Int
) {
    val displayNameAr: String get() = "سورة $nameAr"
    val displayNameEn: String get() = "Surah $nameEn"
}
