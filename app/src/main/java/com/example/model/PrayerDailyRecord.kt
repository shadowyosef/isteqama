package com.example.model

enum class PrayerStatus(val id: String, val labelAr: String, val labelEn: String) {
    ON_TIME("on_time", "في وقتها", "On Time"),
    JAMAAH("jamaah", "مع الجماعة", "In Congregation"),
    LATE_QADAA("late", "قضاء", "Late (Qadaa)"),
    EXCUSED("excused", "عذر شرعي", "Excused"),
    NOT_PRAYED("not_prayed", "لم تؤدَّ بعد", "Not Prayed");

    companion object {
        fun fromId(id: String?): PrayerStatus = values().find { it.id == id } ?: ON_TIME
    }
}

data class PrayerDailyRecord(
    val dateKey: String,
    val prayerId: String,
    val isPrayed: Boolean = true,
    val status: PrayerStatus = PrayerStatus.ON_TIME,
    val timestampMillis: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    val prayerType: PrayerType?
        get() = PrayerType.values().find { it.id == prayerId }
}
