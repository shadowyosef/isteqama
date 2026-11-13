package com.example.model

enum class PrayerType(val id: String, val nameAr: String, val nameEn: String) {
    FAJR("fajr", "الفجر", "Fajr"),
    SUNRISE("sunrise", "الشروق", "Sunrise"),
    DHUHR("dhuhr", "الظهر", "Dhuhr"),
    ASR("asr", "العصر", "Asr"),
    MAGHRIB("maghrib", "المغرب", "Maghrib"),
    ISHA("isha", "العشاء", "Isha");

    val isObligatoryPrayer: Boolean
        get() = this != SUNRISE
}

data class PrayerTimeItem(
    val type: PrayerType,
    val timeFormatted: String,
    val hour: Int,
    val minute: Int,
    val isPassed: Boolean = false,
    val isNext: Boolean = false,
    val isPrayed: Boolean = false
)

data class PrayerLocation(
    val nameAr: String,
    val nameEn: String,
    val latitude: Double,
    val longitude: Double,
    val countryAr: String = "الأردن",
    val countryEn: String = "Jordan",
    val timezoneHours: Double = 3.0,
    val isAutoGps: Boolean = false
)

data class PrayerAdjustments(
    val fajrOffset: Int = 0,
    val sunriseOffset: Int = 0,
    val dhuhrOffset: Int = 0,
    val asrOffset: Int = 0,
    val maghribOffset: Int = 0,
    val ishaOffset: Int = 0
) {
    fun getOffset(type: PrayerType): Int = when (type) {
        PrayerType.FAJR -> fajrOffset
        PrayerType.SUNRISE -> sunriseOffset
        PrayerType.DHUHR -> dhuhrOffset
        PrayerType.ASR -> asrOffset
        PrayerType.MAGHRIB -> maghribOffset
        PrayerType.ISHA -> ishaOffset
    }
}
