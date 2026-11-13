package com.example.ui.lock

import android.content.Context
import android.content.Intent
import com.example.data.ScreenLockRepository
import com.example.data.UnifiedRoutineRepository
import com.example.data.local.entity.ScreenLockEntity

object ActiveLockIntentResolver {
    fun resolve(context: Context, intent: Intent?) {
        if (intent == null) return
        val lockId = intent.getStringExtra("EXTRA_LOCK_ID")
        val routineId = intent.getStringExtra("EXTRA_ROUTINE_ID")
        val configJson = intent.getStringExtra("EXTRA_LOCK_CONFIG")
        val title = intent.getStringExtra("EXTRA_LOCK_TITLE")
        val type = intent.getStringExtra("EXTRA_LOCK_TYPE") ?: "QURAN"
        val target = intent.getIntExtra("EXTRA_LOCK_TARGET", 7)

        if (lockId != null) {
            val found = ScreenLockRepository.locks.value.find { it.id == lockId }
            if (found != null) {
                ScreenLockRepository.triggerLock(found, context)
            } else if (configJson != null && title != null) {
                val directLock = ScreenLockEntity(
                    id = lockId,
                    title = title,
                    lockType = type,
                    targetAmount = target,
                    contentConfigJson = configJson
                )
                ScreenLockRepository.triggerLock(directLock, context)
            }
        } else if (routineId != null) {
            val routine = UnifiedRoutineRepository.routines.value.find { it.id == routineId }
            if (routine != null) ScreenLockRepository.triggerLock(routine.toScreenLockEntity(), context)
        }
    }
}
