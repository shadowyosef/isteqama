package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.LockContentConfig
import com.example.model.LockScheduleSlot

@Entity(tableName = "screen_locks")
data class ScreenLockEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val lockType: String,
    val targetAmount: Int = 10,
    val triggerHour: Int = 6,
    val triggerMinute: Int = 0,
    val recurrenceDays: String = "1,2,3,4,5,6,7",
    val specificDate: String? = null,
    val strictnessMode: String = "STRICT_KIOSK",
    val isEnabled: Boolean = true,
    val schedulesJson: String = "",
    val contentConfigJson: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getContentConfig(): LockContentConfig = LockContentConfig.deserialize(contentConfigJson)

    fun getScheduleSlots(): List<LockScheduleSlot> {
        if (schedulesJson.isBlank()) {
            val days = recurrenceDays.split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .ifEmpty { listOf(1, 2, 3, 4, 5, 6, 7) }
            return listOf(
                LockScheduleSlot(
                    id = "${id}_default",
                    hour = triggerHour,
                    minute = triggerMinute,
                    daysOfWeek = days
                )
            )
        }
        return parseScheduleSlots(schedulesJson)
    }

    companion object {
        fun parseScheduleSlots(raw: String): List<LockScheduleSlot> {
            val list = mutableListOf<LockScheduleSlot>()
            raw.split(";").forEach { item ->
                val parts = item.split("|")
                if (parts.size >= 4) {
                    val h = parts[1].toIntOrNull() ?: 6
                    val m = parts[2].toIntOrNull() ?: 0
                    val days = parts[3].split(",").mapNotNull { it.trim().toIntOrNull() }
                        .ifEmpty { listOf(1, 2, 3, 4, 5, 6, 7) }
                    val interval = parts.getOrNull(4)?.toIntOrNull() ?: 0
                    val maxRepeats = parts.getOrNull(5)?.toIntOrNull() ?: 0
                    val quietEnabled = parts.getOrNull(6)?.toBooleanStrictOrNull() ?: false
                    val quietStart = parts.getOrNull(7)?.toIntOrNull()?.coerceIn(0, 23) ?: 0
                    val quietEnd = parts.getOrNull(8)?.toIntOrNull()?.coerceIn(0, 23) ?: 7
                    list.add(
                        LockScheduleSlot(
                            id = parts[0],
                            hour = h,
                            minute = m,
                            daysOfWeek = days,
                            intervalHours = interval / 60,
                            intervalMinutes = interval,
                            maxDailyRepeats = maxRepeats.coerceAtLeast(0),
                            quietHoursEnabled = quietEnabled,
                            quietHoursStart = quietStart,
                            quietHoursEnd = quietEnd
                        )
                    )
                }
            }
            return if (list.isEmpty()) listOf(LockScheduleSlot()) else list
        }

        fun serializeSlots(slots: List<LockScheduleSlot>): String {
            return slots.joinToString(";") { slot ->
                val mins = if (slot.intervalMinutes > 0) slot.intervalMinutes else slot.intervalHours * 60
                "${slot.id}|${slot.hour}|${slot.minute}|${slot.daysOfWeek.joinToString(",")}|$mins|${slot.maxDailyRepeats}|${slot.quietHoursEnabled}|${slot.quietHoursStart}|${slot.quietHoursEnd}"
            }
        }
    }
}
