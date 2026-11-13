package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.fragment.app.FragmentActivity
import com.example.service.LockOverlayService
import com.example.service.TasbeehAlertScheduler
import com.example.ui.lock.ActiveLockActivity
import com.example.ui.navigation.AppDestination
import com.example.ui.navigation.AppScaffold
import com.example.ui.navigation.NavigationState
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppForegroundTracker
import com.example.util.AppStrings

class MainActivity : FragmentActivity() {

    private val notifPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result handled */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.example.data.AppPreferencesManager.init(this)
        com.example.data.AppAppearancePreferences.init(this)
        com.example.data.ScreenLockRepository.init(this)
        com.example.data.QuranDataProvider.init(this)
        com.example.data.PrayerTimesRepository.init(this)
        com.example.data.DailyProgressRepository.init(this)
        com.example.data.FocusProfilesRepository.init(this)
        com.example.data.LockRepository.refreshInstalledApps(this)
        com.example.util.AudioPlayerManager.init(this)
        handleNotificationRouting(intent)
        requestNotificationPermissionIfNeeded()

        try {
            LockOverlayService.startService(this)
            TasbeehAlertScheduler.start(this)
            com.example.util.AppBackupManager.checkAndPerformAutoBackup(this)
        } catch (_: Exception) {}
        if (com.example.util.LockPersistenceManager.isLockActive(this) && !com.example.util.LockScheduleTracker.isGlobalCooldownActive(this)) {
            ActiveLockActivity.launch(this)
        }

        setContent {
            val roomDarkMode by com.example.data.AppPreferencesManager.isDarkMode.collectAsState()
            val roomArabic by com.example.data.AppPreferencesManager.isArabic.collectAsState()
            val isPinConfigured by com.example.data.AppPreferencesManager.isPinConfigured.collectAsState()
            val isSetupDone by com.example.data.AppPreferencesManager.isFirstRunSetupCompleted.collectAsState()
            val currentTheme by com.example.data.AppAppearancePreferences.themeStyle.collectAsState()
            val currentFont by com.example.data.AppAppearancePreferences.fontStyle.collectAsState()
            val customTheme by com.example.data.AppAppearancePreferences.customTheme.collectAsState()

            AppStrings.isArabic = roomArabic

            MyApplicationTheme(
                themeStyle = currentTheme,
                fontStyle = currentFont,
                darkTheme = roomDarkMode,
                customTheme = customTheme
            ) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (roomArabic) LayoutDirection.Rtl else LayoutDirection.Ltr
                ) {
                    AppScaffold(
                        currentTheme = currentTheme,
                        onThemeChange = { com.example.data.AppAppearancePreferences.setThemeStyle(this@MainActivity, it) },
                        isDarkMode = roomDarkMode,
                        onDarkModeToggle = { com.example.data.AppPreferencesManager.setDarkMode(it) },
                        onLanguageChange = { com.example.data.AppPreferencesManager.setLanguage(it) }
                    )

                    if (isSetupDone && !isPinConfigured) {
                        com.example.ui.setup.MandatoryPinSetupDialog(onPinSet = {})
                    }
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        AppForegroundTracker.setInForeground(true)
        if (com.example.util.LockPersistenceManager.isLockActive(this) && !com.example.util.LockScheduleTracker.isGlobalCooldownActive(this)) {
            ActiveLockActivity.launch(this)
        }
    }

    override fun onStop() {
        super.onStop()
        AppForegroundTracker.setInForeground(false)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationRouting(intent)
    }

    private fun handleNotificationRouting(intent: Intent?) {
        if (intent == null) return
        val targetTab = intent.getIntExtra("EXTRA_TARGET_TAB", -1)
        if (targetTab in AppDestination.values().indices) {
            NavigationState.currentDestination.value = AppDestination.values()[targetTab]
        }
        val subTab = intent.getIntExtra("EXTRA_SUB_TAB", -1)
        if (subTab >= 0) {
            NavigationState.routinesSubTab.value = subTab
        }
        val surahId = intent.getIntExtra("EXTRA_OPEN_SURAH_ID", -1)
        if (surahId in 1..114) {
            NavigationState.currentDestination.value = AppDestination.QURAN
            NavigationState.pendingSurahToOpen.value = com.example.data.QuranRepository.getSurah(surahId)
        }
        if (intent.getBooleanExtra("EXTRA_OPEN_QIBLA", false)) {
            NavigationState.currentDestination.value = AppDestination.HOME
            NavigationState.openQiblaDialog.value = true
        }
        val shouldLock = intent.getBooleanExtra("EXTRA_LAUNCH_LOCK_OVERLAY", false)
        if (shouldLock) {
            val lockId = intent.getStringExtra("EXTRA_LOCK_ID")
            val targetLock = if (lockId != null) {
                com.example.data.ScreenLockRepository.locks.value.find { it.id == lockId }
            } else null
            ActiveLockActivity.launch(this, targetLock)
        }
    }
}
