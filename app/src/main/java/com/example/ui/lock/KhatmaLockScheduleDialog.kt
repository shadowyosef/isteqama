package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.common.QuietHoursEditorRow
import com.example.util.AppStrings
import com.example.util.KhatmaScheduleCalculator
import com.example.util.TimeFormatUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KhatmaLockScheduleDialog(
    initialTargetDays: Int = 30,
    onDismiss: () -> Unit,
    onApply: (LockContentConfig, List<LockScheduleSlot>, String) -> Unit
) {
    val isAr = AppStrings.isArabic
    var targetDaysText by remember { mutableStateOf(initialTargetDays.toString()) }
    val targetDays = (targetDaysText.toIntOrNull() ?: 30).coerceIn(1, 365)
    var startHour by remember { mutableIntStateOf(9) }
    var endHour by remember { mutableIntStateOf(23) }
    var selectedMode by remember { mutableStateOf(KhatmaDistributionMode.INTERVAL_HOURLY) }
    var intervalMinutes by remember { mutableIntStateOf(60) }
    var customSessions by remember { mutableIntStateOf(5) }
    var quietEnabled by remember { mutableStateOf(true) }
    var quietStart by remember { mutableIntStateOf(0) }
    var quietEnd by remember { mutableIntStateOf(7) }
    var maxDailyRepeats by remember { mutableIntStateOf(0) }

    val plan = remember(targetDays, startHour, endHour, selectedMode, intervalMinutes, customSessions, quietEnabled, quietStart, quietEnd) {
        KhatmaScheduleCalculator.calculatePlan(targetDays, startHour, endHour, selectedMode, intervalMinutes, customSessions, quietEnabled, quietStart, quietEnd)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(max = 640.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "جدولة الختمة الذكية بالقفل" else "Smart Khatma Lock Generator", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Text(if (isAr) "عدد أيام الختمة (اختر أو اكتب يدوياً):" else "Khatma Days (Pick or Type Manually):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = targetDaysText,
                        onValueChange = { targetDaysText = it.filter { c -> c.isDigit() }.take(3) },
                        label = { Text(if (isAr) "أيام" else "Days", fontSize = 9.sp) },
                        singleLine = true,
                        modifier = Modifier.width(82.dp).testTag("khatma_manual_days_input")
                    )
                    FlowRow(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(7, 15, 30, 60, 90).forEach { d ->
                            FilterChip(selected = targetDays == d, onClick = { targetDaysText = d.toString() }, label = { Text("$d", fontSize = 10.sp) })
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startHour.toString(),
                        onValueChange = { startHour = (it.filter { c -> c.isDigit() }.toIntOrNull() ?: 9).coerceIn(0, 23) },
                        label = { Text("${if (isAr) "من" else "From"} (${TimeFormatUtils.format12Hour(startHour, 0, isAr)})", fontSize = 9.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endHour.toString(),
                        onValueChange = { endHour = (it.filter { c -> c.isDigit() }.toIntOrNull() ?: 23).coerceIn(0, 23) },
                        label = { Text("${if (isAr) "إلى" else "To"} (${TimeFormatUtils.format12Hour(endHour, 0, isAr)})", fontSize = 9.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    KhatmaDistributionMode.values().forEach { mode ->
                        FilterChip(selected = selectedMode == mode, onClick = { selectedMode = mode }, label = { Text(if (isAr) mode.labelAr else mode.labelEn, fontSize = 10.sp) })
                    }
                }

                if (selectedMode == KhatmaDistributionMode.INTERVAL_HOURLY) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(30 to if (isAr) "30 د" else "30m", 60 to if (isAr) "ساعة" else "1h", 90 to if (isAr) "1.5 س" else "1.5h", 120 to if (isAr) "ساعتان" else "2h").forEach { (m, label) ->
                            FilterChip(selected = intervalMinutes == m, onClick = { intervalMinutes = m }, label = { Text(label, fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                        }
                    }
                } else if (selectedMode == KhatmaDistributionMode.CUSTOM_SESSIONS) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(3, 5, 6, 8, 12).forEach { s ->
                            FilterChip(selected = customSessions == s, onClick = { customSessions = s }, label = { Text("$s", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                        }
                    }
                }

                QuietHoursEditorRow(
                    enabled = quietEnabled, startHour = quietStart, endHour = quietEnd, isArabic = isAr,
                    onToggle = { quietEnabled = it }, onRangeChange = { s, e -> quietStart = s; quietEnd = e }
                )

                KhatmaSchedulePreviewCard(plan = plan, isArabic = isAr)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(if (isAr) "إلغاء" else "Cancel") }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(onClick = {
                        val slots = KhatmaScheduleCalculator.buildScheduleSlots(plan, maxDailyRepeats, quietEnabled, quietStart, quietEnd)
                        val config = LockContentConfig(isPageMode = true, pagesPerSession = plan.pagesPerSession, startPage = 1, endPage = 604, currentProgressPage = 1)
                        val title = if (isAr) "ختمة ${plan.targetDays} يوم (${plan.pagesPerSession} صفحة/قفل)" else "${plan.targetDays}-Day Khatma (${plan.pagesPerSession} pages/lock)"
                        onApply(config, slots, title)
                        onDismiss()
                    }) { Text(if (isAr) "تطبيق الجدولة" else "Apply Plan") }
                }
            }
        }
    }
}
