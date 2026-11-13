package com.example.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.AppAppearancePreferences
import com.example.data.AppPreferencesManager
import com.example.data.LockRepository
import com.example.model.SurahInfo
import com.example.ui.calendar.CalendarScheduleScreen
import com.example.ui.home.HomeScreen
import com.example.ui.lock.ActiveLockOverlay
import com.example.ui.quran.QuranHomeScreen
import com.example.ui.quran.QuranReaderSheet
import com.example.ui.settings.SettingsScreen
import com.example.ui.setup.FirstRunSetupScreen
import com.example.ui.stats.StatsDashboardScreen
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.IslamicOrnamentBackground
import com.example.ui.unified.UnifiedRoutinesHomeScreen

@Composable
fun AppScaffold(
    currentTheme: AppThemeStyle,
    onThemeChange: (AppThemeStyle) -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onLanguageChange: (Boolean) -> Unit
) {
    val isSetupDone by AppPreferencesManager.isFirstRunSetupCompleted.collectAsState()
    if (!isSetupDone) {
        FirstRunSetupScreen(onComplete = { })
        return
    }

    val selectedDestination by NavigationState.currentDestination.collectAsState()
    val pendingSurah by NavigationState.pendingSurahToOpen.collectAsState()
    var viewingSurah by remember { mutableStateOf<SurahInfo?>(null) }
    LaunchedEffect(pendingSurah) {
        if (pendingSurah != null) {
            viewingSurah = pendingSurah
            NavigationState.pendingSurahToOpen.value = null
        }
    }

    val isLockActive by LockRepository.isLockActive.collectAsState()
    val activeSchedule by LockRepository.currentActiveSchedule.collectAsState()
    val customTheme by AppAppearancePreferences.customTheme.collectAsState()
    val patOverride by AppAppearancePreferences.patternOverride.collectAsState()
    val patternStyle = AppAppearancePreferences.resolvePattern(currentTheme, patOverride, customTheme)
    val patternOpacity = if (currentTheme == AppThemeStyle.CUSTOM_STUDIO) customTheme.patternOpacity else 0.20f
    val stateHolder = rememberSaveableStateHolder()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        IslamicOrnamentBackground(patternStyle = patternStyle, patternOpacity = patternOpacity)
        val isWideScreen = maxWidth > 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (isWideScreen) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight().testTag("app_nav_rail"),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    AppDestination.values().forEach { destination ->
                        NavigationRailItem(
                            selected = selectedDestination == destination,
                            onClick = { viewingSurah = null; NavigationState.currentDestination.value = destination },
                            icon = { Icon(destination.icon, contentDescription = destination.getTitle()) },
                            label = { Text(destination.getTitle()) },
                            modifier = Modifier.testTag("nav_${destination.name.lowercase()}")
                        )
                    }
                }
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = Color.Transparent,
                bottomBar = {
                    if (!isWideScreen) {
                        NavigationBar(
                            modifier = Modifier.testTag("app_bottom_nav"),
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            AppDestination.values().forEach { destination ->
                                NavigationBarItem(
                                    selected = selectedDestination == destination,
                                    onClick = { viewingSurah = null; NavigationState.currentDestination.value = destination },
                                    icon = { Icon(destination.icon, contentDescription = destination.getTitle()) },
                                    label = { Text(destination.getTitle()) },
                                    modifier = Modifier.testTag("nav_${destination.name.lowercase()}")
                                )
                            }
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    if (viewingSurah != null) {
                        QuranReaderSheet(surah = viewingSurah!!, onBack = { viewingSurah = null })
                    } else {
                        stateHolder.SaveableStateProvider(selectedDestination) {
                            when (selectedDestination) {
                                AppDestination.HOME -> HomeScreen(onNavigate = { dest -> NavigationState.currentDestination.value = dest })
                                AppDestination.ROUTINES -> UnifiedRoutinesHomeScreen()
                                AppDestination.QURAN -> QuranHomeScreen(onSelectSurah = { viewingSurah = it })
                                AppDestination.LOCKER -> CalendarScheduleScreen()
                                AppDestination.STATS -> StatsDashboardScreen()
                                AppDestination.SETTINGS -> SettingsScreen(
                                    currentTheme = currentTheme,
                                    onThemeChange = onThemeChange,
                                    isDarkMode = isDarkMode,
                                    onDarkModeToggle = onDarkModeToggle,
                                    onLanguageChange = onLanguageChange
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (isLockActive && activeSchedule != null) {
        ActiveLockOverlay(schedule = activeSchedule!!)
    }
}
