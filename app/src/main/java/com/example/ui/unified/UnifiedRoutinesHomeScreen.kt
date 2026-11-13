package com.example.ui.unified

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.TasbeehScheduleRepository
import com.example.model.*
import com.example.service.FloatingTasbeehToastManager
import com.example.ui.navigation.NavigationState
import com.example.ui.tasbeeh.CreateTasbeehScheduleDialog
import com.example.util.AppStrings

@Composable
fun UnifiedRoutinesHomeScreen(
    dashboardViewModel: UnifiedDashboardViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        com.example.service.TasbeehAlertScheduler.start(context)
    }
    val selectedSubTab by NavigationState.routinesSubTab.collectAsState()
    val viewMode by dashboardViewModel.viewMode.collectAsState()
    val items by dashboardViewModel.items.collectAsState()
    var showCreateRoutineSheet by remember { mutableStateOf(false) }
    var showCreateTasbeehDialog by remember { mutableStateOf(false) }
    var viewingItem by remember { mutableStateOf<UnifiedDashboardItem?>(null) }
    var editingItem by remember { mutableStateOf<UnifiedDashboardItem?>(null) }
    var editingTasbeeh by remember { mutableStateOf<TasbeehScheduleModel?>(null) }
    val activeInAppToast by FloatingTasbeehToastManager.activeInAppToast.collectAsState()

    val subTabTitles = listOf(
        AppStrings.get("الأذكار والروتين", "Adhkar & Routines"),
        AppStrings.get("التسبيح والجدولة", "Tasbeeh & Scheduling")
    )

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { if (selectedSubTab == 0) showCreateRoutineSheet = true else showCreateTasbeehDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(if (selectedSubTab == 0) Icons.Default.Add else Icons.Default.NotificationsActive, contentDescription = "Add") },
                text = { Text(if (selectedSubTab == 0) AppStrings.get("إضافة روتين", "Add Routine") else AppStrings.get("إضافة تسبيح", "Add Tasbeeh")) },
                modifier = Modifier.testTag("unified_fab_dynamic")
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            activeInAppToast?.let { toast ->
                InAppToastBanner(toast = toast, onDismiss = { FloatingTasbeehToastManager.dismissInAppToast() })
            }

            TabRow(selectedTabIndex = selectedSubTab, modifier = Modifier.fillMaxWidth()) {
                subTabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSubTab == index,
                        onClick = { NavigationState.routinesSubTab.value = index },
                        text = { Text(title, maxLines = 1) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (selectedSubTab == 0) {
                    AdhkarRoutinesTabContent(
                        items = items,
                        viewMode = viewMode,
                        onViewModeChange = { dashboardViewModel.switchViewMode(it) },
                        onOpen = { viewingItem = it },
                        onIncrement = { dashboardViewModel.incrementProgress(it) },
                        onEdit = { editingItem = it },
                        onDuplicate = { dashboardViewModel.duplicateItem(it) },
                        onDelete = { dashboardViewModel.deleteItem(it) },
                        onReset = { dashboardViewModel.resetProgress(it) }
                    )
                } else {
                    TasbeehRoutinesTabContent(onEdit = { editingTasbeeh = it })
                }
            }
        }
    }

    var editingDhikrList by remember { mutableStateOf<com.example.model.CustomDhikrList?>(null) }

    if (showCreateRoutineSheet) CreateUnifiedRoutineSheet(onDismiss = { showCreateRoutineSheet = false })
    if (showCreateTasbeehDialog) {
        CreateTasbeehScheduleDialog(onDismiss = { showCreateTasbeehDialog = false }, onSave = { TasbeehScheduleRepository.addSchedule(it); showCreateTasbeehDialog = false })
    }
    viewingItem?.let { item ->
        RoutineDetailViewerSheet(
            item = item,
            onDismiss = { viewingItem = null },
            onEdit = {
                viewingItem = null
                editingItem = it
            }
        )
    }
    editingItem?.let { item ->
        if (item.id.startsWith("dhikr_")) {
            val listId = item.id.removePrefix("dhikr_")
            val dl = com.example.data.CustomDhikrRepository.lists.value.find { it.id == listId }
            if (dl != null) editingDhikrList = dl
            editingItem = null
        } else {
            val existing = com.example.data.UnifiedRoutineRepository.routines.value.find { it.id == item.id }
            val routine = existing ?: UnifiedRoutine(id = item.id, titleAr = item.titleAr, titleEn = item.titleEn, category = item.category, targetCount = item.targetCount, isLockActive = item.isLockActive, blockerStyle = item.blockerStyle, detailsSummary = item.subtitle)
            CreateUnifiedRoutineSheet(initialRoutine = routine, onDismiss = { editingItem = null })
        }
    }
    editingDhikrList?.let { dl ->
        com.example.ui.adhkar.CreateCustomDhikrSheet(initialList = dl, onDismiss = { editingDhikrList = null })
    }
    editingTasbeeh?.let { tasbeeh ->
        CreateTasbeehScheduleDialog(initialItem = tasbeeh, onDismiss = { editingTasbeeh = null }, onSave = { TasbeehScheduleRepository.updateSchedule(it); editingTasbeeh = null })
    }
}
