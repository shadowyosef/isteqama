package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.calendar.dashboard.CentralizedLocksDashboard

@Composable
fun CalendarScheduleScreen(
    viewModel: CalendarScheduleViewModel = viewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showAppBlocker by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_calendar_lock_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Lock")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).testTag("calendar_schedule_screen"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            item { LockPermissionBanner() }

            item { LiveCountdownCard() }

            item { CalendarQuickControlsRow(onOpenAppBlocker = { showAppBlocker = true }) }

            item { CentralizedLocksDashboard() }
        }
    }

    if (showCreateDialog) {
        CreateEditLockDialog(defaultDate = selectedDate, onDismiss = { showCreateDialog = false })
    }

    if (showAppBlocker) {
        com.example.ui.lock.AppBlockerConfigSheet(onDismiss = { showAppBlocker = false })
    }
}
