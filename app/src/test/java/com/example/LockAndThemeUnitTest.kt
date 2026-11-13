package com.example

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.example.data.*
import com.example.data.local.entity.ScreenLockEntity
import com.example.model.KhatmaDistributionMode
import com.example.model.LockScheduleSlot
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.CustomThemeConfig
import com.example.ui.theme.IslamicPatternStyle
import com.example.util.KhatmaScheduleCalculator
import com.example.util.LockScheduleTracker
import com.example.widget.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LockAndThemeUnitTest {

    @Test
    fun lockSlotRoundTripsMaxDailyAndRestHours() {
        val slot = LockScheduleSlot("s1", 9, 15, listOf(1, 2, 3), intervalMinutes = 30, maxDailyRepeats = 5, quietHoursEnabled = true, quietHoursStart = 22, quietHoursEnd = 6)
        val raw = ScreenLockEntity.serializeSlots(listOf(slot))
        val parsed = ScreenLockEntity("l1", "Test", "DHIKR", 5, 9, 15, "1,2,3", null, "STRICT_KIOSK", true, raw).getScheduleSlots().first()
        assertEquals(5, parsed.maxDailyRepeats)
        assertTrue(parsed.quietHoursEnabled)
        assertEquals(22, parsed.quietHoursStart)
        assertEquals(6, parsed.quietHoursEnd)
    }

    @Test
    fun restWindowAndDailyLimitEnforced() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        assertTrue(LockScheduleTracker.isHourInRestWindow(23, true, 22, 6))
        assertTrue(LockScheduleTracker.isHourInRestWindow(5, true, 22, 6))
        assertFalse(LockScheduleTracker.isHourInRestWindow(12, true, 22, 6))
        assertTrue(LockScheduleTracker.isHourInRestWindow(14, true, 13, 16))
        assertFalse(LockScheduleTracker.isHourInRestWindow(23, false, 22, 6))

        val id = "slot_test_${System.currentTimeMillis()}"
        assertFalse(LockScheduleTracker.isDailyLimitReached(ctx, id, 2, 100))
        LockScheduleTracker.incrementDailyTriggerCount(ctx, id, 100)
        LockScheduleTracker.incrementDailyTriggerCount(ctx, id, 100)
        assertTrue(LockScheduleTracker.isDailyLimitReached(ctx, id, 2, 100))
    }

    @Test
    fun khatmaManualDaysAndQuizBankWork() {
        assertTrue(IslamicQuizCatalog.questions.size >= 45)
        val plan = KhatmaScheduleCalculator.calculatePlan(21, 10, 22, KhatmaDistributionMode.INTERVAL_HOURLY, 60, 5, true, 13, 15)
        assertEquals(21, plan.targetDays)
        val slots = KhatmaScheduleCalculator.buildScheduleSlots(plan, 2, true, 13, 15)
        assertTrue(slots.isNotEmpty())
        assertTrue(slots.none { it.hour in 13..14 })
    }

    @Test
    fun appearanceCustomThemeAndPatternResolve() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val cfg = CustomThemeConfig(primaryHex = 0xFF004D40, patternStyle = IslamicPatternStyle.MIHRAB_ARCH, patternOpacity = 0.14f)
        AppAppearancePreferences.saveCustomTheme(ctx, cfg)
        AppAppearancePreferences.setThemeStyle(ctx, AppThemeStyle.CUSTOM_STUDIO)
        assertEquals(IslamicPatternStyle.MIHRAB_ARCH, AppAppearancePreferences.resolvePattern(AppThemeStyle.CUSTOM_STUDIO, null, cfg))

        AppThemeStyle.values().forEach { style ->
            listOf(false, true).forEach { dark ->
                val scheme = com.example.ui.theme.resolveColorScheme(style, dark, cfg)
                assertEquals(1f, scheme.background.alpha, 0.001f)
                assertEquals(1f, scheme.surface.alpha, 0.001f)
                assertEquals(1f, scheme.primaryContainer.alpha, 0.001f)
                assertEquals(1f, scheme.secondaryContainer.alpha, 0.001f)
            }
        }
    }

    @Test
    fun widgetsRenderCompactAndExpandedWithoutCrashing() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        PrayerTimesRepository.init(ctx)
        DailyProgressRepository.init(ctx)

        val compact = PrayerWidgetLayoutHelper.buildPrayerRemoteViews(ctx, PrayerTimesRepository.prayerTimes.value, isExpanded = false)
        val expanded = PrayerWidgetLayoutHelper.buildPrayerRemoteViews(ctx, PrayerTimesRepository.prayerTimes.value, isExpanded = true)
        val cView = compact.apply(ctx, FrameLayout(ctx))
        val eView = expanded.apply(ctx, FrameLayout(ctx))
        assertEquals(View.GONE, cView.findViewById<View>(R.id.widget_prayer_expanded).visibility)
        assertEquals(View.VISIBLE, eView.findViewById<View>(R.id.widget_prayer_expanded).visibility)

        InteractiveTasbeehWidgetProvider().onReceive(ctx, Intent(InteractiveTasbeehWidgetProvider.ACTION_COUNT))
        val ibadah = IbadahDashboardWidgetProvider()
        ibadah.onReceive(ctx, Intent(IbadahDashboardWidgetProvider.ACTION_ADD_QURAN_PAGE))
        val wm = AppWidgetManager.getInstance(ctx)
        val widgetId = Shadows.shadowOf(wm).createWidget(IbadahDashboardWidgetProvider::class.java, R.layout.widget_ibadah_dashboard)
        val opts = Bundle().apply { putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 180) }
        ibadah.onAppWidgetOptionsChanged(ctx, wm, widgetId, opts)
        val rendered = Shadows.shadowOf(wm).getViewFor(widgetId)
        assertEquals(View.VISIBLE, rendered.findViewById<View>(R.id.widget_ibadah_expanded).visibility)
        assertTrue(WidgetUtils.getKhatmaPercentAndPages(ctx).second >= 1)
    }
}
