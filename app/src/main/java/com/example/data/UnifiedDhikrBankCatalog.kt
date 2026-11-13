package com.example.data

import com.example.model.*

object UnifiedDhikrBankCatalog {

    fun getAllBankItems(): List<UnifiedBankDhikrItem> {
        val list = mutableListOf<UnifiedBankDhikrItem>()

        // 1. All Authentic Adhkar items individually
        AuthenticAdhkarCatalog.allAdhkar.forEach { dhikr ->
            val type = when (dhikr.category) {
                DhikrCategory.MORNING -> UnifiedBankItemType.MORNING
                DhikrCategory.EVENING -> UnifiedBankItemType.EVENING
                DhikrCategory.SLEEP -> UnifiedBankItemType.SLEEP
                DhikrCategory.PRAYER -> UnifiedBankItemType.PRAYER
                DhikrCategory.CUSTOM -> UnifiedBankItemType.CUSTOM
            }
            list.add(
                UnifiedBankDhikrItem(
                    id = "dhikr_${dhikr.id}",
                    titleAr = dhikr.titleAr.ifBlank { dhikr.textAr.take(30) },
                    titleEn = dhikr.titleEn.ifBlank { dhikr.textEn.take(30) },
                    textAr = dhikr.textAr,
                    textEn = dhikr.textEn,
                    targetCount = dhikr.targetCount,
                    virtue = dhikr.virtue,
                    itemType = type,
                    isRoutineSet = false
                )
            )
        }

        // 2. Comprehensive Tasbeeh catalog items
        ComprehensiveTasbeehCatalog.items.forEach { tasbeeh ->
            list.add(
                UnifiedBankDhikrItem(
                    id = "tasbeeh_${tasbeeh.id}",
                    titleAr = tasbeeh.phraseAr,
                    titleEn = tasbeeh.phraseEn,
                    textAr = tasbeeh.phraseAr,
                    textEn = tasbeeh.phraseEn,
                    targetCount = tasbeeh.defaultTarget,
                    virtue = tasbeeh.virtueAr,
                    itemType = UnifiedBankItemType.TASBEEH,
                    isRoutineSet = false
                )
            )
        }

        // 3. Custom Dhikrs added by user in AdhkarRepository
        AdhkarRepository.adhkarState.value.filter { it.isCustom }.forEach { custom ->
            if (list.none { it.textAr == custom.textAr }) {
                list.add(
                    UnifiedBankDhikrItem(
                        id = "custom_${custom.id}",
                        titleAr = custom.titleAr.ifBlank { custom.textAr.take(30) },
                        titleEn = custom.titleEn.ifBlank { custom.textEn.take(30) },
                        textAr = custom.textAr,
                        textEn = custom.textEn,
                        targetCount = custom.targetCount,
                        virtue = custom.virtue,
                        itemType = UnifiedBankItemType.CUSTOM,
                        isRoutineSet = false
                    )
                )
            }
        }

        // 4. Custom Tasbeeh phrases added by user in TasbeehRepository
        TasbeehRepository.phrases.value.forEach { t ->
            if (list.none { it.textAr == t.phraseAr }) {
                list.add(
                    UnifiedBankDhikrItem(
                        id = "t_user_${t.id}",
                        titleAr = t.phraseAr,
                        titleEn = t.phraseEn,
                        textAr = t.phraseAr,
                        textEn = t.phraseEn,
                        targetCount = t.target,
                        virtue = "تسبيح مخصص",
                        itemType = UnifiedBankItemType.TASBEEH,
                        isRoutineSet = false
                    )
                )
            }
        }

        // 5. Whole Routines (e.g. Morning Routine, Evening Routine, Custom Sets)
        CustomDhikrRepository.lists.value.forEach { r ->
            list.add(
                UnifiedBankDhikrItem(
                    id = "routine_${r.id}",
                    titleAr = "روتين: ${r.titleAr}",
                    titleEn = "Routine: ${r.titleEn}",
                    textAr = r.titleAr,
                    textEn = r.titleEn,
                    targetCount = r.totalTarget,
                    virtue = "روتين كامل يشمل ${r.items.size} أذكار متسلسلة",
                    itemType = UnifiedBankItemType.ROUTINE,
                    isRoutineSet = true,
                    routineChildCount = r.items.size
                )
            )
        }

        return list
    }
}
