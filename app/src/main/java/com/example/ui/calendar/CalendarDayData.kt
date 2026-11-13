package com.example.ui.calendar

import androidx.compose.ui.graphics.Color

data class CalendarDayData(
    val dayNumber: Int,
    val dateStr: String,
    val dayOfWeek: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val dots: List<Color>
)

object CalendarColors {
    val QURAN_DOT = Color(0xFF2E7D32)   // Green
    val DHIKR_DOT = Color(0xFFFFA000)   // Amber / Yellow
    val TASBEEH_DOT = Color(0xFF1976D2) // Blue
}
