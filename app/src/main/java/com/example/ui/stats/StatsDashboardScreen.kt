package com.example.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyProgressRepository
import com.example.data.StatsRepository
import com.example.model.ActivityCategory
import com.example.util.AppStrings

enum class StatsViewType(val id: String, val labelAr: String, val labelEn: String) {
    CALENDAR("calendar", "تقويم شهري", "Calendar"),
    WEEKLY("weekly", "رسم أسبوعي", "Weekly"),
    TIMELINE("timeline", "سجل اليوم", "Day Log")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsDashboardScreen() {
    val isAr = AppStrings.isArabic
    val stats by StatsRepository.stats.collectAsState()
    val progressMap by DailyProgressRepository.progressMap.collectAsState()
    var selectedDateKey by remember { mutableStateOf(DailyProgressRepository.todayKey()) }
    var selectedViewType by remember { mutableStateOf(StatsViewType.CALENDAR) }
    var selectedCategory by remember { mutableStateOf(ActivityCategory.ALL) }

    val currentRecord = remember(progressMap, selectedDateKey) {
        DailyProgressRepository.getRecord(selectedDateKey)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
            .padding(bottom = 72.dp)
            .testTag("stats_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = if (isAr) "إحصائيات الإنجاز والتقويم" else "Progress & Calendar Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        BadgesGalleryCard(isArabic = isAr)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val viewTypes = listOf(
                StatsViewType.CALENDAR to Icons.Default.CalendarMonth,
                StatsViewType.WEEKLY to Icons.Default.ShowChart,
                StatsViewType.TIMELINE to Icons.Default.FormatListBulleted
            )
            viewTypes.forEach { (vt, icon) ->
                FilterChip(
                    selected = selectedViewType == vt,
                    onClick = { selectedViewType = vt },
                    leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    label = { Text(if (isAr) vt.labelAr else vt.labelEn, fontSize = 11.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            ActivityCategory.values().forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(if (isAr) cat.labelAr else cat.labelEn, fontSize = 10.sp) }
                )
            }
        }

        when (selectedViewType) {
            StatsViewType.CALENDAR -> {
                CalendarMonthGrid(
                    selectedDateKey = selectedDateKey,
                    progressMap = progressMap,
                    isArabic = isAr,
                    onDateSelected = { selectedDateKey = it }
                )
            }
            StatsViewType.WEEKLY -> {
                WeeklyProgressCard(progressMap = progressMap, isArabic = isAr)
            }
            StatsViewType.TIMELINE -> {}
        }

        DailyProgressTimeline(
            dateKey = selectedDateKey,
            record = currentRecord,
            filterCategory = selectedCategory,
            isArabic = isAr
        )

        HorizontalDivider()
        StatsLocksCalendarSection(isArabic = isAr)
    }
}
