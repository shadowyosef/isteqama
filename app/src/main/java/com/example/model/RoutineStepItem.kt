package com.example.model

import java.util.UUID

data class RoutineStepItem(
    val id: String = UUID.randomUUID().toString(),
    val textAr: String,
    val textEn: String = "",
    val targetCount: Int = 33,
    val order: Int = 0
) {
    fun serialize(): String {
        return "$id|$textAr|$textEn|$targetCount|$order"
    }

    companion object {
        fun deserialize(raw: String): RoutineStepItem? {
            val parts = raw.split("|")
            if (parts.size < 5) return null
            return RoutineStepItem(
                id = parts[0],
                textAr = parts[1],
                textEn = parts[2],
                targetCount = parts[3].toIntOrNull() ?: 33,
                order = parts[4].toIntOrNull() ?: 0
            )
        }

        fun serializeList(list: List<RoutineStepItem>): String {
            return list.joinToString(";") { it.serialize() }
        }

        fun deserializeList(raw: String): List<RoutineStepItem> {
            if (raw.isBlank()) return emptyList()
            return raw.split(";").mapNotNull { deserialize(it) }
        }
    }
}
