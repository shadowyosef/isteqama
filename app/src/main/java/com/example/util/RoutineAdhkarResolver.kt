package com.example.util

import com.example.data.*
import com.example.model.*

object RoutineAdhkarResolver {

    fun getAdhkarForRoutine(itemId: String, titleAr: String): List<DhikrItem> {
        val allCurrentAdhkar = AdhkarRepository.adhkarState.value.associateBy { it.id }

        if (itemId.startsWith("dhikr_")) {
            val listId = itemId.removePrefix("dhikr_")
            val list = CustomDhikrRepository.lists.value.find { it.id == listId }
            if (list != null && list.items.isNotEmpty()) {
                return list.items.map { allCurrentAdhkar[it.id] ?: it }
            }
        }

        val lowerId = itemId.lowercase()
        val lowerTitle = titleAr.lowercase()

        val baseList: List<DhikrItem> = when {
            lowerId == "r_morning_adhkar" || lowerId.contains("morning") || lowerTitle.contains("صباح") -> {
                MorningAdhkarData.items
            }
            lowerId == "r_evening_adhkar" || lowerId.contains("evening") || lowerTitle.contains("مساء") -> {
                EveningAdhkarData.items
            }
            lowerId == "r_mulk_night" || lowerId.contains("sleep") || lowerTitle.contains("نوم") -> {
                SleepAdhkarData.items
            }
            lowerId == "r_tasbeeh_prayer" || lowerId.contains("prayer") || lowerTitle.contains("صلاة") || lowerTitle.contains("تسبيح") -> {
                PrayerAdhkarData.items
            }
            else -> {
                val routine = UnifiedRoutineRepository.routines.value.find { it.id == itemId }
                if (routine != null) {
                    val steps = routine.getSteps()
                    if (steps.isNotEmpty() && routine.stepsJson.isNotBlank()) {
                        steps.mapIndexed { idx, s ->
                            DhikrItem(
                                id = "${itemId}_$idx",
                                category = DhikrCategory.CUSTOM,
                                titleAr = if (s.textAr.contains(":")) s.textAr.substringBefore(":") else "ذكر ${idx + 1}",
                                titleEn = s.textEn.ifBlank { "Dhikr ${idx + 1}" },
                                textAr = if (s.textAr.contains(":")) s.textAr.substringAfter(":").trim() else s.textAr,
                                textEn = s.textEn,
                                virtue = "تكرار ${s.targetCount} مرات",
                                targetCount = s.targetCount
                            )
                        }
                    } else MorningAdhkarData.items
                } else MorningAdhkarData.items
            }
        }

        return baseList.map { allCurrentAdhkar[it.id] ?: it }
    }

    fun getAdhkarForLock(lock: com.example.data.local.entity.ScreenLockEntity): List<DhikrItem> {
        val config = lock.getContentConfig()
        if (config.selectedDhikrIds.isNotEmpty()) {
            val allBank = UnifiedDhikrBankCatalog.getAllBankItems().associateBy { it.textAr }
            val resolved = mutableListOf<DhikrItem>()
            config.selectedDhikrIds.forEachIndexed { idx, idOrText ->
                val bankItem = allBank[idOrText] ?: allBank.values.find { it.titleAr == idOrText || it.id == idOrText }
                if (bankItem != null) {
                    if (bankItem.isRoutineSet) {
                        resolved.addAll(getAdhkarForRoutine(bankItem.id.removePrefix("routine_"), bankItem.titleAr))
                    } else {
                        resolved.add(
                            DhikrItem(
                                id = "lock_item_${lock.id}_$idx",
                                category = DhikrCategory.CUSTOM,
                                titleAr = bankItem.titleAr,
                                titleEn = bankItem.titleEn,
                                textAr = bankItem.textAr,
                                textEn = bankItem.textEn,
                                virtue = bankItem.virtue,
                                targetCount = bankItem.targetCount
                            )
                        )
                    }
                } else {
                    resolved.add(
                        DhikrItem(
                            id = "lock_item_${lock.id}_$idx",
                            category = DhikrCategory.CUSTOM,
                            titleAr = idOrText.take(25),
                            titleEn = idOrText.take(25),
                            textAr = idOrText,
                            textEn = idOrText,
                            virtue = "ذكر القفل",
                            targetCount = lock.targetAmount.coerceAtLeast(33)
                        )
                    )
                }
            }
            if (resolved.isNotEmpty()) return resolved
        }

        if (!config.selectedRoutineId.isNullOrBlank()) {
            return getAdhkarForRoutine(config.selectedRoutineId, lock.title)
        }
        if (lock.id.startsWith("lock_routine_")) {
            return getAdhkarForRoutine(lock.id.removePrefix("lock_routine_"), lock.title)
        }
        if (lock.id.startsWith("lock_dhikr_")) {
            return getAdhkarForRoutine(lock.id.removePrefix("lock_"), lock.title)
        }
        return getAdhkarForRoutine(lock.id, lock.title)
    }
}
