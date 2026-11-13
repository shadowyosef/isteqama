package com.example.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AppStrings

@Composable
fun CalendarMonthGrid(
    days: List<CalendarDayData>,
    onSelectDay: (CalendarDayData) -> Unit,
    modifier: Modifier = Modifier
) {
    val weekDays = if (AppStrings.isArabic) {
        listOf("سبت", "أحد", "اثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة")
    } else {
        listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
    }

    Card(
        modifier = modifier.fillMaxWidth().testTag("calendar_month_grid"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDays.forEach { dayName ->
                    Text(
                        text = dayName,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            days.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    week.forEach { day ->
                        DayCell(day = day, onSelect = { onSelectDay(day) }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: CalendarDayData,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = day.isSelected
    val isToday = day.isToday
    val todayAccent = MaterialTheme.colorScheme.tertiary
    val bgColor = when {
        !day.isCurrentMonth -> Color.Transparent
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isToday -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f)
        else -> Color.Transparent
    }
    val borderColor = when {
        !day.isCurrentMonth -> Color.Transparent
        isToday -> todayAccent
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    val borderWidth = if (isToday) 2.dp else if (isSelected) 1.5.dp else 0.dp

    Box(
        modifier = modifier
            .height(54.dp)
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(borderWidth, borderColor, RoundedCornerShape(10.dp))
            .clickable(enabled = day.isCurrentMonth) { onSelect() }
            .testTag("calendar_day_${day.dateStr}"),
        contentAlignment = Alignment.Center
    ) {
        if (isToday && day.isCurrentMonth) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(todayAccent)
                    .testTag("calendar_today_marker")
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                text = if (day.dayNumber > 0) day.dayNumber.toString() else "",
                fontSize = 13.sp,
                fontWeight = if (isToday || isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                color = when {
                    !day.isCurrentMonth -> Color.Transparent
                    isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                    isToday -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            if (isToday && day.isCurrentMonth) {
                Text(
                    text = if (AppStrings.isArabic) "اليوم" else "Now",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color = todayAccent,
                    lineHeight = 8.sp
                )
            }
            if (day.dots.isNotEmpty() && day.isCurrentMonth) {
                Spacer(modifier = Modifier.height(1.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                    day.dots.take(3).forEach { dotColor ->
                        Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(dotColor))
                    }
                }
            }
        }
    }
}
