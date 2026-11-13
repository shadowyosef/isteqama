package com.example.model

enum class MissedPrayerType(val labelAr: String, val labelEn: String) {
    FAJR("صلاة الفجر", "Fajr"),
    DHUHR("صلاة الظهر", "Dhuhr"),
    ASR("صلاة العصر", "Asr"),
    MAGHRIB("صلاة المغرب", "Maghrib"),
    ISHA("صلاة العشاء", "Isha")
}

data class MissedPrayerRecord(
    val type: MissedPrayerType,
    val count: Int = 0
)
