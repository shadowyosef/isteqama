package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_preferences")
data class AppPreferencesEntity(
    @PrimaryKey val id: Int = 1,
    val notificationStyle: String = "GENTLE_ISLAMIC_TONE",
    val reminderFrequency: String = "DAILY",
    val dashboardViewMode: String = "CARD",
    val isDarkMode: Boolean = false,
    val isArabic: Boolean = true,
    val customIntervalMinutes: Int = 60,
    val isPersistentNotification: Boolean = true,
    val lockEnforcementMode: String = "SEAMLESS_OVERLAY",
    val isFirstRunSetupCompleted: Boolean = false,
    val quranVerificationMode: String = "INTERACTION_TRACK",
    val securityPin: String = ""
)
