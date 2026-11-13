package com.example.ui.unified

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.common.VisualClockPickerDialog
import com.example.util.TimeFormatUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoutineScheduleSection(
    intervalMinutes: Int,
    onIntervalChange: (Int) -> Unit,
    startFromNow: Boolean,
    onStartFromNowChange: (Boolean) -> Unit,
    triggerHour: Int,
    triggerMinute: Int,
    onTimeChange: (Int, Int) -> Unit,
    isArabic: Boolean
) {
    var isRepeating by remember { mutableStateOf(intervalMinutes > 0) }
    var showClockDialog by remember { mutableStateOf(false) }

    val presets = listOf(
        15 to if (isArabic) "15 دقيقة" else "15m",
        30 to if (isArabic) "30 دقيقة" else "30m",
        60 to if (isArabic) "1 ساعة" else "1h",
        120 to if (isArabic) "2 ساعة" else "2h"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text(text = if (isArabic) "نمط التكرار والجدولة:" else "Schedule Mode:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(
                selected = !isRepeating,
                onClick = { isRepeating = false; onIntervalChange(0) },
                label = { Text(if (isArabic) "موعد يومي محدد" else "Daily Fixed Time", fontSize = 10.sp) },
                modifier = Modifier.weight(1f).testTag("routine_daily_chip")
            )
            FilterChip(
                selected = isRepeating,
                onClick = { isRepeating = true; if (intervalMinutes == 0) onIntervalChange(15) },
                label = { Text(if (isArabic) "تكرار دوري (يدوي)" else "Repeat Interval", fontSize = 10.sp) },
                modifier = Modifier.weight(1f).testTag("routine_repeat_chip")
            )
        }

        if (isRepeating) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = intervalMinutes.toString(),
                    onValueChange = { onIntervalChange(it.toIntOrNull()?.coerceIn(1, 1440) ?: intervalMinutes) },
                    label = { Text(if (isArabic) "كل (دقائق)" else "Every (min)", fontSize = 10.sp) },
                    modifier = Modifier.weight(1f).testTag("routine_interval_input")
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    presets.forEach { (mins, label) ->
                        AssistChip(onClick = { onIntervalChange(mins) }, label = { Text(label, fontSize = 9.sp) })
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = startFromNow,
                    onClick = { onStartFromNowChange(true) },
                    label = { Text(if (isArabic) "يعد من الآن" else "Count from now", fontSize = 10.sp) }
                )
                FilterChip(
                    selected = !startFromNow,
                    onClick = { onStartFromNowChange(false) },
                    label = { Text(if (isArabic) "بدء بوقت محدد" else "Pick start time", fontSize = 10.sp) }
                )
                if (!startFromNow) {
                    OutlinedButton(onClick = { showClockDialog = true }, contentPadding = PaddingValues(horizontal = 8.dp), modifier = Modifier.height(32.dp)) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(TimeFormatUtils.format12Hour(triggerHour, triggerMinute, isArabic), fontSize = 10.sp)
                    }
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(if (isArabic) "وقت الورد بالساعة:" else "Daily Time:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedButton(onClick = { showClockDialog = true }, modifier = Modifier.testTag("routine_visual_clock_btn")) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(TimeFormatUtils.format12Hour(triggerHour, triggerMinute, isArabic), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showClockDialog) {
        VisualClockPickerDialog(
            initialHour = triggerHour,
            initialMinute = triggerMinute,
            isArabic = isArabic,
            title = if (isArabic) "حدد الساعة والدقائق (صباحاً / مساءً)" else "Pick Time (AM / PM)",
            onDismiss = { showClockDialog = false },
            onConfirm = { h, m -> onTimeChange(h, m); showClockDialog = false }
        )
    }
}
