package com.example

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.data.*
import com.example.model.*
import com.example.receiver.NotificationActionReceiver
import com.example.util.PrayerSeasonsHelper
import com.example.util.TajweedHighlighter
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
class FeaturesAndTilesUnitTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        FocusProfilesRepository.init(context)
        HifzLoopAndReflectionRepository.init(context)
        PrayerSeasonsHelper.init(context)
    }

    @Test
    fun focusProfiles_dailyBudgetAndBypass_workProperly() {
        val pkg = "com.test.social"
        FocusProfilesRepository.setAppDailyLimit(context, pkg, 1)
        assertFalse(FocusProfilesRepository.recordForegroundSeconds(context, pkg, 30))
        assertTrue(FocusProfilesRepository.recordForegroundSeconds(context, pkg, 35))
        FocusProfilesRepository.grantTemporaryBypass(pkg, 2)
        assertFalse(FocusProfilesRepository.recordForegroundSeconds(context, pkg, 1))
    }

    @Test
    fun tajweedHighlighter_highlightsAndMasksWords() {
        val sample = "إِنَّ اللَّهَ عَلَىٰ قَدِيرٌ"
        val styled = TajweedHighlighter.highlight(sample, enabled = true)
        assertTrue(styled.spanStyles.isNotEmpty())
        val masked = TajweedHighlighter.maskWordsForHifz("قُلْ هُوَ اللَّهُ أَحَدٌ")
        assertTrue(masked.contains("_____"))
    }

    @Test
    fun prayerSeasons_lastThirdAndForbiddenWindows_accurate() {
        val list = listOf(
            PrayerTimeItem(PrayerType.FAJR, "05:00", 5, 0),
            PrayerTimeItem(PrayerType.SUNRISE, "06:30", 6, 30),
            PrayerTimeItem(PrayerType.DHUHR, "12:30", 12, 30),
            PrayerTimeItem(PrayerType.ASR, "15:45", 15, 45),
            PrayerTimeItem(PrayerType.MAGHRIB, "18:00", 18, 0),
            PrayerTimeItem(PrayerType.ISHA, "19:30", 19, 30)
        )
        val lastThird = PrayerSeasonsHelper.calculateLastThirdStart(list)
        assertEquals("01:20", lastThird)
        assertNotNull(PrayerSeasonsHelper.getForbiddenNaflReason(5, 30, list, true))
        assertNotNull(PrayerSeasonsHelper.getForbiddenNaflReason(12, 25, list, false))
        assertNull(PrayerSeasonsHelper.getForbiddenNaflReason(10, 0, list, true))

        val fri = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
            set(Calendar.HOUR_OF_DAY, 10)
        }
        assertEquals(FocusProfileMode.FRIDAY_KAHF, FocusProfilesRepository.effectiveProfile(fri))
    }

    @Test
    fun badgesAndNotificationAction_incrementProperly() {
        val beforeCount = TasbeehRepository.totalLifetimeCount.value
        val intent = Intent(NotificationActionReceiver.ACTION_INCREMENT_TASBEEH)
        NotificationActionReceiver().onReceive(context, intent)
        assertEquals(beforeCount + 1, TasbeehRepository.totalLifetimeCount.value)

        StatsRepository.stats.value = StatsRepository.stats.value.copy(currentStreak = 7, totalDhikrCount = 1200)
        val earnedIds = BadgesRepository.getBadges().filter { it.earned }.map { it.id }.toSet()
        assertTrue("streak_7" in earnedIds)
        assertTrue("dhikr_1k" in earnedIds)
    }

    @Test
    fun defaults_monthlyKhatma_randomTasbeeh30m_andSnoozeWork() {
        val tasbeeh = TasbeehScheduleRepository.schedules.value
        assertEquals(1, tasbeeh.size)
        val t = tasbeeh.first()
        assertEquals(30, t.repeatIntervalMins)
        assertTrue(t.isRandomOrder)
        assertTrue(t.quietHoursEnabled)
        assertEquals(23, t.quietHoursStart)
        assertEquals(8, t.quietHoursEnd)
        assertTrue(t.getPhrases().size >= AuthenticTasbeehCatalog.allPhrases.size)

        assertTrue(com.example.util.LockScheduleTracker.isHourInRestWindow(23, true, 23, 8))
        assertTrue(com.example.util.LockScheduleTracker.isHourInRestWindow(7, true, 23, 8))
        assertFalse(com.example.util.LockScheduleTracker.isHourInRestWindow(8, true, 23, 8))

        com.example.util.LockScheduleTracker.scheduleSnoozeLock(context, "prayer_lock_asr", "أذكار بعد العصر", "DHIKR", "", 15)
        assertNull(com.example.util.LockScheduleTracker.popDueSnoozedLock(context))
        com.example.util.LockScheduleTracker.scheduleSnoozeLock(context, "prayer_lock_asr", "أذكار بعد العصر", "DHIKR", "", 0)
        val popped = com.example.util.LockScheduleTracker.popDueSnoozedLock(context)
        assertNotNull(popped)
        assertEquals("prayer_lock_asr", popped?.id)
        assertNull(com.example.util.LockScheduleTracker.popDueSnoozedLock(context))
    }
}
