package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LockContentConfig
import com.example.model.LockScheduleSlot
import com.example.ui.lock.KhatmaLockScheduleDialog

@Composable
fun QuranPageConfigFields(
    config: LockContentConfig,
    onConfigChange: (LockContentConfig) -> Unit,
    isArabic: Boolean,
    onApplyKhatmaPlan: ((LockContentConfig, List<LockScheduleSlot>, String) -> Unit)? = null
) {
    var showKhatmaDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                value = config.startPage.toString(),
                onValueChange = {
                    val v = (it.filter { c -> c.isDigit() }.toIntOrNull() ?: 1).coerceIn(1, 604)
                    onConfigChange(config.copy(startPage = v, currentProgressPage = v))
                },
                label = { Text(if (isArabic) "من صفحة" else "From P.", fontSize = 10.sp) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = config.endPage.toString(),
                onValueChange = {
                    val v = (it.filter { c -> c.isDigit() }.toIntOrNull() ?: 604).coerceIn(1, 604)
                    onConfigChange(config.copy(endPage = v))
                },
                label = { Text(if (isArabic) "إلى صفحة" else "To P.", fontSize = 10.sp) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = config.pagesPerSession.toString(),
                onValueChange = {
                    val v = (it.filter { c -> c.isDigit() }.toIntOrNull() ?: 1).coerceIn(1, 30)
                    onConfigChange(config.copy(pagesPerSession = v))
                },
                label = { Text(if (isArabic) "صفحات/قفل" else "Pages/Lock", fontSize = 10.sp) },
                modifier = Modifier.weight(1.2f)
            )
        }

        if (onApplyKhatmaPlan != null) {
            FilledTonalButton(
                onClick = { showKhatmaDialog = true },
                modifier = Modifier.fillMaxWidth().testTag("open_khatma_schedule_planner_btn")
            ) {
                Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "✨ جدولة ختمة ذكية بالقفل (أيام وساعات وراحة)"
                    else "✨ Smart Khatma Lock Schedule (Days & Hours)",
                    fontSize = 11.sp
                )
            }
        }

        Text(
            text = if (isArabic) "📖 سيعرض القفل صفحة قرآن كاملة متتالية لسرعة وسهولة القراءة، ويستأنف للصفحة التالية تلقائياً."
            else "📖 The lock screen displays complete Quran pages and advances to the next page automatically.",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showKhatmaDialog && onApplyKhatmaPlan != null) {
        KhatmaLockScheduleDialog(
            initialTargetDays = 30,
            onDismiss = { showKhatmaDialog = false },
            onApply = { newConfig, slots, title ->
                onApplyKhatmaPlan(newConfig, slots, title)
                showKhatmaDialog = false
            }
        )
    }
}

