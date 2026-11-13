package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quran_data")
data class QuranDataEntity(
    @PrimaryKey
    val globalAyahNumber: Int,
    val surahNumber: Int,
    val ayahNumberInSurah: Int,
    val textArabic: String,
    val tafseer: String,
    val juz: Int,
    val page: Int,
    val isBookmarked: Boolean = false
)
