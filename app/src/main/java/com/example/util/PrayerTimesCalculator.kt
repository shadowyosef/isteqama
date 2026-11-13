package com.example.util

import com.example.model.PrayerAdjustments
import com.example.model.PrayerTimeItem
import com.example.model.PrayerType
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.*

object PrayerTimesCalculator {

    fun calculate(
        latitude: Double,
        longitude: Double,
        calendar: Calendar = Calendar.getInstance(),
        adjustments: PrayerAdjustments = PrayerAdjustments(),
        timezoneHours: Double? = null
    ): List<PrayerTimeItem> {
        val timeZone = timezoneHours ?: (
            if (latitude in 29.0..33.5 && longitude in 34.5..39.5) 3.0
            else round(longitude / 15.0).coerceIn(-12.0, 14.0)
        )
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        val b = 2 * PI * (dayOfYear - 81) / 365
        val eqtMinutes = 9.87 * sin(2 * b) - 7.53 * cos(b) - 1.5 * sin(b)
        val declinationDegrees = 23.45 * sin(2 * PI * (284 + dayOfYear) / 365.0)
        val declinationRad = Math.toRadians(declinationDegrees)
        val latRad = Math.toRadians(latitude)

        val solarNoon = 12.0 + timeZone - (longitude / 15.0) - (eqtMinutes / 60.0)

        fun hourAngle(altitudeDegrees: Double): Double {
            val altRad = Math.toRadians(altitudeDegrees)
            val cosH = (sin(altRad) - sin(latRad) * sin(declinationRad)) / (cos(latRad) * cos(declinationRad))
            return if (cosH in -1.0..1.0) Math.toDegrees(acos(cosH)) / 15.0 else 0.0
        }

        val fajrHA = hourAngle(-18.0)
        val sunriseHA = hourAngle(-0.833)
        val asrAlt = Math.toDegrees(atan(1.0 / (1.0 + tan(abs(latRad - declinationRad)))))
        val asrHA = hourAngle(asrAlt)
        val sunsetHA = hourAngle(-0.833)
        val ishaHA = hourAngle(-18.0)

        val rawFajr = solarNoon - fajrHA
        val rawSunrise = solarNoon - sunriseHA
        val rawDhuhr = solarNoon + 0.0167
        val rawAsr = solarNoon + asrHA
        val rawMaghrib = solarNoon + sunsetHA + 0.0167
        val rawIsha = solarNoon + ishaHA

        val nowHour = calendar.get(Calendar.HOUR_OF_DAY)
        val nowMinute = calendar.get(Calendar.MINUTE)
        val nowTotal = nowHour * 60 + nowMinute

        fun toItem(type: PrayerType, rawHourDec: Double): PrayerTimeItem {
            val offset = adjustments.getOffset(type)
            var totalMins = (round(rawHourDec * 60).toInt() + offset) % 1440
            if (totalMins < 0) totalMins += 1440
            val h = totalMins / 60
            val m = totalMins % 60
            val timeStr = String.format("%02d:%02d", h, m)
            val isPassed = nowTotal >= totalMins
            return PrayerTimeItem(type = type, timeFormatted = timeStr, hour = h, minute = m, isPassed = isPassed)
        }

        val items = listOf(
            toItem(PrayerType.FAJR, rawFajr),
            toItem(PrayerType.SUNRISE, rawSunrise),
            toItem(PrayerType.DHUHR, rawDhuhr),
            toItem(PrayerType.ASR, rawAsr),
            toItem(PrayerType.MAGHRIB, rawMaghrib),
            toItem(PrayerType.ISHA, rawIsha)
        )

        var foundNext = false
        val marked = items.map { item ->
            if (!item.isPassed && !foundNext && item.type.isObligatoryPrayer) {
                foundNext = true
                item.copy(isNext = true)
            } else item
        }
        return if (!foundNext) {
            marked.mapIndexed { idx, it -> if (idx == 0) it.copy(isNext = true) else it }
        } else marked
    }
}
