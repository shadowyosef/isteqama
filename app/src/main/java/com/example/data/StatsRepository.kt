package com.example.data

import com.example.model.UserStats
import kotlinx.coroutines.flow.MutableStateFlow

object StatsRepository {
    val stats = MutableStateFlow(UserStats(
        currentStreak = 1,
        bestStreak = 1,
        totalQuranMinutes = 0,
        ayahsReadToday = 0,
        totalAyahsRead = 0,
        dhikrCompletedToday = 0,
        totalDhikrCount = 0,
        locksCompleted = 0,
        emergencyBypasses = 0,
        khatmaProgressAyahs = 0,
        weeklyMinutes = listOf(0, 0, 0, 0, 0, 0, 0)
    ))

    fun recordAyahsRead(count: Int) {
        val current = stats.value
        val newProgress = (current.khatmaProgressAyahs + count).coerceAtMost(current.totalQuranAyahs)
        stats.value = current.copy(
            ayahsReadToday = current.ayahsReadToday + count,
            totalAyahsRead = current.totalAyahsRead + count,
            khatmaProgressAyahs = newProgress
        )
        DailyProgressRepository.recordQuranProgress(ayahs = count, minutes = 1)
    }

    fun recordDhikrCount(count: Int) {
        val current = stats.value
        stats.value = current.copy(
            dhikrCompletedToday = current.dhikrCompletedToday + count,
            totalDhikrCount = current.totalDhikrCount + count
        )
        DailyProgressRepository.recordTasbeehProgress(count = count)
    }

    fun recordQuranMinutes(minutes: Int) {
        val current = stats.value
        stats.value = current.copy(
            totalQuranMinutes = current.totalQuranMinutes + minutes
        )
        DailyProgressRepository.recordQuranProgress(ayahs = 0, minutes = minutes)
    }

    fun recordLockCompleted() {
        val current = stats.value
        stats.value = current.copy(
            locksCompleted = current.locksCompleted + 1,
            currentStreak = current.currentStreak + 1
        )
        DailyProgressRepository.recordLockProgress("قفل شاشة مكتمل", "Completed Screen Lock")
    }

    fun recordEmergencyBypass() {
        val current = stats.value
        stats.value = current.copy(
            emergencyBypasses = current.emergencyBypasses + 1
        )
    }
}
