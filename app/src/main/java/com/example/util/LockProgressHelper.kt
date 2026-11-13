package com.example.util

import com.example.data.QuranRepository
import com.example.data.local.entity.ScreenLockEntity

object LockProgressHelper {
    fun advanceProgress(lock: ScreenLockEntity): ScreenLockEntity {
        val config = lock.getContentConfig()
        if (config.isTimedMode) return lock

        val updatedConfig = if (config.isPageMode) {
            val nextPg = config.currentProgressPage + config.pagesPerSession.coerceAtLeast(1)
            val maxPg = if (config.endPage > 0) config.endPage else 604
            val finalPg = if (nextPg <= maxPg) nextPg else config.startPage.coerceAtLeast(1)
            config.copy(currentProgressPage = finalPg)
        } else {
            val surahIds = config.selectedSurahIds.ifEmpty { listOf(1) }
            val currSurahIdx = config.currentSurahIndex.coerceIn(0, surahIds.size - 1)
            val currentSurahNum = surahIds[currSurahIdx]
            val totalAyahs = QuranRepository.getAyahsForSurah(currentSurahNum).size.coerceAtLeast(1)
            val nextAyah = config.currentProgressAyah + config.ayahsPerSession
            if (nextAyah <= totalAyahs) {
                config.copy(currentProgressAyah = nextAyah)
            } else {
                val nextSurahIdx = (currSurahIdx + 1) % surahIds.size
                config.copy(currentSurahIndex = nextSurahIdx, currentProgressAyah = 1)
            }
        }
        return lock.copy(contentConfigJson = updatedConfig.serialize())
    }

    fun resetToStart(lock: ScreenLockEntity): ScreenLockEntity {
        val config = lock.getContentConfig()
        val updatedConfig = config.copy(
            currentSurahIndex = 0,
            currentProgressAyah = config.startAyah,
            currentProgressPage = config.startPage.coerceAtLeast(1)
        )
        return lock.copy(contentConfigJson = updatedConfig.serialize())
    }
}
