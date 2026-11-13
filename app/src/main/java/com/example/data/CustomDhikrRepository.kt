package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow

object CustomDhikrRepository {

    private val presetMorning = CustomDhikrList(
        id = "preset_morning",
        titleAr = "أذكار الصباح والتحصين",
        titleEn = "Morning Adhkar Routine",
        items = MorningAdhkarData.items,
        frequency = ReminderFrequency.DAILY,
        scheduledHour = 6,
        scheduledMinute = 0,
        requireOnLockScreen = true
    )

    private val presetEvening = CustomDhikrList(
        id = "preset_evening",
        titleAr = "أذكار المساء وحفظ الحرز",
        titleEn = "Evening Adhkar & Protection",
        items = EveningAdhkarData.items,
        frequency = ReminderFrequency.DAILY,
        scheduledHour = 17,
        scheduledMinute = 30,
        requireOnLockScreen = true
    )

    private val presetIstighfar = CustomDhikrList(
        id = "preset_istighfar",
        titleAr = "سلسلة الاستغفار والتوبة",
        titleEn = "Istighfar & Repentance Chain",
        items = listOf(
            DhikrItem("i1", DhikrCategory.CUSTOM, "الاستغفار والتوبة", "Istighfar", "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ وَأَتُوبُ إِلَيْهِ", "I seek forgiveness from Allah", "محو الخطايا وتفريج الهموم", 70),
            MorningAdhkarData.items[10]
        ),
        frequency = ReminderFrequency.HOURLY,
        scheduledHour = 12,
        scheduledMinute = 0,
        requireOnLockScreen = false
    )

    private val presetSalawat = CustomDhikrList(
        id = "preset_salawat",
        titleAr = "الصلاة والسلام على النبي ﷺ",
        titleEn = "Blessings upon the Prophet ﷺ",
        items = listOf(
            DhikrItem("s1", DhikrCategory.CUSTOM, "الصلاة على النبي ﷺ", "Salawat", "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ", "O Allah, send blessings upon Prophet Muhammad", "تكفى همك ويغفر ذنبك", 100)
        ),
        frequency = ReminderFrequency.DAILY,
        scheduledHour = 20,
        scheduledMinute = 0,
        requireOnLockScreen = false
    )

    val lists = MutableStateFlow(listOf(presetMorning, presetEvening, presetIstighfar, presetSalawat))

    fun addList(list: CustomDhikrList) {
        lists.value = lists.value + list
        updateLockScheduleForList(list)
    }

    fun updateList(updated: CustomDhikrList) {
        lists.value = lists.value.map { if (it.id == updated.id) updated else it }
        updateLockScheduleForList(updated)
    }

    fun deleteList(id: String) {
        lists.value = lists.value.filter { it.id != id }
        LockRepository.removeSchedule("lock_dhikr_$id")
    }

    fun duplicateList(list: CustomDhikrList) {
        val copy = list.copy(
            id = "d_${System.currentTimeMillis()}",
            titleAr = "${list.titleAr} (نسخة)",
            titleEn = "${list.titleEn} (Copy)",
            isCustom = true
        )
        addList(copy)
    }

    fun incrementItem(listId: String, itemId: String) {
        lists.value = lists.value.map { list ->
            if (list.id == listId) {
                val updatedItems = list.items.map { item ->
                    if (item.id == itemId) {
                        val newCount = (item.currentCount + 1).coerceAtMost(item.targetCount)
                        item.copy(currentCount = newCount, isCompleted = newCount >= item.targetCount)
                    } else item
                }
                list.copy(items = updatedItems)
            } else list
        }
        StatsRepository.recordDhikrCount(1)
    }

    fun resetList(listId: String) {
        lists.value = lists.value.map { list ->
            if (list.id == listId) {
                list.copy(items = list.items.map { it.copy(currentCount = 0, isCompleted = false) })
            } else list
        }
    }

    private fun updateLockScheduleForList(list: CustomDhikrList) {
        if (list.requireOnLockScreen) {
            val sched = LockSchedule(
                id = "lock_dhikr_${list.id}",
                title = list.titleAr,
                lockType = LockType.ADHKAR_ROUTINE,
                targetAmount = list.totalTarget,
                triggerHour = list.scheduledHour,
                triggerMinute = list.scheduledMinute,
                days = list.scheduledDays,
                isEnabled = true
            )
            LockRepository.addSchedule(sched)
        } else {
            LockRepository.removeSchedule("lock_dhikr_${list.id}")
        }
    }
}
