package com.example.model

import com.example.data.local.entity.ScreenLockEntity

enum class RoutineCategory {
    QURAN_WARD,
    ADHKAR_SET,
    TASBEEH_CHAIN,
    CUSTOM_HYBRID
}

data class UnifiedRoutine(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val category: RoutineCategory,
    val targetCount: Int,
    var currentCount: Int = 0,
    val surahNumber: Int = 1,
    val startAyah: Int = 1,
    val endAyah: Int = 7,
    val isLockActive: Boolean = false,
    val triggerHour: Int = 6,
    val triggerMinute: Int = 0,
    val repeatIntervalMinutes: Int = 0,
    val startFromNow: Boolean = true,
    val lastTriggeredMillis: Long = System.currentTimeMillis(),
    val createdAtMillis: Long = System.currentTimeMillis(),
    val blockerStyle: BlockerStyle = BlockerStyle.OVERLAY,
    val detailsSummary: String = "",
    val stepsJson: String = "",
    val isCompleted: Boolean = false
) {
    val progressFraction: Float
        get() = (currentCount.toFloat() / targetCount.coerceAtLeast(1)).coerceIn(0f, 1f)

    fun getSteps(): List<RoutineStepItem> {
        val parsed = RoutineStepItem.deserializeList(stepsJson)
        return if (parsed.isNotEmpty()) parsed else listOf(
            RoutineStepItem(textAr = titleAr, targetCount = targetCount)
        )
    }

    fun toScreenLockEntity(): ScreenLockEntity {
        val config = if (category == RoutineCategory.QURAN_WARD) {
            LockContentConfig(
                selectedSurahIds = listOf(surahNumber),
                startAyah = startAyah,
                endAyah = (startAyah + targetCount - 1).coerceAtLeast(startAyah),
                ayahsPerSession = targetCount.coerceAtLeast(1)
            )
        } else {
            val adhkar = com.example.util.RoutineAdhkarResolver.getAdhkarForRoutine(id, titleAr)
            val stepTexts = adhkar.map { "${it.titleAr}: ${it.textAr}" }
            LockContentConfig(
                selectedDhikrIds = stepTexts,
                selectedRoutineId = id
            )
        }
        val slots = listOf(
            LockScheduleSlot(
                id = "slot_$id",
                hour = triggerHour,
                minute = triggerMinute,
                intervalMinutes = repeatIntervalMinutes
            )
        )
        return ScreenLockEntity(
            id = "lock_routine_$id",
            title = titleAr,
            lockType = if (category == RoutineCategory.QURAN_WARD) "QURAN" else "DHIKR",
            targetAmount = targetCount,
            triggerHour = triggerHour,
            triggerMinute = triggerMinute,
            schedulesJson = ScreenLockEntity.serializeSlots(slots),
            contentConfigJson = config.serialize(),
            isEnabled = true
        )
    }
}
