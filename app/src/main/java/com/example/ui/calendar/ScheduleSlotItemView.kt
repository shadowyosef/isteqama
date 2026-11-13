package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.model.LockScheduleSlot
import com.example.model.RepeatUnit
import com.example.ui.common.QuietHoursEditorRow
import com.example.ui.common.VisualClockPickerDialog
import com.example.ui.tasbeeh.TasbeehIntervalPickerSection
import com.example.util.TimeFormatUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleSlotItemView(
    index: Int,
    slot: LockScheduleSlot,
    canDelete: Boolean,
    onUpdate: (LockScheduleSlot) -> Unit,
    onDelete: () -> Unit
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    var showClockDialog by remember { mutableStateOf(false) }

    val initMins = slot.intervalMinutes
    var isSpecificTimes by remember(slot.id) { mutableStateOf(initMins == 0) }
    var intervalValue by remember(slot.id) {
        mutableIntStateOf(if (initMins == 0) 15 else if (initMins % 60 == 0) initMins / 60 else initMins.coerceAtLeast(5))
    }
    var unit by remember(slot.id) {
        mutableStateOf(if (initMins >= 60 && initMins % 60 == 0) RepeatUnit.HOURS else RepeatUnit.MINUTES)
    }

    val days = if (isArabic) listOf("أحد" to 1, "إثنين" to 2, "ثلاثاء" to 3, "أربعاء" to 4, "خميس" to 5, "جمعة" to 6, "سبت" to 7)
    else listOf("Sun" to 1, "Mon" to 2, "Tue" to 3, "Wed" to 4, "Thu" to 5, "Fri" to 6, "Sat" to 7)

    fun calcMinutes(specific: Boolean, u: RepeatUnit, v: Int): Int {
        if (specific) return 0
        return if (u == RepeatUnit.HOURS) v * 60 else v.coerceAtLeast(5)
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("schedule_slot_item_$index"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${if (isArabic) "موعد التكرار" else "Repeat Slot"} #${index + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                if (canDelete) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }

            TasbeehIntervalPickerSection(
                isSpecificTimes = isSpecificTimes,
                onModeChange = { specific ->
                    isSpecificTimes = specific
                    onUpdate(slot.copy(intervalMinutes = calcMinutes(specific, unit, intervalValue)))
                },
                intervalValue = intervalValue,
                onValueChange = { v ->
                    intervalValue = v
                    onUpdate(slot.copy(intervalMinutes = calcMinutes(isSpecificTimes, unit, v)))
                },
                unit = unit,
                onUnitChange = { u ->
                    unit = u
                    onUpdate(slot.copy(intervalMinutes = calcMinutes(isSpecificTimes, u, intervalValue)))
                },
                startFromNow = slot.hour == 0 && slot.minute == 0,
                onStartFromNowChange = { fromNow ->
                    if (fromNow) onUpdate(slot.copy(hour = 0, minute = 0)) else onUpdate(slot.copy(hour = 6, minute = 0))
                },
                startHour = slot.hour, startMinute = slot.minute,
                onStartTimeChange = { h, m -> onUpdate(slot.copy(hour = h, minute = m)) },
                isArabic = isArabic,
                allowSeconds = false,
                minMinutes = 5,
                maxDailyRepeats = slot.maxDailyRepeats,
                onMaxRepeatsChange = { onUpdate(slot.copy(maxDailyRepeats = it)) }
            )

            if (isSpecificTimes) {
                OutlinedButton(onClick = { showClockDialog = true }, modifier = Modifier.fillMaxWidth().testTag("slot_clock_picker_btn_$index")) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${if (isArabic) "الوقت المحدد:" else "Selected:"} ${TimeFormatUtils.format12Hour(slot.hour, slot.minute, isArabic)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    days.forEach { (name, dayNum) ->
                        val isSelected = slot.daysOfWeek.contains(dayNum)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val updated = if (isSelected) slot.daysOfWeek - dayNum else slot.daysOfWeek + dayNum
                                onUpdate(slot.copy(daysOfWeek = updated.ifEmpty { listOf(dayNum) }))
                            },
                            label = { Text(name, fontSize = 10.sp) }, modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }

            QuietHoursEditorRow(
                enabled = slot.quietHoursEnabled,
                startHour = slot.quietHoursStart,
                endHour = slot.quietHoursEnd,
                isArabic = isArabic,
                onToggle = { onUpdate(slot.copy(quietHoursEnabled = it)) },
                onRangeChange = { s, e -> onUpdate(slot.copy(quietHoursStart = s, quietHoursEnd = e)) }
            )
        }
    }

    if (showClockDialog) {
        VisualClockPickerDialog(
            initialHour = slot.hour, initialMinute = slot.minute, isArabic = isArabic,
            title = if (isArabic) "حدد وقت القفل بالساعة" else "Pick Lock Time",
            onDismiss = { showClockDialog = false },
            onConfirm = { h, m -> onUpdate(slot.copy(hour = h, minute = m)); showClockDialog = false }
        )
    }
}
