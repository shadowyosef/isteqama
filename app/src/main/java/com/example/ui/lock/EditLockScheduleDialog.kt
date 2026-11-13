package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.AppPreferencesManager
import com.example.data.LockRepository
import com.example.model.BlockerStyle
import com.example.model.LockSchedule
import com.example.ui.common.VisualClockPickerDialog
import com.example.util.AppStrings
import com.example.util.TimeFormatUtils

@Composable
fun EditLockScheduleDialog(
    schedule: LockSchedule,
    onDismiss: () -> Unit
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val sKey = schedule.id
    var title by remember(sKey) { mutableStateOf(schedule.title) }
    var selectedStyle by remember(sKey) { mutableStateOf(schedule.blockerStyle) }
    var targetCountStr by remember(sKey) { mutableStateOf(schedule.targetAmount.toString()) }
    var triggerHour by remember(sKey) { mutableIntStateOf(schedule.triggerHour) }
    var triggerMinute by remember(sKey) { mutableIntStateOf(schedule.triggerMinute) }
    var showClockDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.get("تعديل جدول القفل", "Edit Lock Schedule"), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isArabic) "عنوان القفل" else "Lock Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isArabic) "موعد القفل اليومي:" else "Lock Time:", style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(onClick = { showClockDialog = true }) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(TimeFormatUtils.format12Hour(triggerHour, triggerMinute, isArabic))
                    }
                }
                Text(if (isArabic) "نوع وحزم القفل:" else "Lock Style:", style = MaterialTheme.typography.bodySmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    FilterChip(selected = selectedStyle == BlockerStyle.OVERLAY, onClick = { selectedStyle = BlockerStyle.OVERLAY }, label = { Text(if (isArabic) "أوفرلاي" else "Overlay") })
                    FilterChip(selected = selectedStyle == BlockerStyle.STRICT, onClick = { selectedStyle = BlockerStyle.STRICT }, label = { Text(if (isArabic) "صارم" else "Strict") })
                    FilterChip(selected = selectedStyle == BlockerStyle.GENTLE, onClick = { selectedStyle = BlockerStyle.GENTLE }, label = { Text(if (isArabic) "لطيف" else "Gentle") })
                }
                OutlinedTextField(
                    value = targetCountStr,
                    onValueChange = { targetCountStr = it.filter { c -> c.isDigit() } },
                    label = { Text(if (isArabic) "الهدف المطلوب" else "Target Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val count = targetCountStr.toIntOrNull() ?: schedule.targetAmount
                    val updated = schedule.copy(
                        title = title, blockerStyle = selectedStyle, targetAmount = count,
                        triggerHour = triggerHour, triggerMinute = triggerMinute
                    )
                    LockRepository.schedules.value = LockRepository.schedules.value.map {
                        if (it.id == schedule.id) updated else it
                    }
                    onDismiss()
                },
                modifier = Modifier.testTag("save_edit_schedule_btn")
            ) {
                Text(AppStrings.save)
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = {
                        LockRepository.removeSchedule(schedule.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                    Text(if (isArabic) "حذف" else "Delete")
                }
                TextButton(onClick = onDismiss) { Text(AppStrings.cancel) }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )

    if (showClockDialog) {
        VisualClockPickerDialog(
            initialHour = triggerHour,
            initialMinute = triggerMinute,
            isArabic = isArabic,
            title = if (isArabic) "اختر وقت القفل (صباحاً / مساءً)" else "Pick Lock Time (AM / PM)",
            onDismiss = { showClockDialog = false },
            onConfirm = { h, m ->
                triggerHour = h
                triggerMinute = m
                showClockDialog = false
            }
        )
    }
}
