package com.example.model

import java.util.UUID

data class LockScheduleSlot(
    val id: String = UUID.randomUUID().toString(),
    val hour: Int = 6,
    val minute: Int = 0,
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    val intervalHours: Int = 0,
    val intervalMinutes: Int = 0,
    val maxDailyRepeats: Int = 0,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: Int = 0,
    val quietHoursEnd: Int = 7
) {
    fun formattedTime(): String {
        return if (intervalMinutes > 0) {
            if (intervalMinutes >= 60) "كل ${intervalMinutes / 60} ساعة" else "كل $intervalMinutes دقيقة"
        } else {
            String.format("%02d:%02d", hour, minute)
        }
    }

    fun daysSummary(isArabic: Boolean): String {
        if (intervalMinutes > 0) {
            val intStr = if (intervalMinutes >= 60) {
                "${intervalMinutes / 60} ${if (isArabic) "ساعة" else "hrs"}"
            } else {
                "$intervalMinutes ${if (isArabic) "دقيقة" else "min"}"
            }
            val quietStr = if (quietHoursEnabled) {
                if (isArabic) " (راحة $quietHoursStart-$quietHoursEnd ص)" else " (quiet $quietHoursStart-$quietHoursEnd)"
            } else ""
            return (if (isArabic) "تكرار كل " else "Every ") + intStr + quietStr
        }
        if (daysOfWeek.size >= 7) return if (isArabic) "يومي" else "Daily"
        val dayNames = if (isArabic) {
            listOf("أحد", "إثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة", "سبت")
        } else {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        }
        return daysOfWeek.sorted().mapNotNull { d ->
            if (d in 1..7) dayNames[d - 1] else null
        }.joinToString(", ")
    }
}
