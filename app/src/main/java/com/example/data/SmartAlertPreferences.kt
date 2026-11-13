package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow

object SmartAlertPreferences {
    val smartAlertMode = MutableStateFlow(SmartAlertMode.PERSISTENT_LOCK)
    val reminderFrequency = MutableStateFlow(ReminderFrequency.DAILY)
    val customIntervalMinutes = MutableStateFlow(60)
    val alertSoundStyle = MutableStateFlow(AlertSoundStyle.GENTLE_ISLAMIC_TONE)
    val isPersistentAlert = MutableStateFlow(true)
    
    val quranViewMode = MutableStateFlow(QuranViewMode.VERSE_LIST)
    val selectedRiwayah = MutableStateFlow(QuranRiwayah.HAFS_AN_ASIM)
    val quranFontSizeSp = MutableStateFlow(22f)
    val isFullScreenReader = MutableStateFlow(false)
}
