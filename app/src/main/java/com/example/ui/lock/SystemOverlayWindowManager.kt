package com.example.ui.lock

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.example.MainActivity
import com.example.data.LockRepository
import com.example.data.ScreenLockRepository
import com.example.data.local.entity.ScreenLockEntity
import com.example.model.LockSchedule
import com.example.model.LockType

object SystemOverlayWindowManager {

    fun launchOverlay(context: Context, schedule: LockSchedule? = null) {
        val targetSchedule = schedule ?: LockRepository.schedules.value.firstOrNull() ?: LockSchedule(
            id = "auto_block",
            title = "ورد حظر التطبيق",
            lockType = LockType.QURAN_VERSES,
            targetAmount = 7
        )

        LockRepository.triggerLock(targetSchedule)

        val lockEntity = ScreenLockEntity(
            id = targetSchedule.id,
            title = targetSchedule.title,
            lockType = when (targetSchedule.lockType) {
                LockType.QURAN_VERSES -> "QURAN"
                LockType.ADHKAR_ROUTINE -> "DHIKR"
                LockType.TASBEEH_COUNT -> "TASBEEH"
            },
            targetAmount = targetSchedule.targetAmount
        )
        ActiveLockActivity.launch(context, lockEntity)
    }

    fun dismissOverlay() {
        LockRepository.completeLock()
        ScreenLockRepository.dismissLock()
    }
}
