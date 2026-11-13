package com.example.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyProgressRecord
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarMonthGrid(
    selectedDateKey: String,
    progressMap: Map<String, DailyProgressRecord>,
    isArabic: Boolean,
    onDateSelected: (String) -> Unit
) {
    val calendar = remember { Calendar.getInstance() }
    val todayKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    var displayedMonth by remember { mutableStateOf(calendar.clone() as Calendar) }
    val monthFormat = remember(isArabic) {
        SimpleDateFormat("MMMM yyyy", if (isArabic) Locale.forLanguageTag("ar") else Locale.ENGLISH)
    }

    val daysInMonth = displayedMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayCal = (displayedMonth.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
    val startDayOfWeek = (firstDayCal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY + 7) % 7
    val year = displayedMonth.get(Calendar.YEAR)
    val month = displayedMonth.get(Calendar.MONTH) + 1

    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { displayedMonth = (displayedMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) } }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", modifier = Modifier.size(16.dp))
                }
                Text(text = monthFormat.format(displayedMonth.time), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { displayedMonth = Calendar.getInstance(); onDateSelected(todayKey) }, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
                        Text(if (isArabic) "اليوم" else "Today", fontSize = 11.sp)
                    }
                    IconButton(onClick = { displayedMonth = (displayedMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) } }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", modifier = Modifier.size(16.dp))
                    }
                }
            }

            val weekDayNames = if (isArabic) listOf("أحد", "إثن", "ثلا", "أرب", "خمي", "جمع", "سبت")
            else listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                weekDayNames.forEach { w -> Text(text = w, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center) }
            }

            val rows = (startDayOfWeek + daysInMonth + 6) / 7
            for (r in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    for (c in 0 until 7) {
                        val dayNum = (r * 7 + c) - startDayOfWeek + 1
                        if (dayNum in 1..daysInMonth) {
                            val dateKey = String.format(Locale.US, "%04d-%02d-%02d", year, month, dayNum)
                            val isSelected = dateKey == selectedDateKey
                            val isToday = dateKey == todayKey
                            val record = progressMap[dateKey]
                            val prayerCount = record?.totalObligatoryPrayed ?: 0
                            val hasQuran = (record?.ayahsRead ?: 0) > 0 || (record?.quranMinutes ?: 0) > 0
                            val hasTasbeeh = (record?.tasbeehCount ?: 0) > 0

                            val cellBg = when {
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                isToday -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f)
                                else -> Color.Transparent
                            }
                            val borderColor = if (isToday) MaterialTheme.colorScheme.tertiary else if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

                            Box(
                                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(cellBg)
                                    .border(if (isToday) 2.dp else if (isSelected) 1.5.dp else 0.dp, borderColor, RoundedCornerShape(8.dp))
                                    .clickable { onDateSelected(dateKey) },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Text(
                                        text = "$dayNum",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected || isToday) FontWeight.ExtraBold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(1.dp), verticalAlignment = Alignment.CenterVertically) {
                                        if (prayerCount > 0) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(if (prayerCount >= 5) Color(0xFF2E7D32) else Color(0xFF66BB6A)))
                                        if (hasQuran) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF0288D1)))
                                        if (hasTasbeeh) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFFFA000)))
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.size(38.dp))
                        }
                    }
                }
            }
        }
    }
}
