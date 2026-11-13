package com.example.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ScreenLockRepository
import com.example.data.local.entity.ScreenLockEntity
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarScheduleViewModel : ViewModel() {
    private val _currentCal = MutableStateFlow(Calendar.getInstance())
    val currentCal: StateFlow<Calendar> = _currentCal.asStateFlow()

    private val _selectedDate = MutableStateFlow(formatDate(Calendar.getInstance()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val locks: StateFlow<List<ScreenLockEntity>> = ScreenLockRepository.locks

    val calendarDays: StateFlow<List<CalendarDayData>> = combine(
        _currentCal, _selectedDate, locks
    ) { cal, selected, _ ->
        buildMonthDays(cal, selected)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedDayLocks: StateFlow<List<ScreenLockEntity>> = combine(
        _selectedDate, locks
    ) { selectedDateStr, allLocks ->
        val cal = Calendar.getInstance().apply {
            val parts = selectedDateStr.split("-")
            if (parts.size == 3) {
                set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            }
        }
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        allLocks.filter { lock ->
            if (lock.specificDate != null && lock.specificDate.isNotEmpty()) {
                lock.specificDate == selectedDateStr
            } else {
                lock.getScheduleSlots().any { it.daysOfWeek.contains(dayOfWeek) }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun nextMonth() {
        val cal = _currentCal.value.clone() as Calendar
        cal.add(Calendar.MONTH, 1)
        _currentCal.value = cal
    }

    fun prevMonth() {
        val cal = _currentCal.value.clone() as Calendar
        cal.add(Calendar.MONTH, -1)
        _currentCal.value = cal
    }

    fun selectToday() {
        val today = Calendar.getInstance()
        _currentCal.value = today
        _selectedDate.value = formatDate(today)
    }

    fun selectDay(day: CalendarDayData) {
        _selectedDate.value = day.dateStr
    }

    private fun buildMonthDays(cal: Calendar, selectedDateStr: String): List<CalendarDayData> {
        val days = mutableListOf<CalendarDayData>()
        val temp = cal.clone() as Calendar
        temp.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = temp.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = temp.get(Calendar.DAY_OF_WEEK) // 1=Sun .. 7=Sat
        val todayStr = formatDate(Calendar.getInstance())

        // Align starting from Saturday (or Sunday)
        val shift = (firstDayOfWeek) % 7 // for Saturday-first
        for (i in 0 until shift) {
            days.add(CalendarDayData(0, "", 0, isCurrentMonth = false, isToday = false, isSelected = false, dots = emptyList()))
        }

        for (d in 1..maxDays) {
            temp.set(Calendar.DAY_OF_MONTH, d)
            val dateStr = formatDate(temp)
            val dayOfWeek = temp.get(Calendar.DAY_OF_WEEK)
            val dots = ScreenLockRepository.getDotsForDay(dayOfWeek, dateStr)
            days.add(
                CalendarDayData(
                    dayNumber = d,
                    dateStr = dateStr,
                    dayOfWeek = dayOfWeek,
                    isCurrentMonth = true,
                    isToday = dateStr == todayStr,
                    isSelected = dateStr == selectedDateStr,
                    dots = dots
                )
            )
        }
        return days
    }

    private fun formatDate(cal: Calendar): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }
}
