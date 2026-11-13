package com.example.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.data.local.entity.ScreenLockEntity
import com.example.ui.lock.ActiveLockActivity
import com.example.util.AppStrings

@Composable
fun DayLockItemCard(
    lock: ScreenLockEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val (typeColor, typeIcon, typeLabel) = when (lock.lockType.uppercase()) {
        "QURAN" -> Triple(CalendarColors.QURAN_DOT, Icons.Default.AutoStories, AppStrings.dotQuran)
        "DHIKR" -> Triple(CalendarColors.DHIKR_DOT, Icons.Default.Spa, AppStrings.dotDhikr)
        else -> Triple(CalendarColors.TASBEEH_DOT, Icons.Default.Lock, AppStrings.dotTasbeeh)
    }

    val slots = lock.getScheduleSlots()
    val timesSummary = slots.joinToString(" • ") { it.formattedTime() }
    val daysSummary = slots.firstOrNull()?.daysSummary(isArabic) ?: (if (isArabic) "يومي" else "Daily")

    Card(
        modifier = modifier.fillMaxWidth().testTag("day_lock_item_${lock.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).clip(CircleShape).background(typeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = typeIcon, contentDescription = null, tint = typeColor, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = lock.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        text = "$timesSummary ($daysSummary)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 2.dp)) {
                        Text(
                            text = "$typeLabel • ${if (isArabic) "الهدف" else "Target"}: ${lock.targetAmount}",
                            fontSize = 11.sp,
                            color = typeColor
                        )
                        com.example.ui.unified.CountdownBadge(
                            remainingMillisProvider = { com.example.util.CountdownUtils.getRemainingMillisForLock(lock, context = context) },
                            isArabic = isArabic,
                            isEnabled = lock.isEnabled,
                            accentColor = typeColor
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { ActiveLockActivity.launch(context, lock) },
                    modifier = Modifier.size(36.dp).testTag("test_lock_btn_${lock.id}")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Test Lock", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp).testTag("edit_lock_btn_${lock.id}")) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp).testTag("delete_lock_btn_${lock.id}")) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
                Switch(
                    checked = lock.isEnabled,
                    onCheckedChange = { onToggle() },
                    modifier = Modifier.testTag("toggle_lock_${lock.id}")
                )
            }
        }
    }
}
