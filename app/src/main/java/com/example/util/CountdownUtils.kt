package com.example.util

import com.example.data.local.entity.ScreenLockEntity
import com.example.model.TasbeehScheduleModel
import com.example.model.UnifiedRoutine
import java.util.Calendar

object CountdownUtils {

    fun calculateNextTrigger(baseTime: Long, intervalMs: Long, nowMs: Long): Long {
        return if (nowMs < baseTime + intervalMs) baseTime + intervalMs
        else baseTime + ((nowMs - baseTime) / intervalMs + 1) * intervalMs
    }

    fun getRemainingMillisForTasbeeh(item: TasbeehScheduleModel, now: Calendar = Calendar.getInstance()): Long {
        if (!item.isEnabled) return -1L
        val intervalSecs = item.getEffectiveIntervalSeconds()
        if (intervalSecs > 0) {
            val nowMs = now.timeInMillis
            val intervalMs = intervalSecs * 1000L
            val base = if (item.startFromNow) {
                if (item.lastTriggeredMillis > 0) item.lastTriggeredMillis else item.createdAtMillis
            } else {
                val startCal = (now.clone() as Calendar).apply {
                    set(Calendar.HOUR_OF_DAY, item.startHour)
                    set(Calendar.MINUTE, item.startMinute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                startCal.timeInMillis
            }
            val nextTriggerMs = calculateNextTrigger(base, intervalMs, nowMs)
            val remaining = nextTriggerMs - nowMs
            return if (remaining <= 0) 1000L else remaining
        }

        val times = item.getSpecificTimesList()
        var minDiff = Long.MAX_VALUE
        for (t in times) {
            val parts = t.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: item.hour
            val m = parts.getOrNull(1)?.toIntOrNull() ?: item.minute
            val diff = getRemainingMillisForTime(h, m, now)
            if (diff in 0 until minDiff) minDiff = diff
        }
        return if (minDiff == Long.MAX_VALUE) -1L else minDiff
    }

    fun getRemainingMillisForRoutine(routine: UnifiedRoutine, now: Calendar = Calendar.getInstance()): Long {
        if (!routine.isLockActive) return -1L
        if (routine.repeatIntervalMinutes > 0) {
            val nowMs = now.timeInMillis
            val intervalMs = routine.repeatIntervalMinutes * 60 * 1000L
            val base = if (routine.lastTriggeredMillis > 0) {
                routine.lastTriggeredMillis
            } else if (routine.startFromNow) {
                if (routine.createdAtMillis > 0) routine.createdAtMillis else nowMs
            } else {
                val startCal = (now.clone() as Calendar).apply {
                    set(Calendar.HOUR_OF_DAY, routine.triggerHour)
                    set(Calendar.MINUTE, routine.triggerMinute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                startCal.timeInMillis
            }
            val nextTriggerMs = calculateNextTrigger(base, intervalMs, nowMs)
            val remaining = nextTriggerMs - nowMs
            return if (remaining <= 0) 0L else remaining
        }
        return getRemainingMillisForTime(routine.triggerHour, routine.triggerMinute, now)
    }

    fun getRemainingMillisForLock(lock: ScreenLockEntity, now: Calendar = Calendar.getInstance(), context: android.content.Context? = null): Long {
        if (!lock.isEnabled) return -1L
        val targetCtx = context ?: com.example.data.ScreenLockRepository.appContext
        var minDiff = Long.MAX_VALUE
        for (slot in lock.getScheduleSlots()) {
            val diff = if (slot.intervalMinutes > 0) {
                val nowMs = now.timeInMillis
                val intervalMs = slot.intervalMinutes * 60 * 1000L
                val lastSlot = if (targetCtx != null) LockScheduleTracker.getLastTriggerTime(targetCtx, slot.id) else 0L
                val lastLock = if (targetCtx != null) LockScheduleTracker.getLastCompletedTime(targetCtx, lock.id) else 0L
                val last = maxOf(lastSlot, lastLock)
                val base = if (last > 0L) last else (if (lock.createdAt > 0) lock.createdAt else nowMs)
                val nextTriggerMs = base + intervalMs
                val rem = nextTriggerMs - nowMs
                if (rem <= 0) 0L else rem
            } else {
                val completedToday = if (targetCtx != null) {
                    LockScheduleTracker.isSlotCompletedToday(targetCtx, slot.id, now.get(Calendar.DAY_OF_YEAR))
                } else false
                getRemainingMillisForTime(slot.hour, slot.minute, now, completedToday)
            }
            if (diff in 0 until minDiff) minDiff = diff
        }
        return if (minDiff == Long.MAX_VALUE) -1L else minDiff
    }

    fun getRemainingMillisForTime(hour: Int, minute: Int, now: Calendar = Calendar.getInstance(), completedToday: Boolean = false): Long {
        val target = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (completedToday || target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    fun formatCountdown(diffMillis: Long, isArabic: Boolean): String {
        if (diffMillis < 0L) return if (isArabic) "معطل" else "Off"
        if (diffMillis <= 0L) return if (isArabic) "الآن" else "Now"
        val totalSecs = (diffMillis + 999L) / 1000L
        val hours = totalSecs / 3600
        val mins = (totalSecs % 3600) / 60
        val secs = totalSecs % 60
        return when {
            hours >= 24 -> if (isArabic) "بعد ${hours / 24} يوم" else "In ${hours / 24}d"
            hours > 0 -> if (isArabic) "بعد ${hours}س ${mins}د" else "In ${hours}h ${mins}m"
            mins > 0 -> if (isArabic) "بعد ${mins}د ${secs}ث" else "In ${mins}m ${secs}s"
            else -> if (isArabic) "بعد ${secs}ث" else "In ${secs}s"
        }
    }
}
