package com.example.ui.tasbeeh

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
fun SpecificTimesPickerSection(
    times: List<String>,
    onTimesChange: (List<String>) -> Unit,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    var pickerHour by remember { mutableIntStateOf(9) }
    var pickerMinute by remember { mutableIntStateOf(0) }
    var showClockDialog by remember { mutableStateOf(false) }

    val presets = listOf(
        "05:30" to (if (isArabic) "الفجر 05:30 ص" else "Fajr 5:30 AM"),
        "09:00" to (if (isArabic) "الضحى 09:00 ص" else "Duha 9:00 AM"),
        "13:00" to (if (isArabic) "الظهر 01:00 م" else "Dhuhr 1:00 PM"),
        "16:30" to (if (isArabic) "العصر 04:30 م" else "Asr 4:30 PM"),
        "18:45" to (if (isArabic) "المغرب 06:45 م" else "Maghrib 6:45 PM"),
        "20:30" to (if (isArabic) "العشاء 08:30 م" else "Isha 8:30 PM"),
        "22:30" to (if (isArabic) "الوتر 10:30 م" else "Witr 10:30 PM")
    )

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isArabic) "الأوقات المحددة للتنبيه اليومي:" else "Specific Daily Alert Times:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        if (times.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                times.forEach { timeStr ->
                    val parts = timeStr.split(":")
                    val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
                    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                    val label = TimeFormatUtils.format12Hour(h, m, isArabic)
                    InputChip(
                        selected = true,
                        onClick = { onTimesChange(times.filter { it != timeStr }) },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        trailingIcon = {
                            Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                        }
                    )
                }
            }
        } else {
            Text(
                text = if (isArabic) "لم تحدد أوقات بعد. اختر وقتاً عبر الساعة وأضفه:" else "No times added yet. Pick time via clock & add:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { showClockDialog = true },
                modifier = Modifier.weight(1f).testTag("open_visual_clock_btn")
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = TimeFormatUtils.format12Hour(pickerHour, pickerMinute, isArabic),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    val formatted = String.format("%02d:%02d", pickerHour, pickerMinute)
                    if (!times.contains(formatted)) {
                        onTimesChange((times + formatted).sorted())
                    }
                },
                modifier = Modifier.testTag("add_specific_time_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isArabic) "إضافة وقت" else "Add Time", fontSize = 11.sp)
            }
        }

        Text(text = if (isArabic) "أوقات مقترحة سريعة:" else "Quick Presets:", fontSize = 11.sp)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            presets.forEach { (timeStr, label) ->
                AssistChip(
                    onClick = {
                        if (!times.contains(timeStr)) onTimesChange((times + timeStr).sorted())
                    },
                    label = { Text(label, fontSize = 9.sp) }
                )
            }
        }
    }

    if (showClockDialog) {
        VisualClockPickerDialog(
            initialHour = pickerHour,
            initialMinute = pickerMinute,
            isArabic = isArabic,
            title = if (isArabic) "اختر وقت التنبيه (صباحاً / مساءً)" else "Pick Reminder Time (AM / PM)",
            onDismiss = { showClockDialog = false },
            onConfirm = { h, m ->
                pickerHour = h
                pickerMinute = m
                showClockDialog = false
            }
        )
    }
}
