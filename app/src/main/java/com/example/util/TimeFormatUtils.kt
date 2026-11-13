package com.example.util

object TimeFormatUtils {
    fun format12Hour(hour: Int, minute: Int, isArabic: Boolean): String {
        val normalizedHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val isPm = hour >= 12
        val amPm = if (isArabic) {
            if (isPm) "م" else "ص"
        } else {
            if (isPm) "PM" else "AM"
        }
        return String.format("%02d:%02d %s", normalizedHour, minute, amPm)
    }

    fun formatInterval(intervalSeconds: Int, isArabic: Boolean): String {
        return when {
            intervalSeconds <= 0 -> if (isArabic) "بدون تكرار" else "No Repeat"
            intervalSeconds < 60 -> {
                if (isArabic) "كل $intervalSeconds ثانية" else "Every ${intervalSeconds}s"
            }
            intervalSeconds % 3600 == 0 -> {
                val hours = intervalSeconds / 3600
                if (isArabic) "كل $hours ساعة" else "Every ${hours}h"
            }
            else -> {
                val minutes = intervalSeconds / 60
                if (isArabic) "كل $minutes دقيقة" else "Every ${minutes}m"
            }
        }
    }
}
