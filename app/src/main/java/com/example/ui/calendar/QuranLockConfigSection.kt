package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.data.QuranMetadata
import com.example.model.LockContentConfig
import com.example.model.LockScheduleSlot

@Composable
fun QuranLockConfigSection(
    config: LockContentConfig,
    onConfigChange: (LockContentConfig) -> Unit,
    onApplyKhatmaPlan: ((LockContentConfig, List<LockScheduleSlot>, String) -> Unit)? = null
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    var showSurahDialog by remember { mutableStateOf(false) }
    var isKhatmaMode by remember { mutableStateOf(config.isPageMode && config.startPage == 1 && config.endPage == 604 && onApplyKhatmaPlan != null) }

    val surahNames = remember(config.selectedSurahIds) {
        config.selectedSurahIds.mapNotNull { id ->
            QuranMetadata.surahs.find { it.number == id }?.let { if (isArabic) it.nameAr else it.nameEn }
        }.joinToString("، ")
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (onApplyKhatmaPlan != null) {
            Text(if (isArabic) "نوع قفل القرآن:" else "Quran Lock Mode:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = !isKhatmaMode,
                    onClick = { isKhatmaMode = false },
                    label = { Text(if (isArabic) "📖 قراءة عادية" else "📖 Normal Reading", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f).testTag("quran_mode_normal_chip")
                )
                FilterChip(
                    selected = isKhatmaMode,
                    onClick = { isKhatmaMode = true },
                    label = { Text(if (isArabic) "✨ ختمة كاملة" else "✨ Full Khatma", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f).testTag("quran_mode_khatma_chip")
                )
            }
        }

        if (isKhatmaMode && onApplyKhatmaPlan != null) {
            QuranKhatmaInlineCard(isArabic = isArabic, onApplyKhatmaPlan = onApplyKhatmaPlan)
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(if (isArabic) "السور المحددة:" else "Selected Surahs:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = surahNames.ifBlank { if (isArabic) "الفاتحة" else "Al-Fatihah" }, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                }
                OutlinedButton(onClick = { showSurahDialog = true }, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp), modifier = Modifier.height(34.dp)) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isArabic) "تعديل السور" else "Pick Surahs", fontSize = 11.sp)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FilterChip(selected = !config.isPageMode && !config.isTimedMode, onClick = { onConfigChange(config.copy(isPageMode = false, isTimedMode = false)) }, label = { Text(if (isArabic) "بالآيات" else "Verses", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                FilterChip(selected = config.isPageMode && !config.isTimedMode, onClick = { onConfigChange(config.copy(isPageMode = true, isTimedMode = false)) }, label = { Text(if (isArabic) "بالصفحات" else "Pages", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                FilterChip(selected = config.isTimedMode, onClick = { onConfigChange(config.copy(isTimedMode = true)) }, label = { Text(if (isArabic) "مؤقت" else "Timed", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
            }

            when {
                config.isTimedMode -> {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${if (isArabic) "مدة القراءة المطلوبة:" else "Reading Duration:"} ${config.readingDurationMinutes} ${if (isArabic) "دقيقة" else "min"}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Slider(value = config.readingDurationMinutes.toFloat(), onValueChange = { onConfigChange(config.copy(readingDurationMinutes = it.toInt())) }, valueRange = 1f..60f, steps = 59)
                    }
                }
                config.isPageMode -> QuranPageConfigFields(config = config, onConfigChange = onConfigChange, isArabic = isArabic, onApplyKhatmaPlan = onApplyKhatmaPlan)
                else -> QuranAyahConfigFields(config = config, onConfigChange = onConfigChange, isArabic = isArabic)
            }
        }
    }

    if (showSurahDialog) {
        SurahMultiSelectDialog(initialSelected = config.selectedSurahIds, onDismiss = { showSurahDialog = false }, onConfirm = { ids -> onConfigChange(config.copy(selectedSurahIds = ids)) })
    }
}
