package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppPreferencesManager
import com.example.data.ScreenLockRepository
import com.example.data.local.entity.ScreenLockEntity
import com.example.model.LockContentConfig
import com.example.model.LockScheduleSlot
import com.example.ui.common.WizardStepFooter
import com.example.ui.common.WizardStepProgressHeader
import com.example.ui.theme.OrnateIslamicCard
import java.util.UUID

@Composable
fun CreateEditLockDialog(
    initialLock: ScreenLockEntity? = null,
    defaultDate: String? = null,
    onDismiss: () -> Unit
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val lKey = initialLock?.id ?: "new"
    var step by remember(lKey) { mutableIntStateOf(0) }
    var title by remember(lKey) { mutableStateOf(initialLock?.title ?: "") }
    var lockType by remember(lKey) { mutableStateOf(initialLock?.lockType ?: "QURAN") }
    var slots by remember(lKey) { mutableStateOf(initialLock?.getScheduleSlots() ?: listOf(LockScheduleSlot())) }
    var config by remember(lKey) { mutableStateOf(initialLock?.getContentConfig() ?: LockContentConfig()) }

    val stepTitles = if (isArabic) listOf("١. الاسم ونوع القفل", "٢. المحتوى والهدف", "٣. التكرار ووقت الهدوء")
    else listOf("1. Name & Lock Type", "2. Content & Target", "3. Repeats & Quiet Hours")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth().heightIn(max = 680.dp).testTag("create_edit_lock_dialog"),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (0..2).forEach { idx ->
                        FilterChip(selected = step == idx, onClick = { step = idx }, label = { Text("${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                    }
                }
                WizardStepProgressHeader(currentStep = step, totalSteps = 3, stepTitle = stepTitles[step], isArabic = isArabic)

                when (step) {
                    0 -> LockWizardStep1Type(title = title, onTitleChange = { title = it }, lockType = lockType, onTypeChange = { lockType = it }, isArabic = isArabic)
                    1 -> when (lockType) {
                        "QURAN", "HIFZ" -> QuranLockConfigSection(
                            config = config,
                            onConfigChange = { config = it },
                            onApplyKhatmaPlan = { newConfig, newSlots, newTitle ->
                                config = newConfig; slots = newSlots; title = newTitle
                            }
                        )
                        "QUIZ" -> OrnateIslamicCard {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(if (isArabic) "مسابقة المعرفة الإسلامية" else "Islamic Knowledge Quiz", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    if (isArabic) "سيعرض التطبيق سؤالاً إسلامياً متنوعاً عند التفعيل؛ الإجابة الصحيحة تفتح الشاشة وتزيد رصيدك."
                                    else "An interactive Islamic trivia question appears on lock; answering correctly unlocks your screen.",
                                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        else -> DhikrSelectSection(
                            config = config, lockType = lockType, onConfigChange = { config = it },
                            onTargetAmountUpdate = { total -> if (lockType != "QURAN") config = config.copy(ayahsPerSession = total) }
                        )
                    }
                    else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OrnateIslamicCard {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(if (isArabic) "ملخص القفل:" else "Lock Summary:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Text("${title.ifBlank { if (isArabic) "قفل شاشة" else "Screen Lock" }} • $lockType • ${slots.size}x", fontSize = 11.sp)
                            }
                        }
                        slots.forEachIndexed { index, slot ->
                            ScheduleSlotItemView(
                                index = index, slot = slot, canDelete = slots.size > 1,
                                onUpdate = { updated -> slots = slots.toMutableList().also { it[index] = updated } },
                                onDelete = { if (slots.size > 1) slots = slots.toMutableList().also { it.removeAt(index) } }
                            )
                        }
                        OutlinedButton(
                            onClick = { slots = slots + LockScheduleSlot(hour = ((slots.lastOrNull()?.hour ?: 6) + 4) % 24, minute = 0) },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("add_repeat_slot_btn")
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (isArabic) "أضف موعد تكرار إضافي" else "+ Add Repeat Schedule", fontSize = 11.sp)
                        }
                    }
                }

                WizardStepFooter(
                    currentStep = step,
                    totalSteps = 3,
                    isArabic = isArabic,
                    onCancelOrBack = { if (step > 0) step-- else onDismiss() },
                    onNext = { if (step < 2) step++ },
                    onFinish = {
                        val primarySlot = slots.firstOrNull() ?: LockScheduleSlot()
                        val target = if (lockType == "QURAN" && config.isTimedMode) config.readingDurationMinutes else config.ayahsPerSession
                        val entity = ScreenLockEntity(
                            id = initialLock?.id ?: UUID.randomUUID().toString(),
                            title = title.ifBlank { if (isArabic) "ورد وقفل شاشة" else "Screen Lock" },
                            lockType = lockType, targetAmount = target, triggerHour = primarySlot.hour, triggerMinute = primarySlot.minute,
                            recurrenceDays = primarySlot.daysOfWeek.joinToString(","), specificDate = defaultDate,
                            isEnabled = true, schedulesJson = ScreenLockEntity.serializeSlots(slots), contentConfigJson = config.serialize()
                        )
                        if (initialLock == null) ScreenLockRepository.addLock(entity) else ScreenLockRepository.updateLock(entity)
                        onDismiss()
                    },
                    finishLabel = if (isArabic) "حفظ القفل" else "Save Lock",
                    finishTestTag = "save_lock_schedule_btn"
                )
            }
        }
    }
}
