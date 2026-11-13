package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarMonthHeader(
    currentCalendar: Calendar,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = if (AppStrings.isArabic) Locale("ar") else Locale("en")
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", locale)
    val formattedMonth = monthYearFormat.format(currentCalendar.time)

    Row(
        modifier = modifier.fillMaxWidth().testTag("calendar_month_header"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formattedMonth,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = onToday,
                modifier = Modifier.size(32.dp).testTag("calendar_today_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Today,
                    contentDescription = "Today",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier.size(36.dp).testTag("calendar_prev_month_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }
            IconButton(
                onClick = onNextMonth,
                modifier = Modifier.size(36.dp).testTag("calendar_next_month_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }
    }
}
