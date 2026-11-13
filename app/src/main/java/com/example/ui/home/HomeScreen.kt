package com.example.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.AppDestination
import com.example.ui.prayer.PostPrayerLockDialog
import com.example.ui.prayer.PrayerTimesDashboardCard
import com.example.ui.prayer.SunanTrackerSection
import com.example.ui.theme.IslamicSectionDivider

@Composable
fun HomeScreen(
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPostPrayerLock by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .widthIn(max = 650.dp)
                .align(Alignment.TopCenter)
                .testTag("home_screen_content"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HomeHeroBanner()

            PrayerTimesDashboardCard(onOpenPostPrayerLock = { showPostPrayerLock = true })
            SunanTrackerSection()

            IslamicSectionDivider()
            HomeQuranShortcutCard(onOpenQuran = { onNavigate(AppDestination.QURAN) })

            IslamicSectionDivider()
            HomeAdhkarShortcutGrid(onOpenRoutines = { onNavigate(AppDestination.ROUTINES) })
            HomeQuickTasbeehWidget(onOpenTasbeeh = { onNavigate(AppDestination.ROUTINES) })

            IslamicSectionDivider()
            HomeExtraWorshipSection()
            HomeLockStatusCard(onOpenLocker = { onNavigate(AppDestination.LOCKER) })

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showPostPrayerLock) {
        PostPrayerLockDialog(
            onComplete = { showPostPrayerLock = false },
            onDismiss = { showPostPrayerLock = false }
        )
    }
}
