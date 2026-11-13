package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.LockRepository
import com.example.model.BlockerStyle
import com.example.service.FloatingOverlayPreferences
import com.example.service.FloatingOverlayStyle
import com.example.ui.theme.AppThemeStyle
import com.example.util.AppStrings

@Composable
fun SettingsScreen(
    currentTheme: AppThemeStyle,
    onThemeChange: (AppThemeStyle) -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onLanguageChange: (Boolean) -> Unit
) {
    var defaultBlocker by remember { mutableStateOf(LockRepository.defaultBlockerStyle.value) }
    var overlayStyle by remember { mutableStateOf(FloatingOverlayPreferences.selectedStyle) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp).padding(bottom = 70.dp).testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AppearanceSettingsSection(
            currentTheme = currentTheme,
            onThemeChange = onThemeChange,
            isDarkMode = isDarkMode,
            onDarkModeToggle = onDarkModeToggle,
            onLanguageChange = onLanguageChange
        )

        HorizontalDivider()
        SecuritySettingsSection()

        HorizontalDivider()
        SettingsLockToolsSection()

        HorizontalDivider()
        Text(AppStrings.get("شكل التذكير والتسبيح العائم فوق التطبيقات", "Floating Overlay Style"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(
                selected = overlayStyle == FloatingOverlayStyle.INTERACTIVE_WIDGET,
                onClick = { overlayStyle = FloatingOverlayStyle.INTERACTIVE_WIDGET; FloatingOverlayPreferences.selectedStyle = FloatingOverlayStyle.INTERACTIVE_WIDGET },
                label = { Text(AppStrings.get("ودجت تفاعلي بأزرار", "Interactive Widget")) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = overlayStyle == FloatingOverlayStyle.COMPACT_BUBBLE,
                onClick = { overlayStyle = FloatingOverlayStyle.COMPACT_BUBBLE; FloatingOverlayPreferences.selectedStyle = FloatingOverlayStyle.COMPACT_BUBBLE },
                label = { Text(AppStrings.get("فقاعة دائرية عائمة", "Floating Bubble")) },
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider()
        Text(AppStrings.get("نوع قفل الشاشة الافتراضي", "Default Blocker Style"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(selected = defaultBlocker == BlockerStyle.OVERLAY, onClick = { defaultBlocker = BlockerStyle.OVERLAY; LockRepository.defaultBlockerStyle.value = BlockerStyle.OVERLAY }, label = { Text(AppStrings.get("أوفرلاي", "Overlay")) }, modifier = Modifier.weight(1f))
            FilterChip(selected = defaultBlocker == BlockerStyle.STRICT, onClick = { defaultBlocker = BlockerStyle.STRICT; LockRepository.defaultBlockerStyle.value = BlockerStyle.STRICT }, label = { Text(AppStrings.get("صارم", "Strict")) }, modifier = Modifier.weight(1f))
            FilterChip(selected = defaultBlocker == BlockerStyle.GENTLE, onClick = { defaultBlocker = BlockerStyle.GENTLE; LockRepository.defaultBlockerStyle.value = BlockerStyle.GENTLE }, label = { Text(AppStrings.get("لطيف", "Gentle")) }, modifier = Modifier.weight(1f))
        }

        HorizontalDivider()
        AudioFolderPickerCard()

        HorizontalDivider()
        SmartAlertSettingsSection()

        HorizontalDivider()
        var showCirclesDialog by remember { mutableStateOf(false) }
        com.example.ui.circles.CloudBackupSyncCard(onOpenCircles = { showCirclesDialog = true })
        if (showCirclesDialog) {
            com.example.ui.circles.SharedKhatmaCirclesDialog(onDismiss = { showCirclesDialog = false })
        }

        HorizontalDivider()
        BackupRestoreSettingsSection()

        HorizontalDivider()
        PermissionsSection()

        HorizontalDivider()
        com.example.ui.theme.OrnateIslamicTonalButton(
            onClick = { com.example.data.AppPreferencesManager.setFirstRunSetupCompleted(false) },
            modifier = Modifier.fillMaxWidth().testTag("reopen_setup_wizard_btn")
        ) {
            Text(AppStrings.get("إعادة فتح معالج الإعداد السريع", "Re-run Setup Wizard"))
        }
    }
}
