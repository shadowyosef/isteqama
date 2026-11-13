package com.example.service

import android.content.Context
import com.example.data.ScreenLockRepository
import com.example.data.TasbeehScheduleRepository
import com.example.ui.lock.ActiveLockActivity
import com.example.util.LockPersistenceManager
import com.example.util.LockScheduleTracker
import com.example.util.SpiritualNotificationManager
import java.util.Calendar

object ScheduledTriggerEvaluator {
    private val lastTasbeehTriggerTime = mutableMapOf<String, Long>()

    fun evaluate(context: Context, hour: Int, minute: Int, dayOfWeek: Int, checkSpecificTimes: Boolean = true) {
        evaluateScreenLocks(context, hour, minute, dayOfWeek, checkSpecificTimes)
        if (checkSpecificTimes) {
            evaluateTasbeehSchedules(context, hour, minute)
            com.example.util.FloatingAyahToastHelper.checkHourlyTrigger(
                context, hour, minute, Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            )
        }
    }

    private fun evaluateScreenLocks(context: Context, hour: Int, minute: Int, dayOfWeek: Int, checkSpecificTimes: Boolean) {
        if (LockScheduleTracker.isGlobalCooldownActive(context)) return
        if (ScreenLockRepository.currentActiveLock.value != null || LockPersistenceManager.isLockActive(context)) return

        LockScheduleTracker.popDueSnoozedLock(context)?.let { snoozed ->
            ActiveLockActivity.launch(context, snoozed)
            return
        }

        val now = System.currentTimeMillis()
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        val matchingLock = ScreenLockRepository.locks.value.find { lock ->
            if (!lock.isEnabled || lock.id == "lock_morning_adhkar" || lock.id == "lock_evening_adhkar") return@find false
            val slots = lock.getScheduleSlots()
            slots.any { slot ->
                if (LockScheduleTracker.isHourInRestWindow(hour, slot.quietHoursEnabled, slot.quietHoursStart, slot.quietHoursEnd)) {
                    return@any false
                }
                if (LockScheduleTracker.isDailyLimitReached(context, slot.id, slot.maxDailyRepeats, dayOfYear)) {
                    return@any false
                }
                if (slot.intervalMinutes > 0) {
                    val intervalMs = slot.intervalMinutes.coerceAtLeast(1) * 60_000L
                    val last = LockScheduleTracker.getLastTriggerTime(context, slot.id)
                    if (last == 0L) {
                        LockScheduleTracker.setLastTriggerTime(context, slot.id, now)
                        false
                    } else {
                        val shouldTrigger = (now - last) >= intervalMs
                        if (shouldTrigger) {
                            LockScheduleTracker.setLastTriggerTime(context, slot.id, now)
                            LockScheduleTracker.incrementDailyTriggerCount(context, slot.id, dayOfYear)
                        }
                        shouldTrigger
                    }
                } else if (checkSpecificTimes) {
                    if (LockScheduleTracker.isSlotCompletedToday(context, slot.id, dayOfYear)) return@any false
                    val isMatch = slot.hour == hour && slot.minute == minute && slot.daysOfWeek.contains(dayOfWeek)
                    val last = LockScheduleTracker.getLastTriggerTime(context, slot.id)
                    if (isMatch && (now - last) >= 60_000L) {
                        LockScheduleTracker.setLastTriggerTime(context, slot.id, now)
                        LockScheduleTracker.incrementDailyTriggerCount(context, slot.id, dayOfYear)
                        true
                    } else false
                } else false
            }
        }

        if (matchingLock != null && ScreenLockRepository.currentActiveLock.value == null) {
            SpiritualNotificationManager.sendReminder(
                context, matchingLock.title,
                "حان موعد إتمام وردك الإيماني لفك قفل الشاشة",
                matchingLock.lockType.equals("QURAN", ignoreCase = true)
            )
            ActiveLockActivity.launch(context, matchingLock)
        }
    }

    private fun evaluateTasbeehSchedules(context: Context, hour: Int, minute: Int) {
        val now = System.currentTimeMillis()
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        TasbeehScheduleRepository.schedules.value.forEach { item ->
            if (!item.isEnabled) return@forEach
            if (LockScheduleTracker.isHourInRestWindow(hour, item.quietHoursEnabled, item.quietHoursStart, item.quietHoursEnd)) return@forEach
            if (LockScheduleTracker.isDailyLimitReached(context, item.id, item.maxDailyRepeats, dayOfYear)) return@forEach
            val intervalSecs = item.getEffectiveIntervalSeconds()

            if (intervalSecs > 0) {
                val intervalMs = intervalSecs * 1000L
                val last = lastTasbeehTriggerTime[item.id] ?: if (item.lastTriggeredMillis > 0) item.lastTriggeredMillis else item.createdAtMillis
                val shouldTrigger = if (item.startFromNow) {
                    (now - last) >= intervalMs
                } else {
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, item.startHour)
                        set(Calendar.MINUTE, item.startMinute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    if (now >= cal.timeInMillis) (now - last) >= intervalMs else false
                }
                if (shouldTrigger) {
                    lastTasbeehTriggerTime[item.id] = now
                    LockScheduleTracker.incrementDailyTriggerCount(context, item.id, dayOfYear)
                    FloatingTasbeehToastManager.triggerTasbeehAlert(context, item)
                }
            } else {
                val currentFormatted = String.format("%02d:%02d", hour, minute)
                val isExactMatch = item.getSpecificTimesList().contains(currentFormatted)
                val last = lastTasbeehTriggerTime[item.id] ?: 0L
                if (isExactMatch && (now - last) >= 60_000L) {
                    lastTasbeehTriggerTime[item.id] = now
                    LockScheduleTracker.incrementDailyTriggerCount(context, item.id, dayOfYear)
                    FloatingTasbeehToastManager.triggerTasbeehAlert(context, item)
                }
            }
        }
    }
}
