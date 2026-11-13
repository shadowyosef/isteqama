package com.example.model

data class CustomDhikrList(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val items: List<DhikrItem>,
    val frequency: ReminderFrequency = ReminderFrequency.DAILY,
    val scheduledHour: Int = 8,
    val scheduledMinute: Int = 0,
    val scheduledDays: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    val requireOnLockScreen: Boolean = true,
    val isCustom: Boolean = false
) {
    val totalTarget: Int get() = items.sumOf { it.targetCount }
    val currentProgress: Int get() = items.sumOf { it.currentCount }
    val isCompleted: Boolean get() = items.isNotEmpty() && items.all { it.currentCount >= it.targetCount }
}
