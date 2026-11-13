package com.example.data

import com.example.model.PrayerTimeItem
import java.util.Calendar

object PrayerCountdownHelper {

    fun getNextPrayerAndRemaining(items: List<PrayerTimeItem>): Pair<PrayerTimeItem?, Long> {
        val obligatory = items.filter { it.type.isObligatoryPrayer }
        if (obligatory.isEmpty()) return null to -1L
        val now = Calendar.getInstance()
        val nowMins = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val nowSecs = now.get(Calendar.SECOND)
        val nowTotalSeconds = nowMins * 60 + nowSecs

        for (item in obligatory) {
            val prayerSeconds = (item.hour * 60 + item.minute) * 60
            if (prayerSeconds > nowTotalSeconds) {
                val diffMillis = (prayerSeconds - nowTotalSeconds) * 1000L
                return item to diffMillis
            }
        }
        val firstTomorrow = obligatory.first()
        val tomorrowSeconds = (24 * 3600 - nowTotalSeconds) + (firstTomorrow.hour * 60 + firstTomorrow.minute) * 60
        return firstTomorrow to (tomorrowSeconds * 1000L)
    }

    fun formatCountdown(remainingMs: Long, isArabic: Boolean): String {
        if (remainingMs <= 0L) return if (isArabic) "حان وقت الصلاة" else "Prayer Time Now"
        val totalSecs = remainingMs / 1000L
        val hours = totalSecs / 3600L
        val mins = (totalSecs % 3600L) / 60L
        val secs = totalSecs % 60L
        return String.format("%02d:%02d:%02d", hours, mins, secs)
    }
}
