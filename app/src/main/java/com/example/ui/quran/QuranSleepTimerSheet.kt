package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SleepTimerMode
import com.example.util.AppStrings
import com.example.util.QuranSleepTimerManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranSleepTimerSheet(
    surahNumber: Int,
    surahNameAr: String,
    onDismiss: () -> Unit
) {
    val timerState by QuranSleepTimerManager.timerState.collectAsState()

    ModalBottomSheet(onDismissRequest = onDismiss, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Bedtime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = AppStrings.get("مؤقت النوم لإيقاف التلاوة", "Quran Playback Sleep Timer"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = AppStrings.get(
                    "حدد مدة زمنية أو نهاية السورة ليتم إيقاف التلاوة تلقائياً عند النوم",
                    "Stop recitation automatically after a set duration or when finishing the Surah"
                ),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (timerState.isActive) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val statusText = if (timerState.mode == SleepTimerMode.END_OF_SURAH) {
                                AppStrings.get("سيتوقف عند نهاية $surahNameAr", "Stops at end of $surahNameAr")
                            } else {
                                "${AppStrings.get("متبقي: ", "Remaining: ")}${QuranSleepTimerManager.formatRemainingTime()}"
                            }
                            Text(text = statusText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        FilledTonalButton(
                            onClick = { QuranSleepTimerManager.cancelTimer() },
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(AppStrings.get("إلغاء المؤقت", "Cancel"), fontSize = 11.sp)
                        }
                    }
                }
            }

            Text(AppStrings.get("خيارات المؤقت المتاحة:", "Available Timer Options:"), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

            // Duration options
            val options = listOf(5, 10, 15, 30, 45, 60)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                options.chunked(3).forEach { rowOptions ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowOptions.forEach { mins ->
                            val isSelected = timerState.mode == SleepTimerMode.DURATION_MINUTES && timerState.totalSeconds == mins * 60
                            OutlinedButton(
                                onClick = {
                                    QuranSleepTimerManager.startDurationTimer(mins)
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = if (isSelected) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                         else ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text("$mins ${AppStrings.get("دقيقة", "min")}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // End of Surah Option
            val isEndSurah = timerState.mode == SleepTimerMode.END_OF_SURAH
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isEndSurah) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                onClick = {
                    QuranSleepTimerManager.startEndOfSurahTimer(surahNumber)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(AppStrings.get("عند نهاية السورة الحالية", "At the end of current Surah"), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(surahNameAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    if (isEndSurah) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
