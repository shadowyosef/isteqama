package com.example.ui.stats

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ActivityCategory
import com.example.model.TinyActivityItem
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InstantAchievementsDialog(
    dateKey: String,
    activities: List<TinyActivityItem>,
    initialCategory: ActivityCategory,
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val filtered = remember(activities, selectedCategory) {
        if (selectedCategory == ActivityCategory.ALL) activities
        else activities.filter { it.category == selectedCategory }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp).testTag("instant_achievements_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "سجل الإنجازات اللحظية" else "Instant Achievements Log",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(dateKey, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ActivityCategory.entries.forEach { cat ->
                        val count = if (cat == ActivityCategory.ALL) activities.size else activities.count { it.category == cat }
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = {
                                val lbl = if (isArabic) cat.labelAr else cat.labelEn
                                Text("$lbl ($count)", fontSize = 11.sp)
                            },
                            modifier = Modifier.testTag("achievement_filter_${cat.name}")
                        )
                    }
                }

                HorizontalDivider()

                if (filtered.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isArabic) "لا توجد إنجازات مسجلة ضمن هذا النوع." else "No achievements logged for this filter.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filtered) { act ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    val icon = when (act.category) {
                                        ActivityCategory.PRAYER -> Icons.Default.Mosque
                                        ActivityCategory.QURAN -> Icons.Default.AutoStories
                                        ActivityCategory.TASBEEH -> Icons.Default.Spa
                                        ActivityCategory.LOCK_ROUTINE -> Icons.Default.Lock
                                        else -> Icons.Default.Stars
                                    }
                                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(if (isArabic) act.titleAr else act.titleEn, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        if (act.detailsAr.isNotBlank()) {
                                            Text(if (isArabic) act.detailsAr else act.detailsEn, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text(timeFormat.format(Date(act.timestampMillis)), fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    }
                }

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(if (isArabic) "إغلاق" else "Close")
                }
            }
        }
    }
}
