package com.example.data

import com.example.model.BlockerStyle
import com.example.model.RoutineCategory
import com.example.model.UnifiedRoutine
import kotlinx.coroutines.flow.MutableStateFlow

object UnifiedRoutineRepository {
    private val initialRoutines = listOf(
        UnifiedRoutine(
            id = "r_fajr",
            titleAr = "ورد الفجر وسورة الفاتحة",
            titleEn = "Fajr Quran Ward (Al-Fatihah)",
            category = RoutineCategory.QURAN_WARD,
            targetCount = 7,
            surahNumber = 1,
            startAyah = 1,
            endAyah = 7,
            isLockActive = true,
            triggerHour = 5,
            triggerMinute = 30,
            blockerStyle = BlockerStyle.OVERLAY,
            detailsSummary = "قراءة آيات سورة الفاتحة السبع مع التفسير الميسر"
        ),
        UnifiedRoutine(
            id = "r_morning_adhkar",
            titleAr = "أذكار الصباح والتحصين",
            titleEn = "Morning Adhkar Routine",
            category = RoutineCategory.ADHKAR_SET,
            targetCount = 25,
            isLockActive = true,
            triggerHour = 7,
            triggerMinute = 0,
            blockerStyle = BlockerStyle.STRICT,
            detailsSummary = "٢٥ ذكراً نبوياً كاملاً لحفظ النفس وبركة اليوم"
        ),
        UnifiedRoutine(
            id = "r_evening_adhkar",
            titleAr = "أذكار المساء وحفظ الحرز",
            titleEn = "Evening Adhkar Routine",
            category = RoutineCategory.ADHKAR_SET,
            targetCount = 14,
            isLockActive = true,
            triggerHour = 17,
            triggerMinute = 30,
            blockerStyle = BlockerStyle.STRICT,
            detailsSummary = "١٤ ذكراً نبوياً كاملاً للحفظ والسكينة"
        ),
        UnifiedRoutine(
            id = "r_tasbeeh_prayer",
            titleAr = "سلسلة تسبيح الصلاة (33x3)",
            titleEn = "After-Prayer Tasbeeh Chain",
            category = RoutineCategory.TASBEEH_CHAIN,
            targetCount = 100,
            isLockActive = false,
            triggerHour = 13,
            triggerMinute = 30,
            blockerStyle = BlockerStyle.GENTLE,
            detailsSummary = "33 سبحان الله • 33 الحمد لله • 34 الله أكبر"
        ),
        UnifiedRoutine(
            id = "r_mulk_night",
            titleAr = "ورد النوم - سورة الملك كاملة",
            titleEn = "Surat Al-Mulk Night Ward",
            category = RoutineCategory.QURAN_WARD,
            targetCount = 30,
            surahNumber = 67,
            startAyah = 1,
            endAyah = 30,
            isLockActive = true,
            triggerHour = 22,
            triggerMinute = 0,
            blockerStyle = BlockerStyle.STRICT,
            detailsSummary = "30 آية من سورة الملك المنجية من عذاب القبر"
        )
    )

    val routines = MutableStateFlow(initialRoutines)

    fun addRoutine(routine: UnifiedRoutine) {
        routines.value = listOf(routine) + routines.value
    }

    fun updateRoutine(routine: UnifiedRoutine) {
        routines.value = routines.value.map { if (it.id == routine.id) routine else it }
    }

    fun deleteRoutine(id: String) {
        routines.value = routines.value.filter { it.id != id }
    }

    fun duplicateRoutine(id: String) {
        val original = routines.value.find { it.id == id } ?: return
        val copy = original.copy(
            id = "r_copy_${System.currentTimeMillis()}",
            titleAr = "${original.titleAr} (نسخة)",
            titleEn = "${original.titleEn} (Copy)",
            currentCount = 0,
            isCompleted = false
        )
        routines.value = routines.value + copy
    }

    fun incrementCount(id: String) {
        routines.value = routines.value.map {
            if (it.id == id && it.currentCount < it.targetCount) {
                val next = it.currentCount + 1
                if (it.category == RoutineCategory.QURAN_WARD) {
                    StatsRepository.recordAyahsRead(1)
                } else {
                    StatsRepository.recordDhikrCount(1)
                }
                it.copy(currentCount = next, isCompleted = next >= it.targetCount)
            } else it
        }
    }

    fun resetCount(id: String) {
        routines.value = routines.value.map {
            if (it.id == id) it.copy(currentCount = 0, isCompleted = false) else it
        }
    }

    fun toggleLock(id: String) {
        routines.value = routines.value.map {
            if (it.id == id) {
                val updated = it.copy(isLockActive = !it.isLockActive)
                if (!updated.isLockActive && ScreenLockRepository.currentActiveLock.value?.id == "lock_routine_${id}") {
                    ScreenLockRepository.dismissLock()
                }
                updated
            } else it
        }
    }

    fun recordTriggered(id: String, timestamp: Long = System.currentTimeMillis()) {
        routines.value = routines.value.map {
            if (it.id == id) it.copy(lastTriggeredMillis = timestamp) else it
        }
    }
}
