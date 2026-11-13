package com.example.util

import com.example.model.SleepTimerMode
import com.example.model.SleepTimerState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

object QuranSleepTimerManager {
    val timerState = MutableStateFlow(SleepTimerState())
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun startDurationTimer(minutes: Int) {
        cancelTimer()
        val totalSec = minutes * 60
        timerState.value = SleepTimerState(
            mode = SleepTimerMode.DURATION_MINUTES,
            remainingSeconds = totalSec,
            totalSeconds = totalSec
        )
        timerJob = scope.launch {
            while (timerState.value.remainingSeconds > 0) {
                delay(1000L)
                val current = timerState.value.remainingSeconds - 1
                if (current <= 0) {
                    timerState.value = SleepTimerState(mode = SleepTimerMode.OFF)
                    withContext(Dispatchers.Main) {
                        AudioPlayerManager.stop()
                    }
                    break
                } else {
                    timerState.value = timerState.value.copy(remainingSeconds = current)
                }
            }
        }
    }

    fun startEndOfSurahTimer(surahNumber: Int) {
        cancelTimer()
        timerState.value = SleepTimerState(
            mode = SleepTimerMode.END_OF_SURAH,
            targetSurah = surahNumber
        )
    }

    fun onSurahCompleted(surahNumber: Int) {
        val state = timerState.value
        if (state.mode == SleepTimerMode.END_OF_SURAH && (state.targetSurah == null || state.targetSurah == surahNumber)) {
            cancelTimer()
            AudioPlayerManager.stop()
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        timerState.value = SleepTimerState(mode = SleepTimerMode.OFF)
    }

    fun formatRemainingTime(): String {
        val sec = timerState.value.remainingSeconds
        val m = sec / 60
        val s = sec % 60
        return String.format("%02d:%02d", m, s)
    }
}
