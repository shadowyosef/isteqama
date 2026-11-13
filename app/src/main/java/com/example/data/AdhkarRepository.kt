package com.example.data

import com.example.model.DhikrCategory
import com.example.model.DhikrItem
import kotlinx.coroutines.flow.MutableStateFlow

object AdhkarRepository {
    val adhkarState = MutableStateFlow(AuthenticAdhkarCatalog.allAdhkar)

    fun getItems(category: DhikrCategory): List<DhikrItem> {
        return adhkarState.value.filter { it.category == category }
    }

    fun incrementDhikr(id: String) {
        val updated = adhkarState.value.map { item ->
            if (item.id == id && item.currentCount < item.targetCount) {
                val next = item.currentCount + 1
                StatsRepository.recordDhikrCount(1)
                item.copy(currentCount = next, isCompleted = next >= item.targetCount)
            } else item
        }
        adhkarState.value = updated
    }

    fun resetCategory(category: DhikrCategory) {
        adhkarState.value = adhkarState.value.map {
            if (it.category == category) it.copy(currentCount = 0, isCompleted = false) else it
        }
    }

    fun addCustomDhikr(textAr: String, textEn: String, target: Int, virtue: String, category: DhikrCategory = DhikrCategory.CUSTOM, titleAr: String = "ذكر مخصص") {
        val item = DhikrItem(
            id = "c_${System.currentTimeMillis()}",
            category = category,
            textAr = textAr,
            textEn = textEn,
            virtue = virtue,
            targetCount = target,
            isCustom = true,
            titleAr = titleAr,
            titleEn = "Custom Dhikr"
        )
        adhkarState.value = adhkarState.value + item
    }

    fun updateDhikr(id: String, textAr: String, textEn: String, target: Int, virtue: String, titleAr: String = "") {
        adhkarState.value = adhkarState.value.map {
            if (it.id == id) it.copy(
                titleAr = if (titleAr.isNotBlank()) titleAr else it.titleAr,
                textAr = textAr,
                textEn = textEn,
                targetCount = target,
                virtue = virtue
            )
            else it
        }
        CustomDhikrRepository.lists.value = CustomDhikrRepository.lists.value.map { list ->
            val hasItem = list.items.any { it.id == id }
            if (hasItem) {
                list.copy(items = list.items.map {
                    if (it.id == id) it.copy(
                        titleAr = if (titleAr.isNotBlank()) titleAr else it.titleAr,
                        textAr = textAr,
                        textEn = textEn,
                        targetCount = target,
                        virtue = virtue
                    ) else it
                })
            } else list
        }
    }

    fun deleteDhikr(id: String) {
        adhkarState.value = adhkarState.value.filter { it.id != id }
    }
}
