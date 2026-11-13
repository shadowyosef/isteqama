package com.example.util

import kotlin.math.ceil

data class KhatmaCatchUpOption(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val subtitleAr: String,
    val subtitleEn: String,
    val newDailyPages: Int
)

data class KhatmaCatchUpStatus(
    val targetDays: Int,
    val elapsedDays: Int,
    val expectedPagesSoFar: Int,
    val actualPagesRead: Int,
    val deficitPages: Int,
    val remainingPages: Int,
    val remainingDays: Int,
    val options: List<KhatmaCatchUpOption>
)

object KhatmaCatchUpHelper {
    private const val TOTAL_MUSHAF_PAGES = 604

    fun computeStatus(targetDays: Int, elapsedDays: Int, actualPagesRead: Int): KhatmaCatchUpStatus {
        val safeTarget = targetDays.coerceIn(1, 365)
        val safeElapsed = elapsedDays.coerceIn(1, safeTarget)
        val baseDaily = ceil(TOTAL_MUSHAF_PAGES.toDouble() / safeTarget).toInt()
        val expectedSoFar = (baseDaily * safeElapsed).coerceAtMost(TOTAL_MUSHAF_PAGES)
        val safeActual = actualPagesRead.coerceIn(0, TOTAL_MUSHAF_PAGES)
        val deficit = (expectedSoFar - safeActual).coerceAtLeast(0)
        val remainingPages = (TOTAL_MUSHAF_PAGES - safeActual).coerceAtLeast(0)
        val remainingDays = (safeTarget - safeElapsed).coerceAtLeast(1)

        val spreadDaily = ceil(remainingPages.toDouble() / remainingDays).toInt().coerceAtLeast(baseDaily)
        val threeDayBoost = (baseDaily + ceil(deficit / 3.0).toInt()).coerceAtMost(60)
        val weekendExtra = baseDaily + ceil(deficit / 2.0).toInt()

        val opts = if (deficit == 0) emptyList() else listOf(
            KhatmaCatchUpOption(
                "spread",
                "توزيع المتأخر على باقي الأيام",
                "Spread deficit across remaining days",
                "$spreadDaily صفحة/يوم لمدة $remainingDays يوم",
                "$spreadDaily pages/day for $remainingDays days",
                spreadDaily
            ),
            KhatmaCatchUpOption(
                "three_day",
                "خطة استدراك سريعة (٣ أيام)",
                "3-Day Quick Catch-Up",
                "$threeDayBoost صفحة/يوم لـ ٣ أيام ثم العودة لـ $baseDaily",
                "$threeDayBoost pages/day for 3 days, then $baseDaily",
                threeDayBoost
            ),
            KhatmaCatchUpOption(
                "weekend",
                "تعويض في نهاية الأسبوع",
                "Weekend Catch-Up Boost",
                "قراءة $weekendExtra صفحة يومي الجمعة والسبت",
                "$weekendExtra pages on weekend days",
                baseDaily
            )
        )
        return KhatmaCatchUpStatus(safeTarget, safeElapsed, expectedSoFar, safeActual, deficit, remainingPages, remainingDays, opts)
    }
}
