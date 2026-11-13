package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppPreferencesManager
import com.example.data.LockRepository
import com.example.data.QuranRepository
import com.example.model.BlockerStyle
import com.example.model.LockSchedule
import com.example.model.LockType
import com.example.ui.common.VisualClockPickerDialog
import com.example.ui.common.WizardStepFooter
import com.example.ui.common.WizardStepProgressHeader
import com.example.util.TimeFormatUtils

@Composable
fun CreateLockDialog(onDismiss: () -> Unit) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    var step by remember { mutableIntStateOf(0) }
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(LockType.QURAN_VERSES) }
    var selectedStyle by remember { mutableStateOf(BlockerStyle.OVERLAY) }
    var targetSurahStr by remember { mutableStateOf("67") }
    var targetCountStr by remember { mutableStateOf("7") }
    var triggerHour by remember { mutableIntStateOf(6) }
    var triggerMinute by remember { mutableIntStateOf(0) }
    var showClockDialog by remember { mutableStateOf(false) }

    val surahNum = targetSurahStr.toIntOrNull() ?: 1
    val previewSurah = QuranRepository.getSurah(surahNum)
    val titles = if (isArabic) listOf("١. الاسم ونوع المهمة", "٢. السورة والهدف", "٣. التوقيت وأسلوب القفل")
    else listOf("1. Name & Task Type", "2. Surah & Target", "3. Time & Blocker Style")

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                WizardStepProgressHeader(currentStep = step, totalSteps = 3, stepTitle = titles[step], isArabic = isArabic)
                when (step) {
                    0 -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = title, onValueChange = { title = it },
                            label = { Text(if (isArabic) "عنوان القفل (مثال: ورد سورة الملك)" else "Lock Title") },
                            modifier = Modifier.fillMaxWidth().testTag("lock_title_input")
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(selected = selectedType == LockType.QURAN_VERSES, onClick = { selectedType = LockType.QURAN_VERSES }, label = { Text(if (isArabic) "قرآن" else "Quran", fontSize = 11.sp) }, modifier = Modifier.weight(1f))
                            FilterChip(selected = selectedType == LockType.ADHKAR_ROUTINE, onClick = { selectedType = LockType.ADHKAR_ROUTINE }, label = { Text(if (isArabic) "أذكار" else "Adhkar", fontSize = 11.sp) }, modifier = Modifier.weight(1f))
                            FilterChip(selected = selectedType == LockType.TASBEEH_COUNT, onClick = { selectedType = LockType.TASBEEH_COUNT }, label = { Text(if (isArabic) "تسبيح" else "Tasbeeh", fontSize = 11.sp) }, modifier = Modifier.weight(1f))
                        }
                    }
                    1 -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (selectedType == LockType.QURAN_VERSES) {
                            OutlinedTextField(
                                value = targetSurahStr, onValueChange = { targetSurahStr = it.filter { c -> c.isDigit() } },
                                label = { Text("${if (isArabic) "رقم السورة (1-114)" else "Surah (1-114)"} • ${previewSurah?.nameAr ?: ""}") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        OutlinedTextField(
                            value = targetCountStr, onValueChange = { targetCountStr = it.filter { c -> c.isDigit() } },
                            label = { Text(if (isArabic) "الهدف المطلوب (آيات أو تكرارات)" else "Target Amount") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(if (isArabic) "موعد القفل اليومي:" else "Lock Time:", style = MaterialTheme.typography.bodySmall)
                            OutlinedButton(onClick = { showClockDialog = true }, modifier = Modifier.heightIn(min = 48.dp)) {
                                Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(TimeFormatUtils.format12Hour(triggerHour, triggerMinute, isArabic))
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(BlockerStyle.OVERLAY to "Overlay", BlockerStyle.STRICT to "Strict", BlockerStyle.GENTLE to "Gentle").forEach { (st, lbl) ->
                                FilterChip(selected = selectedStyle == st, onClick = { selectedStyle = st }, label = { Text(lbl, fontSize = 11.sp) }, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                WizardStepFooter(
                    currentStep = step, totalSteps = 3, isArabic = isArabic,
                    onCancelOrBack = { if (step > 0) step-- else onDismiss() },
                    onNext = { if (step < 2) step++ },
                    onFinish = {
                        val count = targetCountStr.toIntOrNull() ?: 7
                        val sNum = targetSurahStr.toIntOrNull() ?: 1
                        LockRepository.addSchedule(LockSchedule(
                            id = "sched_${System.currentTimeMillis()}", title = title.ifEmpty { "قفل الورد - ${previewSurah?.nameAr ?: "القرآن"}" },
                            lockType = selectedType, blockerStyle = selectedStyle, targetAmount = count, targetSurah = sNum,
                            startAyah = 1, endAyah = count, triggerHour = triggerHour, triggerMinute = triggerMinute
                        ))
                        onDismiss()
                    },
                    finishLabel = if (isArabic) "حفظ القفل" else "Save",
                    finishTestTag = "save_lock_schedule_btn"
                )
            }
        }
    }
    if (showClockDialog) {
        VisualClockPickerDialog(
            initialHour = triggerHour, initialMinute = triggerMinute, isArabic = isArabic,
            title = if (isArabic) "اختر وقت القفل" else "Pick Lock Time",
            onDismiss = { showClockDialog = false },
            onConfirm = { h, m -> triggerHour = h; triggerMinute = m; showClockDialog = false }
        )
    }
}
