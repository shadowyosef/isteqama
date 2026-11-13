package com.example.data

import com.example.model.PrayerLocation

object PrayerLocationsDatabase {
    val arabCountriesAndCities: List<PrayerLocation> = PrayerPresetCities.jordanGovernorates + listOf(
        // فلسطين
        PrayerLocation("القدس الشريف", "Jerusalem", 31.7683, 35.2137, "فلسطين", "Palestine", 3.0),
        PrayerLocation("غزة", "Gaza", 31.5017, 34.4668, "فلسطين", "Palestine", 3.0),
        PrayerLocation("رام الله", "Ramallah", 31.9038, 35.2034, "فلسطين", "Palestine", 3.0),
        PrayerLocation("الخليل", "Hebron", 31.5326, 35.0998, "فلسطين", "Palestine", 3.0),
        PrayerLocation("نابلس", "Nablus", 32.2227, 35.2621, "فلسطين", "Palestine", 3.0),
        // السعودية
        PrayerLocation("مكة المكرمة", "Makkah", 21.4225, 39.8262, "السعودية", "Saudi Arabia", 3.0),
        PrayerLocation("المدينة المنورة", "Madinah", 24.5247, 39.5692, "السعودية", "Saudi Arabia", 3.0),
        PrayerLocation("الرياض", "Riyadh", 24.7136, 46.6753, "السعودية", "Saudi Arabia", 3.0),
        PrayerLocation("جدة", "Jeddah", 21.4858, 39.1925, "السعودية", "Saudi Arabia", 3.0),
        PrayerLocation("الدمام", "Dammam", 26.4207, 50.0888, "السعودية", "Saudi Arabia", 3.0),
        // مصر
        PrayerLocation("القاهرة", "Cairo", 30.0444, 31.2357, "مصر", "Egypt", 2.0),
        PrayerLocation("الإسكندرية", "Alexandria", 31.2001, 29.9187, "مصر", "Egypt", 2.0),
        PrayerLocation("الجيزة", "Giza", 30.0131, 31.2089, "مصر", "Egypt", 2.0),
        PrayerLocation("طنطا", "Tanta", 30.7865, 31.0004, "مصر", "Egypt", 2.0),
        // سوريا
        PrayerLocation("دمشق", "Damascus", 33.5138, 36.2765, "سوريا", "Syria", 3.0),
        PrayerLocation("حلب", "Aleppo", 36.2021, 37.1343, "سوريا", "Syria", 3.0),
        // الإمارات
        PrayerLocation("دبي", "Dubai", 25.2048, 55.2708, "الإمارات", "UAE", 4.0),
        PrayerLocation("أبوظبي", "Abu Dhabi", 24.4539, 54.3773, "الإمارات", "UAE", 4.0),
        // دول عربية أخرى
        PrayerLocation("بيروت", "Beirut", 33.8938, 35.5018, "لبنان", "Lebanon", 3.0),
        PrayerLocation("بغداد", "Baghdad", 33.3152, 44.3661, "العراق", "Iraq", 3.0),
        PrayerLocation("الكويت", "Kuwait City", 29.3759, 47.9774, "الكويت", "Kuwait", 3.0),
        PrayerLocation("الدوحة", "Doha", 25.2854, 51.5310, "قطر", "Qatar", 3.0),
        PrayerLocation("المنامة", "Manama", 26.2285, 50.5860, "البحرين", "Bahrain", 3.0),
        PrayerLocation("مسقط", "Muscat", 23.5880, 58.3829, "عُمان", "Oman", 4.0)
    )

    fun search(query: String): List<PrayerLocation> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return arabCountriesAndCities
        return arabCountriesAndCities.filter { loc ->
            loc.nameAr.lowercase().contains(q) ||
            loc.nameEn.lowercase().contains(q) ||
            loc.countryAr.lowercase().contains(q) ||
            loc.countryEn.lowercase().contains(q)
        }
    }
}
