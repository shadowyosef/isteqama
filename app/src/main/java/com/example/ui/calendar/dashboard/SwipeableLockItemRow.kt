package com.example.ui.calendar.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ScreenLockRepository
import com.example.data.local.entity.ScreenLockEntity
import com.example.ui.lock.ActiveLockActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableLockItemRow(
    lock: ScreenLockEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).testTag("swipeable_lock_${lock.id}"),
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) MaterialTheme.colorScheme.error else Color.Transparent,
                label = "swipe_bg"
            )
            Box(
                modifier = Modifier.fillMaxSize().background(color).padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
            }
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Badge(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)) {
                        Text(lock.lockType, fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(2.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = lock.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        val timesStr = lock.getScheduleSlots().joinToString(", ") { it.formattedTime() }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = timesStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            com.example.ui.unified.CountdownBadge(
                                remainingMillisProvider = { com.example.util.CountdownUtils.getRemainingMillisForLock(lock, context = context) },
                                isArabic = com.example.util.AppStrings.isArabic,
                                isEnabled = lock.isEnabled
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { ActiveLockActivity.launch(context, lock) }, modifier = Modifier.size(32.dp).testTag("quick_test_${lock.id}")) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Test", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp).testTag("quick_edit_${lock.id}")) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("quick_delete_${lock.id}")) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                    Switch(
                        checked = lock.isEnabled,
                        onCheckedChange = { ScreenLockRepository.toggleLock(lock.id) },
                        modifier = Modifier.size(36.dp).testTag("quick_toggle_${lock.id}")
                    )
                }
            }
        }
    }
}
