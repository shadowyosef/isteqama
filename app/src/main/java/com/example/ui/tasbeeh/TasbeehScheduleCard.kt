package com.example.ui.tasbeeh

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.unified.CountdownBadge
import com.example.util.CountdownUtils

@Composable
fun TasbeehScheduleCard(
    item: TasbeehScheduleModel,
    isArabic: Boolean,
    onToggle: (Boolean) -> Unit,
    onTest: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val accentColor = when (item.themeColor) {
        TasbeehThemeColor.GOLD -> Color(0xFFD4AF37)
        TasbeehThemeColor.OCEAN -> Color(0xFF1976D2)
        TasbeehThemeColor.PURPLE -> Color(0xFF8E24AA)
        TasbeehThemeColor.ROSE -> Color(0xFFE91E63)
        else -> Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(10.dp).background(accentColor, androidx.compose.foundation.shape.CircleShape))
                    val phraseText = if (isArabic) item.phraseAr else item.phraseEn
                    val extra = if (item.getPhrases().size > 1) " (+${item.getPhrases().size - 1})" else ""
                    Text(text = phraseText + extra, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                CountdownBadge(
                    remainingMillisProvider = { CountdownUtils.getRemainingMillisForTasbeeh(item) },
                    isArabic = isArabic,
                    isEnabled = item.isEnabled,
                    accentColor = accentColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Switch(checked = item.isEnabled, onCheckedChange = onToggle, modifier = Modifier.height(24.dp))
            }

            Text(text = item.getIntervalSummary(isArabic), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                AssistChip(onClick = {}, label = { Text(getStyleLabel(item.deliveryStyle, isArabic), fontSize = 10.sp) }, modifier = Modifier.height(26.dp))
                AssistChip(onClick = {}, label = { Text("${item.durationSeconds}s", fontSize = 10.sp) }, modifier = Modifier.height(26.dp))
                if (item.getPhrases().size > 1) {
                    AssistChip(
                        onClick = {},
                        label = { Text(if (item.isRandomOrder) (if (isArabic) "عشوائي" else "Random") else (if (isArabic) "متسلسل" else "Sequential"), fontSize = 10.sp) },
                        modifier = Modifier.height(26.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onTest, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Test", tint = accentColor)
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

private fun getStyleLabel(style: TasbeehDeliveryStyle, isAr: Boolean): String {
    return when (style) {
        TasbeehDeliveryStyle.TOAST_POPUP -> if (isAr) "توست عائم" else "Floating Toast"
        TasbeehDeliveryStyle.NOTIFICATION -> if (isAr) "إشعار عادي" else "Notification"
        TasbeehDeliveryStyle.BOTH -> if (isAr) "توست + إشعار" else "Toast & Notif"
        TasbeehDeliveryStyle.FULLSCREEN -> if (isAr) "شاشة كاملة" else "Fullscreen"
    }
}
