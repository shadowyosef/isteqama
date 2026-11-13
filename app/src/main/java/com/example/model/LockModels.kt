package com.example.model

enum class BlockerStyle {
    STRICT,
    OVERLAY,
    GENTLE,
    ALLOWED
}

enum class LockType {
    QURAN_VERSES,
    ADHKAR_ROUTINE,
    TASBEEH_COUNT
}

data class LockSchedule(
    val id: String,
    val title: String,
    val lockType: LockType,
    val blockerStyle: BlockerStyle = BlockerStyle.OVERLAY,
    val targetAmount: Int = 10,
    val targetSurah: Int = 1,
    val startAyah: Int = 1,
    val endAyah: Int = 7,
    val triggerHour: Int = 6,
    val triggerMinute: Int = 0,
    val days: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    val isEnabled: Boolean = true
)

data class AppRule(
    val packageName: String,
    val appName: String,
    val blockerStyle: BlockerStyle = BlockerStyle.OVERLAY,
    val isBlocked: Boolean = true
)
