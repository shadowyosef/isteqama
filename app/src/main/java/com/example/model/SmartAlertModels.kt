package com.example.model

enum class SmartAlertMode {
    PERSISTENT_LOCK,
    FLOATING_BANNER,
    COUNTDOWN_GRACE
}

enum class ReminderFrequency {
    HOURLY,
    DAILY,
    PRAYER_TIMES,
    CUSTOM_INTERVAL
}

enum class AlertSoundStyle {
    GENTLE_ISLAMIC_TONE,
    VIBRATION_ONLY,
    SILENT
}

enum class QuranViewMode {
    VERSE_LIST,
    MUSHAF_PAGE
}

enum class QuranRiwayah {
    HAFS_AN_ASIM,
    WARSH_AN_NAFI
}
