package com.example.ui.stats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.prayer.PrayerMarkCrudDialog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DailyProgressTimeline(
    dateKey: String,
    record: DailyProgressRecord,
    filterCategory: ActivityCategory,
    isArabic: Boolean
) {
    var editingPrayer by remember { mutableStateOf<PrayerType?>(null) }
    var showAchievementsPopup by remember { mutableStateOf(false) }
    val prayers = listOf(PrayerType.FAJR, PrayerType.DHUHR, PrayerType.ASR, PrayerType.MAGHRIB, PrayerType.ISHA)
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    val filteredCount = remember(record.activities, filterCategory) {
        if (filterCategory == ActivityCategory.ALL) record.activities.size
        else record.activities.count { it.category == filterCategory }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isArabic) "صلوات اليوم ($dateKey)" else "Prayers on $dateKey",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    prayers.forEach { pt ->
                        val pRec = record.prayersMarked[pt.id]
                        val isDone = pRec?.isPrayed == true && pRec.status != PrayerStatus.NOT_PRAYED
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDone) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                            onClick = { editingPrayer = pt }
                        ) {
                            Column(modifier = Modifier.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (isArabic) pt.nameAr else pt.nameEn, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Icon(
                                    if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ProgressMiniCard(title = if (isArabic) "القرآن" else "Quran", value = "${record.ayahsRead} ${if (isArabic) "آية" else "ayahs"}", sub = "${record.quranMinutes} ${if (isArabic) "د" else "m"}", modifier = Modifier.weight(1f))
            ProgressMiniCard(title = if (isArabic) "التسبيح" else "Tasbeeh", value = "${record.tasbeehCount}", sub = if (isArabic) "تسبيحة" else "dhikr", modifier = Modifier.weight(1f))
            ProgressMiniCard(title = if (isArabic) "الأقفال" else "Locks", value = "${record.locksCompleted}", sub = if (isArabic) "مكتمل" else "done", modifier = Modifier.weight(1f))
        }

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth().clickable { showAchievementsPopup = true }.testTag("open_instant_achievements_btn")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Column {
                        Text(
                            text = if (isArabic) "سجل الإنجازات اللحظية ($filteredCount)" else "Instant Achievements Log ($filteredCount)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isArabic) "اضغط للعرض كقائمة منبثقة والفلترة حسب النوع" else "Tap to open popup list & filter by type",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
        }
    }

    if (showAchievementsPopup) {
        InstantAchievementsDialog(
            dateKey = dateKey,
            activities = record.activities,
            initialCategory = filterCategory,
            isArabic = isArabic,
            onDismiss = { showAchievementsPopup = false }
        )
    }

    editingPrayer?.let { pt ->
        val existing = record.prayersMarked[pt.id]
        PrayerMarkCrudDialog(
            prayerType = pt,
            timeFormatted = if (existing != null) timeFormat.format(Date(existing.timestampMillis)) else "--:--",
            dateKey = dateKey,
            isFuturePrayer = false,
            isArabic = isArabic,
            onDismiss = { editingPrayer = null }
        )
    }
}

@Composable
private fun ProgressMiniCard(title: String, value: String, sub: String, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(sub, fontSize = 9.sp, color = MaterialTheme.colorScheme.outline)
        }
    }
}
