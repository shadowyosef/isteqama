package com.example.ui.calendar.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.data.ScreenLockRepository
import com.example.data.local.entity.ScreenLockEntity

@Composable
fun UpcomingLocksCard(
    searchQuery: String = "",
    selectedType: String? = null,
    onEditLock: (ScreenLockEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val locks by ScreenLockRepository.locks.collectAsState()

    val filteredLocks = remember(locks, searchQuery, selectedType) {
        locks.filter { lock ->
            val matchesQuery = searchQuery.isBlank() ||
                lock.title.contains(searchQuery.trim(), ignoreCase = true) ||
                lock.lockType.contains(searchQuery.trim(), ignoreCase = true)
            val matchesType = selectedType == null || lock.lockType.equals(selectedType, ignoreCase = true)
            matchesQuery && matchesType
        }
    }

    Card(
        modifier = modifier.fillMaxWidth().testTag("upcoming_locks_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "الأقفال المجدولة (${filteredLocks.size})" else "Scheduled Locks (${filteredLocks.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (isArabic) "اسحب للحذف أو عدّل سريعاً" else "Swipe to delete or edit",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (filteredLocks.isEmpty()) {
                Text(
                    text = if (isArabic) "لا توجد أقفال تطابق بحثك الحالي." else "No locks matching your search.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                filteredLocks.forEach { lock ->
                    SwipeableLockItemRow(
                        lock = lock,
                        onEdit = { onEditLock(lock) },
                        onDelete = { ScreenLockRepository.deleteLock(lock.id) }
                    )
                }
            }
        }
    }
}
