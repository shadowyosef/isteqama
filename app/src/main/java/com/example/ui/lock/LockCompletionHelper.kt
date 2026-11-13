package com.example.ui.lock

import android.content.Context
import com.example.data.PrayerTimesRepository
import com.example.data.ScreenLockRepository
import com.example.data.UnifiedRoutineRepository
import com.example.data.local.entity.ScreenLockEntity
import com.example.model.PrayerType
import com.example.util.LockScheduleTracker
import com.example.util.LockSoundManager

object LockCompletionHelper {
    fun completeLock(context: Context, lock: ScreenLockEntity, onComplete: () -> Unit) {
        LockSoundManager.playUnlockChime(context)
        if (lock.id.startsWith("lock_routine_")) {
            val rId = lock.id.removePrefix("lock_routine_")
            UnifiedRoutineRepository.incrementCount(rId)
            UnifiedRoutineRepository.recordTriggered(rId, System.currentTimeMillis())
        } else if (lock.id.startsWith("prayer_lock_")) {
            val pId = lock.id.removePrefix("prayer_lock_")
            PrayerType.values().find { it.id == pId }?.let { pt ->
                PrayerTimesRepository.togglePrayerPrayed(context, pt, true)
            }
        }
        if (!lock.id.startsWith("preview_")) {
            LockScheduleTracker.setLockCompleted(context, lock.id, lock.getScheduleSlots())
            ScreenLockRepository.recordLockCompleted(lock)
        }
        onComplete()
    }
}
