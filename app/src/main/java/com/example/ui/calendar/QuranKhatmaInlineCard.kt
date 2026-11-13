package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.common.QuietHoursEditorRow
import com.example.ui.lock.KhatmaLockScheduleDialog
import com.example.ui.lock.KhatmaSchedulePreviewCard
import com.example.util.KhatmaScheduleCalculator

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuranKhatmaInlineCard(
    isArabic: Boolean,
    onApplyKhatmaPlan: (LockContentConfig, List<LockScheduleSlot>, String) -> Unit
) {
    var daysText by remember { mutableStateOf("30") }
    val targetDays = (daysText.toIntOrNull() ?: 30).coerceIn(1, 365)
    var startHour by remember { mutableIntStateOf(8) }
    var endHour by remember { mutableIntStateOf(22) }
    var mode by remember { mutableStateOf(KhatmaDistributionMode.INTERVAL_HOURLY) }
    var quietEnabled by remember { mutableStateOf(true) }
    var quietStart by remember { mutableIntStateOf(0) }
    var quietEnd by remember { mutableIntStateOf(7) }
    var showFullDialog by remember { mutableStateOf(false) }

    val plan = remember(targetDays, startHour, endHour, mode, quietEnabled, quietStart, quietEnd) {
        KhatmaScheduleCalculator.calculatePlan(targetDays, startHour, endHour, mode, 60, 5, quietEnabled, quietStart, quietEnd)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth().testTag("quran_khatma_inline_card")
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(if (isArabic) "تخصيص ختمة كاملة يدوياً" else "Full Khatma Customization", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                TextButton(onClick = { showFullDialog = true }, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(if (isArabic) "خيارات متقدمة" else "Advanced", fontSize = 10.sp)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = daysText,
                    onValueChange = { daysText = it.filter { c -> c.isDigit() }.take(3) },
                    label = { Text(if (isArabic) "عدد الأيام" else "Days", fontSize = 9.sp) },
                    singleLine = true,
                    modifier = Modifier.width(86.dp).testTag("inline_khatma_days_input")
                )
                FlowRow(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(7, 15, 30, 60).forEach { d ->
                        FilterChip(selected = targetDays == d, onClick = { daysText = d.toString() }, label = { Text("$d ${if (isArabic) "ي" else "d"}", fontSize = 9.sp) })
                    }
                }
            }

            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                KhatmaDistributionMode.values().forEach { m ->
                    FilterChip(selected = mode == m, onClick = { mode = m }, label = { Text(if (isArabic) m.labelAr else m.labelEn, fontSize = 9.sp) })
                }
            }

            QuietHoursEditorRow(
                enabled = quietEnabled, startHour = quietStart, endHour = quietEnd, isArabic = isArabic,
                onToggle = { quietEnabled = it }, onRangeChange = { s, e -> quietStart = s; quietEnd = e }
            )

            KhatmaSchedulePreviewCard(plan = plan, isArabic = isArabic)

            Button(
                onClick = {
                    val slots = KhatmaScheduleCalculator.buildScheduleSlots(plan, 0, quietEnabled, quietStart, quietEnd)
                    val cfg = LockContentConfig(isPageMode = true, pagesPerSession = plan.pagesPerSession, startPage = 1, endPage = 604, currentProgressPage = 1)
                    val title = if (isArabic) "ختمة ${plan.targetDays} يوم (${plan.pagesPerSession} ص/قفل)" else "${plan.targetDays}-Day Khatma (${plan.pagesPerSession}p)"
                    onApplyKhatmaPlan(cfg, slots, title)
                },
                modifier = Modifier.fillMaxWidth().testTag("apply_inline_khatma_btn")
            ) {
                Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isArabic) "توليد وتطبيق جدول الختمة" else "Generate & Apply Khatma", fontSize = 11.sp)
            }
        }
    }

    if (showFullDialog) {
        KhatmaLockScheduleDialog(
            initialTargetDays = targetDays,
            onDismiss = { showFullDialog = false },
            onApply = { cfg, slots, t -> onApplyKhatmaPlan(cfg, slots, t); showFullDialog = false }
        )
    }
}
