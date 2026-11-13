package com.example.ui.calendar.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
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
import com.example.model.CompletedLockRecord

@Composable
fun CompletedLocksCard(modifier: Modifier = Modifier) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val completedLocks by ScreenLockRepository.completedLocksToday.collectAsState()

    Card(
        modifier = modifier.fillMaxWidth().testTag("completed_locks_card"),
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
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "الأقفال المكتملة اليوم (${completedLocks.size})" else "Completed Locks Today (${completedLocks.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (completedLocks.isNotEmpty()) {
                    TextButton(
                        onClick = { ScreenLockRepository.resetCompletedLocksToday() },
                        modifier = Modifier.height(28.dp).testTag("reset_completed_locks_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isArabic) "إعادة ضبط" else "Reset", fontSize = 11.sp)
                    }
                }
            }

            if (completedLocks.isEmpty()) {
                Text(
                    text = if (isArabic) "لم يتم إتمام أقفال اليوم بعد. تبدأ رحلتك مع أول ورد!" else "No locks completed yet today. Resets automatically at midnight.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                completedLocks.take(4).forEach { item ->
                    CompletedLockRow(item = item, isArabic = isArabic)
                }
            }
        }
    }
}

@Composable
private fun CompletedLockRow(item: CompletedLockRecord, isArabic: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = item.title, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Text(text = item.formattedTime(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
