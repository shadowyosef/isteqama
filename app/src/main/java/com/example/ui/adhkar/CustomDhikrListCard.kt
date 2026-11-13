package com.example.ui.adhkar

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomDhikrRepository
import com.example.model.CustomDhikrList
import com.example.util.AppStrings

@Composable
fun CustomDhikrListCard(
    list: CustomDhikrList,
    onEdit: () -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = if (AppStrings.isArabic) list.titleAr else list.titleEn, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = "${AppStrings.get("الموعد", "Time")}: ${String.format("%02d:%02d", list.scheduledHour, list.scheduledMinute)} • ${if (list.requireOnLockScreen) "🔒 " + AppStrings.get("قفل الشاشة", "Lock Screen") else AppStrings.get("تذكير فقط", "Reminder")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text(AppStrings.get("إرسال توست عائم الآن", "Trigger Floating Toast")) },
                            leadingIcon = { Icon(Icons.Default.NearMe, null, tint = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                menuOpen = false
                                com.example.ui.adhkar.DhikrToastScheduleHelper.triggerToastNow(context, list)
                            }
                        )
                        DropdownMenuItem(text = { Text(AppStrings.get("تعديل", "Edit")) }, onClick = { menuOpen = false; onEdit() })
                        DropdownMenuItem(text = { Text(AppStrings.get("استنساخ", "Duplicate")) }, onClick = { menuOpen = false; CustomDhikrRepository.duplicateList(list) })
                        DropdownMenuItem(text = { Text(AppStrings.get("تصفير العداد", "Reset")) }, onClick = { menuOpen = false; CustomDhikrRepository.resetList(list.id) })
                        if (list.isCustom) {
                            DropdownMenuItem(text = { Text(AppStrings.get("حذف", "Delete"), color = MaterialTheme.colorScheme.error) }, onClick = { menuOpen = false; CustomDhikrRepository.deleteList(list.id) })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            val progress = if (list.totalTarget > 0) (list.currentProgress.toFloat() / list.totalTarget).coerceIn(0f, 1f) else 0f
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(6.dp), color = MaterialTheme.colorScheme.primary)
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${list.currentProgress} / ${list.totalTarget}", style = MaterialTheme.typography.labelSmall)
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) AppStrings.get("إخفاء الأذكار ▲", "Collapse ▲") else AppStrings.get("عرض والتسبيح ▼", "Dhikr Items ▼"), fontSize = 12.sp)
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    list.items.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth().clickable {
                                val isGoal = item.currentCount + 1 >= item.targetCount
                                com.example.util.TasbeehHapticsHelper.performCountFeedback(context, isGoal)
                                CustomDhikrRepository.incrementItem(list.id, item.id)
                            }
                        ) {
                            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(if (AppStrings.isArabic) item.textAr else item.textEn, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text(item.virtue, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                                FilledTonalButton(onClick = {
                                    val isGoal = item.currentCount + 1 >= item.targetCount
                                    com.example.util.TasbeehHapticsHelper.performCountFeedback(context, isGoal)
                                    CustomDhikrRepository.incrementItem(list.id, item.id)
                                }) {
                                    Text("${item.currentCount} / ${item.targetCount}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
