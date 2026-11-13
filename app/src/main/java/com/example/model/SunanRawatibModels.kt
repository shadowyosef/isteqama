package com.example.model

enum class SunnahType(val id: String, val nameAr: String, val nameEn: String, val rakahs: Int) {
    FAJR_BEFORE("fajr_pre", "سنة الفجر القبلية", "Fajr Sunnah", 2),
    DHUHR_BEFORE("dhuhr_pre", "سنة الظهر القبلية", "Dhuhr Pre-Sunnah", 4),
    DHUHR_AFTER("dhuhr_post", "سنة الظهر البعدية", "Dhuhr Post-Sunnah", 2),
    MAGHRIB_AFTER("maghrib_post", "سنة المغرب البعدية", "Maghrib Sunnah", 2),
    ISHA_AFTER("isha_post", "سنة العشاء البعدية", "Isha Sunnah", 2),
    DUHA("duha", "صلاة الضحى", "Duha Prayer", 2),
    WITR("witr", "صلاة الوتر", "Witr Prayer", 3),
    QIYAM("qiyam", "قيام الليل والتهجد", "Night Prayer", 2)
}

data class SunnahDailyItem(
    val type: SunnahType,
    val isDone: Boolean = false,
    val timestampMillis: Long = 0L
)
