package com.example.ui.unified

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
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
import com.example.data.UnifiedRoutineRepository
import com.example.data.local.entity.ScreenLockEntity
import com.example.model.RoutineCategory
import com.example.model.UnifiedDashboardItem
import com.example.model.UnifiedRoutine
import com.example.ui.lock.ActiveLockActivity
import com.example.util.AppStrings
import com.example.util.CountdownUtils

@Composable
fun DashboardCard(
    item: UnifiedDashboardItem,
    onOpen: () -> Unit = {},
    onIncrement: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onReset: () -> Unit
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }
    val launchLock: () -> Unit = {
        val r = UnifiedRoutineRepository.routines.value.find { it.id == item.id }
        val entity = r?.toScreenLockEntity() ?: ScreenLockEntity(id = item.id, title = if (AppStrings.isArabic) item.titleAr else item.titleEn, lockType = "DHIKR", targetAmount = item.targetCount)
        ActiveLockActivity.launch(context, entity)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().clickable { onOpen() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    val icon = when (item.category) {
                        RoutineCategory.QURAN_WARD -> Icons.Default.AutoStories
                        RoutineCategory.ADHKAR_SET -> Icons.Default.Spa
                        else -> Icons.Default.AllInclusive
                    }
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Text(text = if (AppStrings.isArabic) item.titleAr else item.titleEn, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CountdownBadge(
                        remainingMillisProvider = {
                            if (item.repeatIntervalMinutes > 0) {
                                val r = UnifiedRoutine(id = item.id, titleAr = item.titleAr, titleEn = item.titleEn, category = item.category, targetCount = item.targetCount, repeatIntervalMinutes = item.repeatIntervalMinutes, startFromNow = item.startFromNow, lastTriggeredMillis = item.lastTriggeredMillis, createdAtMillis = item.createdAtMillis, triggerHour = item.scheduledHour, triggerMinute = item.scheduledMinute)
                                CountdownUtils.getRemainingMillisForRoutine(r)
                            } else CountdownUtils.getRemainingMillisForTime(item.scheduledHour, item.scheduledMinute)
                        },
                        isArabic = AppStrings.isArabic
                    )
                    if (item.isLockActive) {
                        IconButton(onClick = launchLock, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.LockOpen, contentDescription = "Test Lock", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    }
                    Box {
                        IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text(AppStrings.get("إرسال توست عائم الآن", "Trigger Floating Toast")) },
                                leadingIcon = { Icon(Icons.Default.NearMe, null, tint = MaterialTheme.colorScheme.primary) },
                                onClick = {
                                    menuExpanded = false
                                    val r = UnifiedRoutineRepository.routines.value.find { it.id == item.id }
                                    val dl = if (item.id.startsWith("dhikr_")) {
                                        val listId = item.id.removePrefix("dhikr_")
                                        CustomDhikrRepository.lists.value.find { it.id == listId }
                                    } else null
                                    if (dl != null) {
                                        com.example.ui.adhkar.DhikrToastScheduleHelper.triggerToastNow(context, dl as com.example.model.CustomDhikrList)
                                    } else {
                                        val phrases = r?.getSteps()?.map { com.example.model.TasbeehPhraseItem(it.textAr, it.textEn, it.targetCount) } ?: listOf(com.example.model.TasbeehPhraseItem(item.titleAr, item.titleEn, item.targetCount))
                                        com.example.ui.adhkar.DhikrToastScheduleHelper.triggerToastNow(context, item.titleAr, phrases)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(AppStrings.get("تجربة القفل الآن", "Test Lock Now")) },
                                leadingIcon = { Icon(Icons.Default.LockOpen, null, tint = MaterialTheme.colorScheme.primary) },
                                onClick = { menuExpanded = false; launchLock() }
                            )
                            DropdownMenuItem(text = { Text(AppStrings.get("تعديل", "Edit")) }, onClick = { menuExpanded = false; onEdit() })
                            DropdownMenuItem(text = { Text(AppStrings.get("استنساخ", "Duplicate")) }, onClick = { menuExpanded = false; onDuplicate() })
                            DropdownMenuItem(text = { Text(AppStrings.get("تصفير", "Reset")) }, onClick = { menuExpanded = false; onReset() })
                            DropdownMenuItem(text = { Text(AppStrings.get("حذف", "Delete"), color = MaterialTheme.colorScheme.error) }, onClick = { menuExpanded = false; onDelete() })
                        }
                    }
                }
            }

            Text(text = item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 4.dp))
            LinearProgressIndicator(
                progress = { item.progressFraction },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = if (item.isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${item.currentCount} / ${item.targetCount} (${(item.progressFraction * 100).toInt()}%)", style = MaterialTheme.typography.labelSmall)
                Button(
                    onClick = {
                        val isGoal = item.currentCount + 1 >= item.targetCount
                        com.example.util.TasbeehHapticsHelper.performCountFeedback(context, isGoal)
                        onIncrement()
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (item.isCompleted) AppStrings.get("مكتمل ✓", "Done ✓") else AppStrings.get("إنجاز +1", "+1 Done"), fontSize = 12.sp)
                }
            }
        }
    }
}
