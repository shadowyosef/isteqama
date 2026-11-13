package com.example.model

data class UserStats(
    val currentStreak: Int = 3,
    val bestStreak: Int = 14,
    val totalQuranMinutes: Int = 120,
    val ayahsReadToday: Int = 28,
    val totalAyahsRead: Int = 412,
    val dhikrCompletedToday: Int = 99,
    val totalDhikrCount: Int = 1450,
    val locksCompleted: Int = 8,
    val emergencyBypasses: Int = 1,
    val khatmaProgressAyahs: Int = 620,
    val weeklyMinutes: List<Int> = listOf(25, 40, 30, 45, 20, 35, 50)
) {
    val totalQuranAyahs: Int = 6236
    val khatmaPercentage: Float
        get() = (khatmaProgressAyahs.toFloat() / totalQuranAyahs * 100f).coerceIn(0f, 100f)
}
