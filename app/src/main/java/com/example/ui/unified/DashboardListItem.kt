package com.example.ui.unified

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RoutineCategory
import com.example.model.UnifiedDashboardItem
import com.example.util.AppStrings

@Composable
fun DashboardListItem(
    item: UnifiedDashboardItem,
    onOpen: () -> Unit = {},
    onIncrement: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth().clickable { onOpen() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                val icon = when (item.category) {
                    RoutineCategory.QURAN_WARD -> Icons.Default.AutoStories
                    RoutineCategory.ADHKAR_SET -> Icons.Default.Spa
                    RoutineCategory.TASBEEH_CHAIN -> Icons.Default.AllInclusive
                    else -> Icons.Default.AllInclusive
                }
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Column {
                    Text(if (AppStrings.isArabic) item.titleAr else item.titleEn, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${item.currentCount} / ${item.targetCount}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CountdownBadge(
                    remainingMillisProvider = { com.example.util.CountdownUtils.getRemainingMillisForTime(item.scheduledHour, item.scheduledMinute) },
                    isArabic = AppStrings.isArabic
                )
                FilledTonalButton(
                    onClick = onIncrement,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+1", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
