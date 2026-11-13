package com.example

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.data.*
import com.example.util.KhatmaCatchUpHelper
import com.example.util.VoiceHifzVerifier
import com.example.widget.HadithAyahWidgetProvider
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NewWaveFeaturesUnitTest {

    @Test
    fun voiceHifzEvaluator_normalizesTashkeelAndScores() {
        val expected = "قُلْ هُوَ اللَّهُ أَحَدٌ"
        val spokenGood = "قل هو الله احد"
        val (results, pct) = VoiceHifzVerifier.evaluateRecitation(expected, spokenGood)
        assertEquals(4, results.size)
        assertEquals(100, pct)

        val (_, partialPct) = VoiceHifzVerifier.evaluateRecitation(expected, "قل هو")
        assertEquals(50, partialPct)
    }

    @Test
    fun khatmaCatchUpAndWordGlossary_workAccurately() {
        val status = KhatmaCatchUpHelper.computeStatus(targetDays = 30, elapsedDays = 5, actualPagesRead = 42)
        assertTrue(status.deficitPages > 0)
        assertEquals(3, status.options.size)

        val entry = QuranWordMeaningCatalog.lookupWord("الصَّمَدُ")
        assertEquals("ص م د", entry.rootAr)
        assertTrue(entry.meaningAr.contains("يقصد"))
    }

    @Test
    fun radioHadithMosqueAndMindfulGate_behaveProperly() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        assertTrue(QuranRadioCatalog.stations.size >= 5)
        assertTrue(HadithLibraryCatalog.search("النيات").isNotEmpty())

        val dist = NearbyMosquesRepository.haversineMeters(31.95, 35.91, 31.96, 35.91)
        assertTrue(dist in 900..1300)

        MindfulGateRepository.init(ctx)
        MindfulGateRepository.activatePrayerFreezeWindow(ctx)
        assertTrue(MindfulGateRepository.isPrayerFreezeActiveNow(ctx))
        MindfulGateRepository.recordSavedScrollMinutes(ctx, 10)
        assertTrue(MindfulGateRepository.savedMinutes.value >= 10)
    }

    @Test
    fun hadithAyahWidget_togglesModeAndCycles() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val wm = AppWidgetManager.getInstance(ctx)
        val wid = Shadows.shadowOf(wm).createWidget(HadithAyahWidgetProvider::class.java, R.layout.widget_hadith_ayah)
        val provider = HadithAyahWidgetProvider()
        provider.onUpdate(ctx, wm, intArrayOf(wid))
        provider.onReceive(ctx, Intent(HadithAyahWidgetProvider.ACTION_TOGGLE_MODE))
        provider.onReceive(ctx, Intent(HadithAyahWidgetProvider.ACTION_NEXT))
        val view = Shadows.shadowOf(wm).getViewFor(wid)
        assertNotNull(view.findViewById(R.id.tv_hadith_widget_body))
    }

    @Test
    fun firstRunSetupFlag_persistsSynchronouslyInSharedPrefs() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        AppPreferencesManager.init(ctx)
        AppPreferencesManager.setFirstRunSetupCompleted(true)
        assertTrue(AppPreferencesManager.isFirstRunSetupCompleted.value)
        val prefs = ctx.getSharedPreferences("istaqama_security_prefs", Context.MODE_PRIVATE)
        assertTrue(prefs.getBoolean("FIRST_RUN_SETUP_DONE", false))
        AppPreferencesManager.setFirstRunSetupCompleted(false)
        assertFalse(AppPreferencesManager.isFirstRunSetupCompleted.value)
        assertFalse(prefs.getBoolean("FIRST_RUN_SETUP_DONE", true))
    }

    @Test
    fun onboardingFixes_livePrayerPatternAndDefaultToggles_workProperly() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        PrayerTimesRepository.init(ctx)
        PrayerTimesRepository.updateAdjustments(ctx, com.example.model.PrayerAdjustments(fajrOffset = 5))
        val base = com.example.util.PrayerTimesCalculator.calculate(
            PrayerTimesRepository.currentLocation.value.latitude,
            PrayerTimesRepository.currentLocation.value.longitude,
            java.util.Calendar.getInstance(),
            com.example.model.PrayerAdjustments(fajrOffset = 5),
            PrayerTimesRepository.currentLocation.value.timezoneHours
        )
        assertEquals(base.first().timeFormatted, PrayerTimesRepository.prayerTimes.value.firstOrNull()?.timeFormatted)

        AppAppearancePreferences.setPatternOverride(ctx, com.example.ui.theme.IslamicPatternStyle.MIHRAB_ARCH)
        assertEquals(
            com.example.ui.theme.IslamicPatternStyle.MIHRAB_ARCH,
            AppAppearancePreferences.resolvePattern(
                com.example.ui.theme.AppThemeStyle.CUSTOM_STUDIO,
                AppAppearancePreferences.patternOverride.value,
                AppAppearancePreferences.customTheme.value
            )
        )

        TasbeehScheduleRepository.setScheduleEnabled("tasbeeh_default_all_random", false)
        assertFalse(TasbeehScheduleRepository.schedules.value.first { it.id == "tasbeeh_default_all_random" }.isEnabled)
        TasbeehScheduleRepository.setScheduleEnabled("tasbeeh_default_all_random", true)
        assertTrue(TasbeehScheduleRepository.schedules.value.first { it.id == "tasbeeh_default_all_random" }.isEnabled)
    }

    @Test
    fun fontScalePreviewCooldownBypassAndRealAppLimits_workProperly() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        AppAppearancePreferences.init(ctx)
        AppAppearancePreferences.setFontScale(ctx, 2.0f)
        assertEquals(2.0f, AppAppearancePreferences.fontScale.value, 0.01f)
        AppAppearancePreferences.setFontScale(ctx, 5.0f)
        assertEquals(3.0f, AppAppearancePreferences.fontScale.value, 0.01f)
        AppAppearancePreferences.setFontScale(ctx, 1.0f)

        val previewLock = com.example.data.local.entity.ScreenLockEntity(
            id = "preview_lock_test",
            title = "Preview",
            lockType = "QURAN",
            targetAmount = 2
        )
        com.example.ui.lock.LockCompletionHelper.completeLock(ctx, previewLock) {}
        assertFalse(com.example.util.LockScheduleTracker.isGlobalCooldownActive(ctx))

        LockRepository.refreshInstalledApps(ctx)
        val firstPkg = LockRepository.appRules.value.first().packageName
        LockRepository.updateAppRule(firstPkg, com.example.model.BlockerStyle.STRICT, true, ctx)
        LockRepository.refreshInstalledApps(ctx)
        assertTrue(LockRepository.appRules.value.first { it.packageName == firstPkg }.isBlocked)

        FocusProfilesRepository.init(ctx)
        FocusProfilesRepository.setAppDailyLimit(ctx, firstPkg, 45)
        assertEquals(45, FocusProfilesRepository.appDailyLimitMins.value[firstPkg])
        assertEquals("QuranAudio", com.example.util.SafAudioLookupHelper.readableFolderName("content://com.android.externalstorage.documents/tree/primary%3AQuranAudio"))
    }
}
