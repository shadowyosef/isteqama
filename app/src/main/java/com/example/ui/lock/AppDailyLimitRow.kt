package com.example.ui.lock

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FocusProfilesRepository
import com.example.model.AppRule

@Composable
fun AppDailyLimitRow(
    rule: AppRule,
    currentLimitMins: Int,
    usedSeconds: Int,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val usedMin = usedSeconds / 60
    var manualInput by remember(rule.packageName, currentLimitMins) {
        mutableStateOf(if (currentLimitMins > 0) currentLimitMins.toString() else "")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(rule.appName, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(
                        text = if (isArabic) "استخدمت $usedMin د اليوم • الحد: ${if (currentLimitMins > 0) "$currentLimitMins د" else "بلا حد"}"
                        else "Used ${usedMin}m today • Limit: ${if (currentLimitMins > 0) "${currentLimitMins}m" else "Off"}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = manualInput,
                    onValueChange = { raw -> manualInput = raw.filter { it.isDigit() }.take(4) },
                    label = { Text(if (isArabic) "حد يدوي (دقيقة)" else "Manual (min)", fontSize = 10.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("manual_limit_input_${rule.packageName}")
                )
                FilledTonalButton(
                    onClick = {
                        val mins = manualInput.toIntOrNull() ?: 0
                        FocusProfilesRepository.setAppDailyLimit(context, rule.packageName, mins)
                    },
                    modifier = Modifier.heightIn(min = 48.dp).testTag("apply_limit_btn_${rule.packageName}")
                ) {
                    Text(if (isArabic) "تطبيق" else "Set", fontSize = 11.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(0, 15, 30, 60, 120).forEach { mins ->
                    FilterChip(
                        selected = currentLimitMins == mins,
                        onClick = { FocusProfilesRepository.setAppDailyLimit(context, rule.packageName, mins) },
                        label = { Text(if (mins == 0) (if (isArZero(isArabic)) "بلا حد" else "Off") else "${mins}m", fontSize = 10.sp) }
                    )
                }
            }
        }
    }
}

private fun isArZero(isAr: Boolean): Boolean = isAr
