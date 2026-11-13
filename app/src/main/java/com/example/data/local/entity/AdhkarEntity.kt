package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adhkar")
data class AdhkarEntity(
    @PrimaryKey
    val id: String,
    val category: String, // MORNING, EVENING, SLEEP, PRAYER, CUSTOM
    val textAr: String,
    val textEn: String,
    val virtue: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val isCustom: Boolean = false,
    val orderIndex: Int = 0
)
