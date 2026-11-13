package com.example.ui.prayer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.DailyProgressRepository
import com.example.data.PrayerRecordRepository
import com.example.model.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrayerMarkCrudDialog(
    prayerType: PrayerType,
    timeFormatted: String,
    dateKey: String = DailyProgressRepository.todayKey(),
    isFuturePrayer: Boolean = false,
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val existing = remember(dateKey, prayerType) { PrayerRecordRepository.getRecord(dateKey, prayerType.id) }
    var selectedStatus by remember { mutableStateOf(existing?.status ?: PrayerStatus.ON_TIME) }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) "تسجيل وتعديل صلاة ${prayerType.nameAr}" else "Edit ${prayerType.nameEn} Record",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                    }
                }

                Text(
                    text = "${if (isArabic) "التاريخ:" else "Date:"} $dateKey • ${if (isArabic) "الموعد:" else "Time:"} $timeFormatted",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isFuturePrayer && dateKey == DailyProgressRepository.todayKey()) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "لم يحن موعد هذه الصلاة بعد اليوم! يمنع تأكيد صلاة مستقبلية."
                                else "This prayer time has not arrived yet! Future prayers cannot be marked.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Text(text = if (isArabic) "حالة أداء الصلاة:" else "Prayer Status:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val statuses = listOf(PrayerStatus.ON_TIME, PrayerStatus.JAMAAH, PrayerStatus.LATE_QADAA, PrayerStatus.EXCUSED)
                    statuses.forEach { st ->
                        FilterChip(
                            selected = selectedStatus == st,
                            onClick = { selectedStatus = st },
                            label = { Text(if (isArabic) st.labelAr else st.labelEn, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isArabic) "ملاحظات إضافية (المسجد، الخشوع...)" else "Notes (Mosque, Khushoo...)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorMessage.isNotBlank()) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    if (existing != null && existing.isPrayed) {
                        TextButton(
                            onClick = {
                                PrayerRecordRepository.deletePrayerMark(context, dateKey, prayerType)
                                onDismiss()
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isArabic) "إلغاء التأكيد" else "Unmark", fontSize = 11.sp)
                        }
                    } else Spacer(modifier = Modifier.width(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(onClick = onDismiss) { Text(if (isArabic) "إلغاء" else "Cancel") }
                        Button(
                            onClick = {
                                if (isFuturePrayer && dateKey == DailyProgressRepository.todayKey()) {
                                    errorMessage = if (isArabic) "لا يمكن تأكيد صلاة مستقبلية" else "Cannot mark future prayer"
                                    return@Button
                                }
                                val record = PrayerDailyRecord(
                                    dateKey = dateKey,
                                    prayerId = prayerType.id,
                                    isPrayed = true,
                                    status = selectedStatus,
                                    timestampMillis = System.currentTimeMillis(),
                                    notes = notes.trim()
                                )
                                PrayerRecordRepository.markOrUpdatePrayer(context, record)
                                onDismiss()
                            },
                            enabled = !(isFuturePrayer && dateKey == DailyProgressRepository.todayKey())
                        ) {
                            Text(if (isArabic) "حفظ الصلاة" else "Save")
                        }
                    }
                }
            }
        }
    }
}
