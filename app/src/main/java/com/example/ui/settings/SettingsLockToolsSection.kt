package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.calendar.CalendarLockSoundCard
import com.example.ui.lock.LockSoundPickerDialog
import com.example.util.AppStrings

@Composable
fun SettingsLockToolsSection() {
    val isAr = AppStrings.isArabic
    var showSoundDialog by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().testTag("settings_lock_tools_section")
    ) {
        Text(
            text = if (isAr) "إعدادات صوت تنبيه القفل" else "Lock Alert Sound Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        CalendarLockSoundCard(
            onClick = { showSoundDialog = true },
            modifier = Modifier.testTag("settings_lock_sound_card")
        )
    }

    if (showSoundDialog) {
        LockSoundPickerDialog(onDismiss = { showSoundDialog = false })
    }
}
