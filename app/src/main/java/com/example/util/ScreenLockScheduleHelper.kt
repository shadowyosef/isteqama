package com.example.util

import androidx.compose.ui.graphics.Color
import com.example.data.ScreenLockRepository
import com.example.data.local.entity.ScreenLockEntity
import java.util.Calendar

object ScreenLockScheduleHelper {
    fun getDotsForDay(locks: List<ScreenLockEntity>, dayOfWeek: Int, dateStr: String): List<Color> {
        val enabledLocks = locks.filter { it.isEnabled }
        val matching = enabledLocks.filter { lock ->
            if (!lock.specificDate.isNullOrEmpty()) lock.specificDate == dateStr
            else lock.getScheduleSlots().any { it.daysOfWeek.contains(dayOfWeek) }
        }
        return matching.map { lock ->
            when (lock.lockType.uppercase()) {
                "QURAN" -> ScreenLockRepository.COLOR_QURAN
                "DHIKR" -> ScreenLockRepository.COLOR_DHIKR
                else -> ScreenLockRepository.COLOR_TASBEEH
            }
        }.distinct()
    }

    fun getRemainingMillisToNextLock(locks: List<ScreenLockEntity>, context: android.content.Context? = null): Pair<ScreenLockEntity?, Long> {
        val enabledLocks = locks.filter { it.isEnabled }
        if (enabledLocks.isEmpty()) return null to -1L
        val now = Calendar.getInstance()
        var closestLock: ScreenLockEntity? = null
        var minDiff = Long.MAX_VALUE
        for (lock in enabledLocks) {
            val diff = CountdownUtils.getRemainingMillisForLock(lock, now, context)
            if (diff in 0 until minDiff) {
                minDiff = diff
                closestLock = lock
            }
        }
        return closestLock to (if (minDiff == Long.MAX_VALUE) -1L else minDiff)
    }
}
