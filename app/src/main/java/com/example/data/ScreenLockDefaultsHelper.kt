package com.example.data

import com.example.data.local.AppDatabase
import com.example.data.local.entity.ScreenLockEntity
import com.example.model.LockContentConfig

object ScreenLockDefaultsHelper {
    suspend fun seedDefaultsIfMissing(db: AppDatabase, existing: List<ScreenLockEntity>) {
        val dndSlot = { slotId: String, h: Int, m: Int ->
            "$slotId|$h|$m|1,2,3,4,5,6,7|0|0|true|23|8"
        }
        val khatmaCfg = LockContentConfig(
            isPageMode = true,
            pagesPerSession = 21,
            startPage = 1,
            endPage = 604,
            currentProgressPage = 1
        ).serialize()
        val morningCfg = LockContentConfig(selectedRoutineId = "routine_morning").serialize()
        val eveningCfg = LockContentConfig(selectedRoutineId = "routine_evening").serialize()

        val defaults = listOf(
            ScreenLockEntity(
                id = "lock_monthly_khatma",
                title = "ختمة القرآن الشهرية (٢١ صفحة يومياً)",
                lockType = "QURAN",
                targetAmount = 21,
                triggerHour = 13,
                triggerMinute = 0,
                schedulesJson = dndSlot("slot_khatma_1", 13, 0),
                contentConfigJson = khatmaCfg,
                isEnabled = true
            ),
            ScreenLockEntity(
                id = "lock_morning_adhkar",
                title = "أذكار الصباح (بعد الفجر بساعة)",
                lockType = "DHIKR",
                targetAmount = 10,
                triggerHour = 6,
                triggerMinute = 15,
                schedulesJson = dndSlot("slot_morning_1", 6, 15),
                contentConfigJson = morningCfg,
                isEnabled = true
            ),
            ScreenLockEntity(
                id = "lock_evening_adhkar",
                title = "أذكار المساء (بعد العصر بساعة)",
                lockType = "DHIKR",
                targetAmount = 10,
                triggerHour = 16,
                triggerMinute = 45,
                schedulesJson = dndSlot("slot_evening_1", 16, 45),
                contentConfigJson = eveningCfg,
                isEnabled = true
            )
        )
        val existingIds = existing.map { it.id }.toSet()
        defaults.filter { it.id !in existingIds }.forEach { db.screenLockDao().insertLock(it) }
    }
}
