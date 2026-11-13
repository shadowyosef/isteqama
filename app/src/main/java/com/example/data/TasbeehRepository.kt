package com.example.data

import com.example.model.TasbeehItem
import com.example.model.TasbeehSet
import kotlinx.coroutines.flow.MutableStateFlow

object TasbeehRepository {
    val phrases = MutableStateFlow(AuthenticTasbeehCatalog.allPhrases)
    val tasbeehSets = MutableStateFlow(AuthenticTasbeehCatalog.defaultSets)
    val selectedIndex = MutableStateFlow(0)
    val activeSetId = MutableStateFlow<String?>("set_prayer")
    val totalLifetimeCount = MutableStateFlow(0)

    fun getCurrentItem(): TasbeehItem {
        val list = phrases.value
        val idx = selectedIndex.value.coerceIn(0, list.lastIndex)
        return list[idx]
    }

    fun increment() {
        val current = getCurrentItem()
        val next = current.count + 1
        val updatedList = phrases.value.toMutableList()
        val idx = selectedIndex.value.coerceIn(0, phrases.value.lastIndex)
        updatedList[idx] = current.copy(count = next, totalCount = current.totalCount + 1)
        phrases.value = updatedList
        totalLifetimeCount.value += 1
        StatsRepository.recordDhikrCount(1)

        if (next >= current.target && idx < phrases.value.lastIndex) {
            selectedIndex.value = idx + 1
        }
    }

    fun resetCurrent() {
        val current = getCurrentItem()
        val updatedList = phrases.value.toMutableList()
        val idx = selectedIndex.value.coerceIn(0, phrases.value.lastIndex)
        updatedList[idx] = current.copy(count = 0)
        phrases.value = updatedList
    }

    fun addCustomTasbeeh(phraseAr: String, phraseEn: String, target: Int) {
        val item = TasbeehItem("custom_${System.currentTimeMillis()}", phraseAr, phraseEn, target)
        phrases.value = phrases.value + item
        selectedIndex.value = phrases.value.lastIndex
    }

    fun addMultipleTasbeeh(newItems: List<TasbeehItem>) {
        val existingAr = phrases.value.map { it.phraseAr }.toSet()
        val toAdd = newItems.filter { it.phraseAr !in existingAr }
        if (toAdd.isNotEmpty()) {
            phrases.value = phrases.value + toAdd
        }
    }

    fun updateTasbeeh(id: String, phraseAr: String, phraseEn: String, target: Int) {
        phrases.value = phrases.value.map {
            if (it.id == id) it.copy(phraseAr = phraseAr, phraseEn = phraseEn, target = target) else it
        }
        tasbeehSets.value = tasbeehSets.value.map { set ->
            set.copy(items = set.items.map {
                if (it.id == id) it.copy(phraseAr = phraseAr, phraseEn = phraseEn, target = target) else it
            })
        }
    }

    fun deleteTasbeeh(id: String) {
        if (phrases.value.size > 1) {
            phrases.value = phrases.value.filter { it.id != id }
            selectedIndex.value = selectedIndex.value.coerceIn(0, phrases.value.lastIndex)
        }
    }

    fun addCustomSet(titleAr: String, titleEn: String, items: List<TasbeehItem>) {
        val newSet = TasbeehSet("set_${System.currentTimeMillis()}", titleAr, titleEn, items, true)
        tasbeehSets.value = tasbeehSets.value + newSet
    }
}
