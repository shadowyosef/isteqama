package com.example.model

data class UnifiedDashboardItem(
    val id: String,
    val category: RoutineCategory,
    val titleAr: String,
    val titleEn: String,
    val subtitle: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val isLockActive: Boolean = false,
    val blockerStyle: BlockerStyle = BlockerStyle.OVERLAY,
    val frequency: ReminderFrequency = ReminderFrequency.DAILY,
    val scheduledHour: Int = 8,
    val scheduledMinute: Int = 0,
    val repeatIntervalMinutes: Int = 0,
    val startFromNow: Boolean = true,
    val lastTriggeredMillis: Long = 0L,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val isCustom: Boolean = false
) {
    val progressFraction: Float
        get() = if (targetCount > 0) (currentCount.toFloat() / targetCount).coerceIn(0f, 1f) else 0f
    val isCompleted: Boolean
        get() = targetCount > 0 && currentCount >= targetCount
}
