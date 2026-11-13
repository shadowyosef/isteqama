package com.example.ui.adhkar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DhikrItem
import com.example.ui.quran.QuranCardShareHelper
import com.example.util.AppStrings
import com.example.util.TasbeehHapticsHelper

@Composable
fun DhikrItemCard(
    item: DhikrItem,
    onIncrement: () -> Unit,
    onEdit: () -> Unit
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val progress = (item.currentCount.toFloat() / item.targetCount).coerceIn(0f, 1f)

    val repeatText = when (item.targetCount) {
        1 -> if (isAr) "مرة واحدة" else "1 time"
        2 -> if (isAr) "مرتان" else "2 times"
        in 3..10 -> if (isAr) "${item.targetCount} مرات" else "${item.targetCount} times"
        else -> if (isAr) "${item.targetCount} مرة" else "${item.targetCount} times"
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp).testTag("dhikr_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    val title = if (isAr) item.titleAr.ifEmpty { "ذكر نبوي" } else item.titleEn.ifEmpty { "Prophetic Dhikr" }
                    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    SuggestionChip(
                        onClick = {},
                        label = { Text(repeatText, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            labelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = null
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        QuranCardShareHelper.shareOrnateStoryCard(
                            context, item.titleAr.ifEmpty { "ذكر مأثور" }, item.textAr, item.virtue.ifBlank { "أذكار المسلم اليومية" }
                        )
                    }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Share Story Card", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = {
                        com.example.ui.adhkar.DhikrToastScheduleHelper.triggerToastNow(context, item)
                    }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.NearMe, contentDescription = "Toast", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Text(
                text = if (isAr) item.textAr else item.textEn,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 28.sp,
                textAlign = if (isAr) TextAlign.Right else TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            if (item.virtue.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Text(text = item.virtue, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.weight(1f).height(8.dp).padding(end = 14.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                Button(
                    onClick = {
                        val isGoal = item.currentCount + 1 >= item.targetCount
                        TasbeehHapticsHelper.performCountFeedback(context, isGoal)
                        onIncrement()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("count_btn_${item.id}")
                ) {
                    if (item.isCompleted) {
                        Icon(Icons.Default.Check, contentDescription = "Done", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isAr) "تم الإتمام" else "Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("${item.currentCount} / ${item.targetCount}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
