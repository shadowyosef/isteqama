package com.example.model

enum class OccasionCategory(val labelAr: String, val labelEn: String) {
    DAILY_LIFE("الحياة اليومية", "Daily Life"),
    TRAVEL("السفر والتنقل", "Travel"),
    MOSQUE("المسجد والصلاة", "Mosque & Prayer"),
    WEATHER("المطر والرياح", "Weather & Nature"),
    DISTRESS("الهم والكرب", "Relief & Distress")
}

data class OccasionAdhkarItem(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val textAr: String,
    val textEn: String,
    val virtue: String,
    val category: OccasionCategory
)
