package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateKey: String, // e.g. "2026-10-30"
    val ayahsReadToday: Int = 0,
    val minutesSpentReading: Int = 0,
    val dhikrCompletedToday: Int = 0,
    val routinesCompletedToday: Int = 0,
    val emergencyBypassesToday: Int = 0,
    val totalLifetimeAyahs: Int = 0,
    val totalLifetimeDhikr: Int = 0,
    val streakDays: Int = 1,
    val updatedAt: Long = System.currentTimeMillis()
)
