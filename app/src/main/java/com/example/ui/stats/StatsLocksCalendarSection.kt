package com.example.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ScreenLockRepository
import com.example.data.local.entity.ScreenLockEntity
import com.example.ui.calendar.CalendarMonthGrid
import com.example.ui.calendar.CalendarMonthHeader
import com.example.ui.calendar.CalendarScheduleViewModel
import com.example.ui.calendar.CreateEditLockDialog
import com.example.ui.calendar.DayLockItemCard
import com.example.ui.calendar.dashboard.DailyLockProgressCard
import com.example.util.AppStrings

@Composable
fun StatsLocksCalendarSection(
    isArabic: Boolean,
    viewModel: CalendarScheduleViewModel = viewModel()
) {
    val currentCal by viewModel.currentCal.collectAsState()
    val calendarDays by viewModel.calendarDays.collectAsState()
    val selectedDayLocks by viewModel.selectedDayLocks.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingLock by remember { mutableStateOf<ScreenLockEntity?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().testTag("stats_locks_calendar_section")
    ) {
        DailyLockProgressCard()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) "تقويم الأقفال المجدولة:" else "Scheduled Locks Calendar:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            TextButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.heightIn(min = 48.dp).testTag("stats_add_scheduled_lock_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isArabic) "إضافة قفل" else "Add Lock", fontSize = 12.sp)
            }
        }

        CalendarMonthHeader(
            currentCalendar = currentCal,
            onPreviousMonth = { viewModel.prevMonth() },
            onNextMonth = { viewModel.nextMonth() },
            onToday = { viewModel.selectToday() }
        )
        CalendarMonthGrid(
            days = calendarDays,
            onSelectDay = { viewModel.selectDay(it) }
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${AppStrings.scheduledForDay} ($selectedDate)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${selectedDayLocks.size} ${AppStrings.get("أقفال", "locks")}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (selectedDayLocks.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Text(
                    text = AppStrings.noLocksForDay,
                    modifier = Modifier.padding(14.dp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            selectedDayLocks.forEach { lock ->
                DayLockItemCard(
                    lock = lock,
                    onToggle = { ScreenLockRepository.toggleLock(lock.id) },
                    onEdit = { editingLock = lock },
                    onDelete = { ScreenLockRepository.deleteLock(lock.id) }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateEditLockDialog(defaultDate = selectedDate, onDismiss = { showCreateDialog = false })
    }
    editingLock?.let { lock ->
        CreateEditLockDialog(initialLock = lock, defaultDate = selectedDate, onDismiss = { editingLock = null })
    }
}
