package com.example.ui.lock

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StatsRepository
import com.example.model.DhikrItem
import com.example.util.AppStrings
import com.example.util.TasbeehHapticsHelper

@Composable
fun SequentialDhikrLockRunner(
    routineTitle: String,
    items: List<DhikrItem>,
    queueInfo: String = "",
    onAllCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var currentIndex by remember { mutableIntStateOf(0) }
    var currentCount by remember { mutableIntStateOf(0) }

    val safeItems = if (items.isNotEmpty()) items else listOf(
        DhikrItem("fallback", com.example.model.DhikrCategory.MORNING, "سُبْحَانَ اللَّهِ", "Tasbeeh", "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", "", "", 33)
    )
    val activeDhikr = safeItems.getOrElse(currentIndex) { safeItems[0] }

    val repeatBadge = when (activeDhikr.targetCount) {
        1 -> if (isAr) "مرة واحدة" else "1 time"
        2 -> if (isAr) "مرتان" else "2 times"
        in 3..10 -> if (isAr) "${activeDhikr.targetCount} مرات" else "${activeDhikr.targetCount} times"
        else -> if (isAr) "${activeDhikr.targetCount} مرة" else "${activeDhikr.targetCount} times"
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = routineTitle, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                if (queueInfo.isNotBlank()) Text(text = queueInfo, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            }
            SuggestionChip(
                onClick = {},
                label = { Text("${if (isAr) "الذكر" else "Dhikr"} ${currentIndex + 1}/${safeItems.size}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), labelColor = MaterialTheme.colorScheme.primary)
            )
        }

        LinearProgressIndicator(
            progress = { ((currentIndex.toFloat() + (currentCount.toFloat() / activeDhikr.targetCount.coerceAtLeast(1))) / safeItems.size).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary
        )

        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF14241B)), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    val title = if (isAr) activeDhikr.titleAr.ifBlank { "ذكر مبارك" } else activeDhikr.titleEn.ifBlank { "Dhikr" }
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    Badge(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
                        Text(repeatBadge, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().heightIn(min = 70.dp, max = 180.dp).verticalScroll(rememberScrollState())) {
                    Text(
                        text = if (isAr || activeDhikr.textEn.isBlank()) activeDhikr.textAr else activeDhikr.textEn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        lineHeight = 26.sp,
                        textAlign = if (isAr) TextAlign.Right else TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (activeDhikr.virtue.isNotBlank()) {
                    Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.08f), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Text(text = activeDhikr.virtue, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f), lineHeight = 16.sp)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(72.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primary).clickable {
                val next = currentCount + 1
                val isGoal = next >= activeDhikr.targetCount
                TasbeehHapticsHelper.performCountFeedback(context, isGoal)
                StatsRepository.recordDhikrCount(1)
                if (isGoal) {
                    if (currentIndex + 1 < safeItems.size) {
                        currentIndex += 1
                        currentCount = 0
                    } else {
                        onAllCompleted()
                    }
                } else {
                    currentCount = next
                }
            }.testTag("lock_sequential_dhikr_btn"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.TouchApp, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$currentCount / ${activeDhikr.targetCount}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    Text(text = if (isAr) "اضغط للتسبيح والانتقال للذكر التالي" else "Tap to count & advance", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                }
            }
        }
    }
}
