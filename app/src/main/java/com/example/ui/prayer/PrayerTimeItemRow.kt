package com.example.ui.prayer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyProgressRepository
import com.example.data.PrayerRecordRepository
import com.example.model.PrayerDailyRecord
import com.example.model.PrayerStatus
import com.example.model.PrayerTimeItem

@Composable
fun PrayerTimeItemChip(
    item: PrayerTimeItem,
    isAr: Boolean,
    onTogglePrayed: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var showCrudDialog by remember { mutableStateOf(false) }
    val isNext = item.isNext
    val isObligatory = item.type.isObligatoryPrayer
    val isFuture = PrayerRecordRepository.isFuturePrayerToday(item)

    val containerColor = when {
        isNext -> MaterialTheme.colorScheme.primaryContainer
        item.isPrayed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        isNext -> MaterialTheme.colorScheme.onPrimaryContainer
        item.isPrayed -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier.width(62.dp).padding(vertical = 2.dp).clickable { showCrudDialog = true }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = if (isAr) item.type.nameAr else item.type.nameEn,
                fontSize = 10.sp,
                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
            Text(
                text = item.timeFormatted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            if (isObligatory) {
                Box(
                    modifier = Modifier.size(20.dp).clip(CircleShape)
                        .background(if (item.isPrayed) MaterialTheme.colorScheme.primary else if (isFuture) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                        .border(1.dp, if (item.isPrayed) MaterialTheme.colorScheme.primary else if (isFuture) MaterialTheme.colorScheme.outline.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline, CircleShape)
                        .clickable {
                            if (isFuture) {
                                val msg = if (isAr) "لم يحن موعد صلاة ${item.type.nameAr} بعد اليوم!" else "${item.type.nameEn} prayer has not arrived yet!"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                showCrudDialog = true
                            } else {
                                if (!item.isPrayed) {
                                    val rec = PrayerDailyRecord(
                                        dateKey = DailyProgressRepository.todayKey(),
                                        prayerId = item.type.id,
                                        isPrayed = true,
                                        status = PrayerStatus.ON_TIME
                                    )
                                    PrayerRecordRepository.markOrUpdatePrayer(context, rec)
                                } else {
                                    PrayerRecordRepository.deletePrayerMark(context, DailyProgressRepository.todayKey(), item.type)
                                }
                                onTogglePrayed(!item.isPrayed)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isPrayed) {
                        Icon(Icons.Default.Check, contentDescription = "Prayed", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(14.dp))
                    } else if (isFuture) {
                        Icon(Icons.Default.Lock, contentDescription = "Future Prayer", tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.size(11.dp))
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showCrudDialog) {
        PrayerMarkCrudDialog(
            prayerType = item.type,
            timeFormatted = item.timeFormatted,
            isFuturePrayer = isFuture,
            isArabic = isAr,
            onDismiss = { showCrudDialog = false }
        )
    }
}
