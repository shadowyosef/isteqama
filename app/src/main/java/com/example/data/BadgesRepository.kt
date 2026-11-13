package com.example.data

data class WorshipBadge(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val descAr: String,
    val descEn: String,
    val earned: Boolean,
    val emoji: String
)

object BadgesRepository {
    fun getBadges(): List<WorshipBadge> {
        val stats = StatsRepository.stats.value
        val today = DailyProgressRepository.getRecord(DailyProgressRepository.todayKey())
        val streak = stats.currentStreak
        val totalDhikr = stats.totalDhikrCount.toLong() + TasbeehRepository.totalLifetimeCount.value
        val totalPages = stats.totalAyahsRead / 15

        return listOf(
            WorshipBadge("streak_7", "ثبات أسبوع", "7-Day Streak", "٧ أيام عبادة متواصلة", "7 consecutive days active", streak >= 7, "🔥"),
            WorshipBadge("streak_30", "شعار الاستقامة", "30-Day Streak", "٣٠ يوماً من المواظبة", "30 consecutive days", streak >= 30, "👑"),
            WorshipBadge("dhikr_1k", "ذاكر ١٠٠٠", "1K Dhikr", "إتمام ١٠٠٠ تسبيحة", "1,000+ total tasbeeh", totalDhikr >= 1000L, "📿"),
            WorshipBadge("dhikr_10k", "المُكثِر ١٠ آلاف", "10K Dhikr", "بلوغ ١٠,٠٠٠ تسبيحة", "10,000+ total tasbeeh", totalDhikr >= 10000L, "✨"),
            WorshipBadge("khatma_1", "صاحب ختمة", "Khatma Finisher", "قراءة ٦٠٤ صفحات قرآن", "604+ Quran pages read", totalPages >= 604, "📖"),
            WorshipBadge("prayers_5", "حافظ الفرائض", "5/5 Prayers", "أداء الصلوات الخمس اليوم", "All 5 prayers logged today", today.prayersMarked.size >= 5, "🕌")
        )
    }
}
