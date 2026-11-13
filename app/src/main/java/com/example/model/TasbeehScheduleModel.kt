package com.example.model

import java.util.UUID

enum class TasbeehDeliveryStyle {
    TOAST_POPUP,
    NOTIFICATION,
    BOTH,
    FULLSCREEN
}

enum class TasbeehThemeColor {
    EMERALD,
    GOLD,
    OCEAN,
    PURPLE,
    ROSE
}

enum class TasbeehCardShape {
    ROUNDED,
    PILL,
    SLEEK
}

enum class RepeatUnit {
    SECONDS,
    MINUTES,
    HOURS
}

data class TasbeehScheduleModel(
    val id: String = UUID.randomUUID().toString(),
    val phraseAr: String,
    val phraseEn: String,
    val targetCount: Int = 33,
    val hour: Int = 9,
    val minute: Int = 0,
    val specificTimesJson: String = "",
    val repeatIntervalMins: Int = 60,
    val repeatIntervalSeconds: Int = 3600,
    val startFromNow: Boolean = true,
    val startHour: Int = 9,
    val startMinute: Int = 0,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val lastTriggeredMillis: Long = System.currentTimeMillis(),
    val timesPerDay: Int = 5,
    val maxDailyRepeats: Int = 0,
    val durationSeconds: Int = 10,
    val deliveryStyle: TasbeehDeliveryStyle = TasbeehDeliveryStyle.TOAST_POPUP,
    val themeColor: TasbeehThemeColor = TasbeehThemeColor.EMERALD,
    val cardShape: TasbeehCardShape = TasbeehCardShape.ROUNDED,
    val isEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: Int = 0,
    val quietHoursEnd: Int = 7,
    val phrasesJson: String = "",
    val isRandomOrder: Boolean = false,
    val currentPhraseIndex: Int = 0,
    val countOnClickWithDismiss: Boolean = true,
    val whisperVoiceEnabled: Boolean = false
) {
    fun getPhrases(): List<TasbeehPhraseItem> {
        if (phrasesJson.isBlank()) return listOf(TasbeehPhraseItem(phraseAr, phraseEn, targetCount))
        val parsed = phrasesJson.split(";").mapNotNull {
            if (it.isNotBlank()) TasbeehPhraseItem.deserialize(it) else null
        }
        return if (parsed.isNotEmpty()) parsed else listOf(TasbeehPhraseItem(phraseAr, phraseEn, targetCount))
    }

    fun getActivePhrase(random: Boolean = isRandomOrder): TasbeehPhraseItem {
        val list = getPhrases()
        if (list.isEmpty()) return TasbeehPhraseItem(phraseAr, phraseEn, targetCount)
        if (random) return list.random()
        val index = (currentPhraseIndex.coerceAtLeast(0)) % list.size
        return list[index]
    }

    fun getSpecificTimesList(): List<String> {
        if (specificTimesJson.isBlank()) return listOf(String.format("%02d:%02d", hour, minute))
        return specificTimesJson.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun getEffectiveIntervalSeconds(): Int = if (repeatIntervalSeconds > 0) repeatIntervalSeconds else repeatIntervalMins * 60

    fun getIntervalSummary(isArabic: Boolean): String {
        val effectiveSecs = getEffectiveIntervalSeconds()
        if (effectiveSecs == 0) {
            val times = getSpecificTimesList()
            return if (times.size > 1) {
                if (isArabic) "أوقات محددة (${times.size}): ${times.joinToString(", ")}"
                else "Specific times (${times.size}): ${times.joinToString(", ")}"
            } else {
                val timeStr = times.firstOrNull() ?: String.format("%02d:%02d", hour, minute)
                if (isArabic) "يومي عند $timeStr" else "Daily at $timeStr"
            }
        }
        val startNote = if (!startFromNow) {
            val h12 = if (startHour == 0) 12 else if (startHour > 12) startHour - 12 else startHour
            val amPm = if (isArabic) (if (startHour >= 12) "م" else "ص") else (if (startHour >= 12) "PM" else "AM")
            if (isArabic) " (يبدأ %02d:%02d %s)".format(h12, startMinute, amPm)
            else " (starts %02d:%02d %s)".format(h12, startMinute, amPm)
        } else {
            if (isArabic) " (يبدأ من الآن)" else " (starts now)"
        }
        val base = when {
            effectiveSecs < 60 -> if (isArabic) "كل $effectiveSecs ثانية" else "Every ${effectiveSecs}s"
            effectiveSecs % 3600 == 0 -> if (isArabic) "كل ${effectiveSecs / 3600} ساعة" else "Every ${effectiveSecs / 3600}h"
            else -> if (isArabic) "كل ${effectiveSecs / 60} دقيقة" else "Every ${effectiveSecs / 60}m"
        }
        val countInfo = if (getPhrases().size > 1) {
            val modeStr = if (isRandomOrder) (if (isArabic) "عشوائي" else "Random") else (if (isArabic) "متتالي" else "Sequential")
            " • ${getPhrases().size} أذكار ($modeStr)"
        } else ""
        return base + startNote + countInfo
    }
}
