package com.example.data

import com.example.model.PrayerLocation

object PrayerPresetCities {
    // Primary default is Jordan - Russeifa (الأردن - الرصيفة)
    val defaultLocation = PrayerLocation(
        nameAr = "الرصيفة",
        nameEn = "Russeifa",
        latitude = 32.0178,
        longitude = 36.0463,
        countryAr = "الأردن",
        countryEn = "Jordan",
        timezoneHours = 3.0
    )

    val jordanGovernorates: List<PrayerLocation> = listOf(
        defaultLocation,
        PrayerLocation("عمّان", "Amman", 31.9539, 35.9106, "الأردن", "Jordan", 3.0),
        PrayerLocation("الزرقاء", "Zarqa", 32.0728, 36.0880, "الأردن", "Jordan", 3.0),
        PrayerLocation("إربد", "Irbid", 32.5568, 35.8469, "الأردن", "Jordan", 3.0),
        PrayerLocation("العقبة", "Aqaba", 29.5321, 35.0063, "الأردن", "Jordan", 3.0),
        PrayerLocation("السلط", "Salt", 32.0392, 35.7272, "الأردن", "Jordan", 3.0),
        PrayerLocation("مأدبا", "Madaba", 31.7197, 35.7941, "الأردن", "Jordan", 3.0),
        PrayerLocation("المفرق", "Mafraq", 32.3429, 36.2081, "الأردن", "Jordan", 3.0),
        PrayerLocation("جرش", "Jerash", 32.2747, 35.8961, "الأردن", "Jordan", 3.0),
        PrayerLocation("عجلون", "Ajloun", 32.3326, 35.7517, "الأردن", "Jordan", 3.0),
        PrayerLocation("الكرك", "Karak", 31.1853, 35.7048, "الأردن", "Jordan", 3.0),
        PrayerLocation("معان", "Ma'an", 30.1927, 35.7360, "الأردن", "Jordan", 3.0),
        PrayerLocation("الطفيلة", "Tafilah", 30.8375, 35.6044, "الأردن", "Jordan", 3.0)
    )

    val presets: List<PrayerLocation> = jordanGovernorates
}
