package com.example.ui.unified

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppPreferencesManager
import com.example.data.CustomDhikrRepository
import com.example.data.StatsRepository
import com.example.data.UnifiedRoutineRepository
import com.example.model.*
import kotlinx.coroutines.flow.*

class UnifiedDashboardViewModel : ViewModel() {

    val viewMode: StateFlow<String> = AppPreferencesManager.dashboardViewMode
    private val _customItems = MutableStateFlow<List<UnifiedDashboardItem>>(emptyList())

    val items: StateFlow<List<UnifiedDashboardItem>> = combine(
        UnifiedRoutineRepository.routines,
        CustomDhikrRepository.lists,
        _customItems
    ) { routines, dhikrLists, customItems ->
        val routineItems = routines.map { r ->
            UnifiedDashboardItem(
                id = r.id,
                category = r.category,
                titleAr = r.titleAr,
                titleEn = r.titleEn,
                subtitle = r.detailsSummary,
                targetCount = r.targetCount,
                currentCount = r.currentCount,
                isLockActive = r.isLockActive,
                blockerStyle = r.blockerStyle,
                scheduledHour = r.triggerHour,
                scheduledMinute = r.triggerMinute,
                repeatIntervalMinutes = r.repeatIntervalMinutes,
                startFromNow = r.startFromNow,
                lastTriggeredMillis = r.lastTriggeredMillis,
                createdAtMillis = r.createdAtMillis,
                isCustom = false
            )
        }
        val dhikrItems = dhikrLists.map { d ->
            UnifiedDashboardItem(
                id = "dhikr_${d.id}",
                category = RoutineCategory.ADHKAR_SET,
                titleAr = d.titleAr,
                titleEn = d.titleEn,
                subtitle = "${d.items.size} أذكار • ${String.format("%02d:%02d", d.scheduledHour, d.scheduledMinute)}",
                targetCount = d.totalTarget,
                currentCount = d.currentProgress,
                isLockActive = d.requireOnLockScreen,
                frequency = d.frequency,
                scheduledHour = d.scheduledHour,
                scheduledMinute = d.scheduledMinute,
                isCustom = d.isCustom
            )
        }
        (routineItems + dhikrItems + customItems).distinctBy { it.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun switchViewMode(mode: String) = AppPreferencesManager.setDashboardViewMode(mode)

    fun createItem(item: UnifiedDashboardItem) {
        _customItems.value = _customItems.value + item
        val routine = UnifiedRoutine(
            id = item.id,
            titleAr = item.titleAr,
            titleEn = item.titleEn,
            category = item.category,
            targetCount = item.targetCount,
            isLockActive = item.isLockActive,
            blockerStyle = item.blockerStyle,
            detailsSummary = item.subtitle,
            repeatIntervalMinutes = item.repeatIntervalMinutes,
            startFromNow = item.startFromNow,
            lastTriggeredMillis = item.lastTriggeredMillis,
            createdAtMillis = item.createdAtMillis
        )
        UnifiedRoutineRepository.addRoutine(routine)
    }

    fun updateItem(updated: UnifiedDashboardItem) {
        _customItems.value = _customItems.value.map { if (it.id == updated.id) updated else it }
        val routine = UnifiedRoutine(
            id = updated.id,
            titleAr = updated.titleAr,
            titleEn = updated.titleEn,
            category = updated.category,
            targetCount = updated.targetCount,
            isLockActive = updated.isLockActive,
            blockerStyle = updated.blockerStyle,
            detailsSummary = updated.subtitle,
            repeatIntervalMinutes = updated.repeatIntervalMinutes,
            startFromNow = updated.startFromNow,
            lastTriggeredMillis = updated.lastTriggeredMillis,
            createdAtMillis = updated.createdAtMillis
        )
        UnifiedRoutineRepository.updateRoutine(routine)
    }

    fun deleteItem(id: String) {
        _customItems.value = _customItems.value.filter { it.id != id }
        UnifiedRoutineRepository.deleteRoutine(id)
        if (id.startsWith("dhikr_")) CustomDhikrRepository.deleteList(id.removePrefix("dhikr_"))
    }

    fun duplicateItem(item: UnifiedDashboardItem) {
        val copy = item.copy(id = "item_${System.currentTimeMillis()}", titleAr = "${item.titleAr} (نسخة)", titleEn = "${item.titleEn} (Copy)", isCustom = true)
        createItem(copy)
    }

    fun incrementProgress(id: String) {
        if (id.startsWith("dhikr_")) {
            val listId = id.removePrefix("dhikr_")
            val list = CustomDhikrRepository.lists.value.find { it.id == listId }
            list?.items?.firstOrNull()?.let { CustomDhikrRepository.incrementItem(listId, it.id) }
        } else {
            UnifiedRoutineRepository.incrementCount(id)
        }
        StatsRepository.recordDhikrCount(1)
    }

    fun resetProgress(id: String) {
        if (id.startsWith("dhikr_")) CustomDhikrRepository.resetList(id.removePrefix("dhikr_"))
        else UnifiedRoutineRepository.resetCount(id)
    }

    fun toggleLock(id: String) = UnifiedRoutineRepository.toggleLock(id)
}
