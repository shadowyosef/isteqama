package com.example.data

import android.content.Context
import com.example.model.MissedPrayerRecord
import com.example.model.MissedPrayerType
import kotlinx.coroutines.flow.MutableStateFlow

object MissedPrayerRepository {
    private const val PREFS_NAME = "missed_prayers_prefs"

    val missedPrayers = MutableStateFlow<Map<MissedPrayerType, Int>>(
        MissedPrayerType.values().associateWith { 0 }
    )

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val loaded = MissedPrayerType.values().associateWith { type ->
            prefs.getInt("missed_${type.name}", 0)
        }
        missedPrayers.value = loaded
    }

    fun addMissed(context: Context, type: MissedPrayerType, delta: Int = 1) {
        val current = missedPrayers.value[type] ?: 0
        val next = (current + delta).coerceAtLeast(0)
        val updated = missedPrayers.value.toMutableMap()
        updated[type] = next
        missedPrayers.value = updated
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putInt("missed_${type.name}", next).apply()
    }

    fun markPrayed(context: Context, type: MissedPrayerType) {
        addMissed(context, type, -1)
        StatsRepository.recordDhikrCount(1)
    }

    fun getTotalMissed(): Int = missedPrayers.value.values.sum()
}
