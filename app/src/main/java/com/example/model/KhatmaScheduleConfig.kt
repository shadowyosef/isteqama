package com.example.model

enum class KhatmaDistributionMode(val id: String, val labelAr: String, val labelEn: String) {
    INTERVAL_HOURLY("hourly", "كل فترة زمنية (ساعة/ساعتان)", "Hourly Interval"),
    PRAYER_TIMES("prayers", "دبر كل صلاة مكتوبة (5 صلوات)", "After Prayers"),
    CUSTOM_SESSIONS("sessions", "عدد جلسات محدد باليوم", "Custom Sessions")
}

data class KhatmaLockPlan(
    val targetDays: Int = 30,
    val startHour: Int = 9,
    val endHour: Int = 23,
    val distributionMode: KhatmaDistributionMode = KhatmaDistributionMode.INTERVAL_HOURLY,
    val intervalMinutes: Int = 60,
    val customSessionsCount: Int = 10,
    val totalPages: Int = 604,
    val pagesPerDay: Int = 20,
    val pagesPerSession: Int = 2,
    val scheduleTimes: List<Pair<Int, Int>> = emptyList()
) {
    val activeHoursCount: Int
        get() = ((endHour - startHour + 24) % 24).coerceAtLeast(1)

    val restHoursCount: Int
        get() = (24 - activeHoursCount).coerceAtLeast(0)
}
