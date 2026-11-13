package com.example

import com.example.data.local.entity.ScreenLockEntity
import com.example.model.LockContentConfig
import com.example.util.CountdownUtils
import com.example.util.LockProgressHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun testCountdownCalculation() {
        val next = CountdownUtils.calculateNextTrigger(1000L, 5000L, 2000L)
        assertEquals(6000L, next)
    }

    @Test
    fun testCountdownFormat() {
        val formattedAr = CountdownUtils.formatCountdown(65000L, true)
        assertTrue(formattedAr.contains("د") || formattedAr.contains("ث"))
    }

    @Test
    fun testQuranResetConfig() {
        val config = LockContentConfig(
            selectedSurahIds = listOf(1, 2),
            startAyah = 10,
            currentProgressAyah = 45,
            currentSurahIndex = 1
        )
        val resetConfig = config.copy(
            currentSurahIndex = 0,
            currentProgressAyah = config.startAyah
        )
        assertEquals(0, resetConfig.currentSurahIndex)
        assertEquals(10, resetConfig.currentProgressAyah)
        assertEquals(10, resetConfig.startAyah)
    }

    @Test
    fun testLockContentConfigSerialization() {
        val config = LockContentConfig(
            selectedSurahIds = listOf(18),
            startAyah = 1,
            endAyah = 10,
            ayahsPerSession = 5,
            currentProgressAyah = 1,
            isTimedMode = false,
            readingDurationMinutes = 10,
            currentSurahIndex = 0,
            isPageMode = true,
            pagesPerSession = 2,
            startPage = 50,
            endPage = 100,
            currentProgressPage = 52
        )
        val serialized = config.serialize()
        val deserialized = LockContentConfig.deserialize(serialized)
        assertEquals(listOf(18), deserialized.selectedSurahIds)
        assertEquals(1, deserialized.startAyah)
        assertEquals(10, deserialized.endAyah)
        assertEquals(5, deserialized.ayahsPerSession)
        assertTrue(deserialized.isPageMode)
        assertEquals(2, deserialized.pagesPerSession)
        assertEquals(50, deserialized.startPage)
        assertEquals(100, deserialized.endPage)
        assertEquals(52, deserialized.currentProgressPage)
    }

    @Test
    fun testLockProgressHelperPageMode() {
        val config = LockContentConfig(
            isPageMode = true,
            startPage = 1,
            pagesPerSession = 2,
            currentProgressPage = 1,
            endPage = 604
        )
        val lock = ScreenLockEntity(
            id = "lock_page_test",
            title = "ورد الصفحات",
            lockType = "QURAN",
            contentConfigJson = config.serialize()
        )
        val advanced = LockProgressHelper.advanceProgress(lock)
        val advancedConfig = advanced.getContentConfig()
        assertEquals(3, advancedConfig.currentProgressPage)

        val reset = LockProgressHelper.resetToStart(advanced)
        val resetConfig = reset.getContentConfig()
        assertEquals(1, resetConfig.currentProgressPage)
    }

    @Test
    fun testLockScheduleSlotParsing() {
        val slots = listOf(
            com.example.model.LockScheduleSlot(
                id = "slot_test_1",
                hour = 7,
                minute = 30,
                daysOfWeek = listOf(1, 2, 3),
                intervalMinutes = 0
            ),
            com.example.model.LockScheduleSlot(
                id = "slot_test_2",
                hour = 0,
                minute = 0,
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7),
                intervalMinutes = 45
            )
        )
        val serialized = ScreenLockEntity.serializeSlots(slots)
        val parsed = ScreenLockEntity.parseScheduleSlots(serialized)
        assertEquals(2, parsed.size)
        assertEquals("slot_test_1", parsed[0].id)
        assertEquals(7, parsed[0].hour)
        assertEquals(30, parsed[0].minute)
        assertEquals("slot_test_2", parsed[1].id)
        assertEquals(45, parsed[1].intervalMinutes)
    }

    @Test
    fun testPrayerTimesCalculation() {
        val prayers = com.example.util.PrayerTimesCalculator.calculate(
            latitude = 21.4225,
            longitude = 39.8262
        )
        assertEquals(6, prayers.size)
        assertTrue(prayers.any { it.type == com.example.model.PrayerType.FAJR })
        assertTrue(prayers.any { it.type == com.example.model.PrayerType.DHUHR })
        assertTrue(prayers.any { it.type == com.example.model.PrayerType.ASR })
        assertTrue(prayers.any { it.type == com.example.model.PrayerType.MAGHRIB })
        assertTrue(prayers.any { it.type == com.example.model.PrayerType.ISHA })
    }
}
