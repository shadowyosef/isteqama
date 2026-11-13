package com.example.ui.calendar.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.data.StatsRepository

@Composable
fun DailyLockProgressCard(modifier: Modifier = Modifier) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val stats by StatsRepository.stats.collectAsState()

    val quranGoal = 20
    val dhikrGoal = 100

    val quranProgress = (stats.ayahsReadToday.toFloat() / quranGoal).coerceIn(0f, 1f)
    val dhikrProgress = (stats.dhikrCompletedToday.toFloat() / dhikrGoal).coerceIn(0f, 1f)

    Card(
        modifier = modifier.fillMaxWidth().testTag("daily_lock_progress_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "الإنجاز اليومي للأوراد" else "Daily Ward Progress Summary",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${if (isArabic) "أقفال اليوم المنجزة:" else "Locks Done:"} ${stats.locksCompleted}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            GoalProgressRow(
                icon = Icons.Default.AutoStories,
                label = if (isArabic) "آيات القرآن المقروءة" else "Quran Ayahs Read",
                current = stats.ayahsReadToday,
                goal = quranGoal,
                progress = quranProgress,
                color = MaterialTheme.colorScheme.primary
            )

            GoalProgressRow(
                icon = Icons.Default.Spa,
                label = if (isArabic) "الأذكار والتسبيحات" else "Dhikrs & Tasbeeh",
                current = stats.dhikrCompletedToday,
                goal = dhikrGoal,
                progress = dhikrProgress,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun GoalProgressRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    current: Int,
    goal: Int,
    progress: Float,
    color: androidx.compose.ui.graphics.Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
            Text(text = "$current / $goal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = color
        )
    }
}
