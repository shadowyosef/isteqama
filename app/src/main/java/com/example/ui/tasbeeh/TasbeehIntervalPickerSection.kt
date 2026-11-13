package com.example.ui.tasbeeh

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.model.RepeatUnit
import com.example.ui.common.VisualClockPickerDialog
import com.example.util.TimeFormatUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TasbeehIntervalPickerSection(
    isSpecificTimes: Boolean,
    onModeChange: (Boolean) -> Unit,
    intervalValue: Int,
    onValueChange: (Int) -> Unit,
    unit: RepeatUnit,
    onUnitChange: (RepeatUnit) -> Unit,
    startFromNow: Boolean,
    onStartFromNowChange: (Boolean) -> Unit,
    startHour: Int,
    startMinute: Int,
    onStartTimeChange: (Int, Int) -> Unit,
    isArabic: Boolean,
    allowSeconds: Boolean = true,
    minMinutes: Int = 1,
    maxDailyRepeats: Int = 0,
    onMaxRepeatsChange: ((Int) -> Unit)? = null
) {
    var showClockDialog by remember { mutableStateOf(false) }
    var customLimitText by remember(maxDailyRepeats) { mutableStateOf(if (maxDailyRepeats > 0) maxDailyRepeats.toString() else "") }
    val quickPresets = if (allowSeconds) listOf(
        Triple(30, RepeatUnit.SECONDS, if (isArabic) "30 ثانية" else "30s"),
        Triple(1, RepeatUnit.MINUTES, if (isArabic) "1 د" else "1m"),
        Triple(15, RepeatUnit.MINUTES, if (isArabic) "15 د" else "15m"),
        Triple(30, RepeatUnit.MINUTES, if (isArabic) "30 د" else "30m"),
        Triple(1, RepeatUnit.HOURS, if (isArabic) "1 س" else "1h"),
        Triple(2, RepeatUnit.HOURS, if (isArabic) "2 س" else "2h")
    ) else listOf(
        Triple(5, RepeatUnit.MINUTES, if (isArabic) "5 د" else "5m"),
        Triple(15, RepeatUnit.MINUTES, if (isArabic) "15 د" else "15m"),
        Triple(30, RepeatUnit.MINUTES, if (isArabic) "30 د" else "30m"),
        Triple(1, RepeatUnit.HOURS, if (isArabic) "1 س" else "1h"),
        Triple(2, RepeatUnit.HOURS, if (isArabic) "2 س" else "2h"),
        Triple(4, RepeatUnit.HOURS, if (isArabic) "4 س" else "4h")
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        Text(text = if (isArabic) "نمط التكرار والجدولة:" else "Repeat & Schedule Mode:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(
                selected = !isSpecificTimes, onClick = { onModeChange(false) },
                label = { Text(if (isArabic) "تكرار دوري" else "Repeat Interval", fontSize = 10.sp) },
                modifier = Modifier.weight(1f).testTag("mode_custom_interval_chip")
            )
            FilterChip(
                selected = isSpecificTimes, onClick = { onModeChange(true) },
                label = { Text(if (isArabic) "أوقات محددة" else "Specific Times", fontSize = 10.sp) },
                modifier = Modifier.weight(1f).testTag("mode_specific_times_chip")
            )
        }

        if (!isSpecificTimes) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = intervalValue.toString(),
                    onValueChange = {
                        val parsed = it.filter { c -> c.isDigit() }.toIntOrNull() ?: intervalValue
                        val clamped = if (!allowSeconds && unit == RepeatUnit.MINUTES) parsed.coerceAtLeast(minMinutes) else parsed.coerceIn(1, 9999)
                        onValueChange(clamped)
                    },
                    label = { Text(if (isArabic) "القيمة" else "Value", fontSize = 9.sp) },
                    modifier = Modifier.weight(1f).testTag("interval_value_input")
                )
                if (allowSeconds) FilterChip(selected = unit == RepeatUnit.SECONDS, onClick = { onUnitChange(RepeatUnit.SECONDS) }, label = { Text(if (isArabic) "ثواني" else "Sec", fontSize = 9.sp) })
                FilterChip(selected = unit == RepeatUnit.MINUTES, onClick = { onUnitChange(RepeatUnit.MINUTES) }, label = { Text(if (isArabic) "دقائق" else "Min", fontSize = 9.sp) })
                FilterChip(selected = unit == RepeatUnit.HOURS, onClick = { onUnitChange(RepeatUnit.HOURS) }, label = { Text(if (isArabic) "ساعات" else "Hrs", fontSize = 9.sp) })
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                quickPresets.forEach { (valNum, u, label) -> AssistChip(onClick = { onValueChange(valNum); onUnitChange(u) }, label = { Text(label, fontSize = 9.sp) }) }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                FilterChip(selected = startFromNow, onClick = { onStartFromNowChange(true) }, label = { Text(if (isArabic) "يعد من الآن" else "From now", fontSize = 10.sp) }, modifier = Modifier.testTag("start_from_now_chip"))
                FilterChip(selected = !startFromNow, onClick = { onStartFromNowChange(false) }, label = { Text(if (isArabic) "وقت بدء" else "Specific start", fontSize = 10.sp) }, modifier = Modifier.testTag("start_specific_chip"))
                if (!startFromNow) {
                    OutlinedButton(onClick = { showClockDialog = true }, contentPadding = PaddingValues(horizontal = 6.dp), modifier = Modifier.height(32.dp)) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(TimeFormatUtils.format12Hour(startHour, startMinute, isArabic), fontSize = 9.sp)
                    }
                }
            }
        }

        if (onMaxRepeatsChange != null) {
            Text(text = if (isArabic) "حد التكرار اليومي:" else "Daily Repeat Limit:", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf(0 to if (isArabic) "بلا حد" else "Unlimited", 3 to "3x", 5 to "5x", 10 to "10x", 20 to "20x").forEach { (cnt, label) ->
                    FilterChip(
                        selected = maxDailyRepeats == cnt,
                        onClick = { customLimitText = if (cnt > 0) cnt.toString() else ""; onMaxRepeatsChange(cnt) },
                        label = { Text(label, fontSize = 9.sp) }
                    )
                }
                OutlinedTextField(
                    value = customLimitText,
                    onValueChange = { raw ->
                        val digits = raw.filter { it.isDigit() }
                        customLimitText = digits
                        onMaxRepeatsChange((digits.toIntOrNull() ?: 0).coerceIn(0, 500))
                    },
                    label = { Text(if (isArabic) "يدوي" else "Custom", fontSize = 8.sp) },
                    singleLine = true,
                    modifier = Modifier.width(68.dp).testTag("manual_daily_limit_input")
                )
            }
        }
    }

    if (showClockDialog) {
        VisualClockPickerDialog(
            initialHour = startHour, initialMinute = startMinute, isArabic = isArabic,
            title = if (isArabic) "اختر وقت بدء التكرار" else "Pick Start Time",
            onDismiss = { showClockDialog = false },
            onConfirm = { h, m -> onStartTimeChange(h, m); showClockDialog = false }
        )
    }
}
