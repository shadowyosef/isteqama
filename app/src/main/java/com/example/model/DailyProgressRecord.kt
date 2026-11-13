package com.example.model

enum class ActivityCategory(val id: String, val labelAr: String, val labelEn: String) {
    ALL("all", "الكل", "All"),
    PRAYER("prayer", "الصلوات", "Prayers"),
    QURAN("quran", "القرآن الكريم", "Quran"),
    TASBEEH("tasbeeh", "التسبيح والذكر", "Tasbeeh"),
    LOCK_ROUTINE("lock", "الأقفال والروتين", "Routines")
}

data class TinyActivityItem(
    val id: String = "${System.currentTimeMillis()}_${(100..999).random()}",
    val timestampMillis: Long = System.currentTimeMillis(),
    val category: ActivityCategory,
    val titleAr: String,
    val titleEn: String,
    val detailsAr: String = "",
    val detailsEn: String = "",
    val numericValue: Int = 0
)

data class DailyProgressRecord(
    val dateKey: String,
    val prayersMarked: Map<String, PrayerDailyRecord> = emptyMap(),
    val quranMinutes: Int = 0,
    val ayahsRead: Int = 0,
    val surahsRead: List<Int> = emptyList(),
    val tasbeehCount: Int = 0,
    val locksCompleted: Int = 0,
    val activities: List<TinyActivityItem> = emptyList()
) {
    val totalObligatoryPrayed: Int
        get() = prayersMarked.values.count { it.isPrayed && it.status != PrayerStatus.NOT_PRAYED }

    val hasAnyProgress: Boolean
        get() = totalObligatoryPrayed > 0 || ayahsRead > 0 || tasbeehCount > 0 || locksCompleted > 0 || quranMinutes > 0
}
