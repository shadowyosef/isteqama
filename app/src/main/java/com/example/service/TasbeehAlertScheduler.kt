package com.example.service

import android.content.Context
import com.example.data.TasbeehScheduleRepository
import com.example.util.CountdownUtils
import com.example.util.LockScheduleTracker
import kotlinx.coroutines.*
import java.util.Calendar

object TasbeehAlertScheduler {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var job: Job? = null
    private var appContext: Context? = null
    private val lastTriggeredMap = mutableMapOf<String, Long>()

    fun start(context: Context) {
        appContext = context.applicationContext
        if (job != null && job?.isActive == true) return
        job = scope.launch {
            while (isActive) {
                try { appContext?.let { checkAndTrigger(it) } } catch (_: Exception) {}
                delay(1000L)
            }
        }
    }

    fun checkAndTrigger(context: Context) {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance().apply { timeInMillis = now }
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)

        for (item in TasbeehScheduleRepository.schedules.value) {
            if (!item.isEnabled) continue
            if (LockScheduleTracker.isHourInRestWindow(hour, item.quietHoursEnabled, item.quietHoursStart, item.quietHoursEnd)) continue
            if (LockScheduleTracker.isDailyLimitReached(context, item.id, item.maxDailyRepeats, dayOfYear)) continue

            val remaining = CountdownUtils.getRemainingMillisForTasbeeh(item)
            val intervalSecs = item.getEffectiveIntervalSeconds()

            val shouldTrigger = if (intervalSecs > 0) {
                val last = lastTriggeredMap[item.id]
                    ?: if (item.lastTriggeredMillis > 0) item.lastTriggeredMillis else item.createdAtMillis
                val intervalMs = intervalSecs * 1000L
                val timePassed = (now - last) >= intervalMs
                val countdownFinished = remaining in 0..1200L
                (timePassed || countdownFinished) && (now - (lastTriggeredMap[item.id] ?: 0L) >= 2000L)
            } else {
                val currentFormatted = String.format("%02d:%02d", hour, minute)
                val isMatch = item.getSpecificTimesList().contains(currentFormatted)
                val last = lastTriggeredMap[item.id] ?: 0L
                isMatch && (now - last) >= 60_000L
            }

            if (shouldTrigger) {
                lastTriggeredMap[item.id] = now
                LockScheduleTracker.incrementDailyTriggerCount(context, item.id, dayOfYear)
                TasbeehScheduleRepository.recordTriggered(item.id, now)
                FloatingTasbeehToastManager.triggerTasbeehAlert(context, item)
            }
        }
    }
}
