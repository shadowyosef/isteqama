package com.example.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.TimeFormatUtils

@Composable
fun QuietHoursEditorRow(
    enabled: Boolean,
    startHour: Int,
    endHour: Int,
    isArabic: Boolean,
    onToggle: (Boolean) -> Unit,
    onRangeChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Bedtime, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Text(
                        text = if (isArabic) "ساعات الراحة وعدم الإزعاج" else "Rest & Quiet Hours",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    modifier = Modifier.testTag("quiet_hours_switch")
                )
            }

            if (enabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HourStepperBox(
                        label = if (isArabic) "من الساعة" else "From",
                        hour = startHour,
                        isArabic = isArabic,
                        onStep = { delta -> onRangeChange((startHour + delta + 24) % 24, endHour) },
                        modifier = Modifier.weight(1f)
                    )
                    HourStepperBox(
                        label = if (isArabic) "إلى الساعة" else "To",
                        hour = endHour,
                        isArabic = isArabic,
                        onStep = { delta -> onRangeChange(startHour, (endHour + delta + 24) % 24) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HourStepperBox(
    label: String,
    hour: Int,
    isArabic: Boolean,
    onStep: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                FilledTonalIconButton(onClick = { onStep(-1) }, modifier = Modifier.size(28.dp)) {
                    Text("-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Text(TimeFormatUtils.format12Hour(hour, 0, isArabic), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                FilledTonalIconButton(onClick = { onStep(1) }, modifier = Modifier.size(28.dp)) {
                    Text("+", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
