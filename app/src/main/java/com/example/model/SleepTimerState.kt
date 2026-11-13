package com.example.model

enum class SleepTimerMode {
    OFF,
    DURATION_MINUTES,
    END_OF_SURAH
}

data class SleepTimerState(
    val mode: SleepTimerMode = SleepTimerMode.OFF,
    val targetSurah: Int? = null,
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 0
) {
    val isActive: Boolean get() = mode != SleepTimerMode.OFF
}
