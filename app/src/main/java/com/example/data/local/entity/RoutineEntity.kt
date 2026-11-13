package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.RoutineStepItem

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val type: String, // QURAN, ADHKAR, TASBEEH
    val blockerStyle: String, // STRICT, OVERLAY, GENTLE
    val targetAmount: Int,
    val targetSurah: Int = 1,
    val startAyah: Int = 1,
    val endAyah: Int = 7,
    val triggerHour: Int,
    val triggerMinute: Int,
    val daysOfWeek: String = "1,2,3,4,5,6,7",
    val isEnabled: Boolean = true,
    val stepsJson: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getSteps(): List<RoutineStepItem> {
        return RoutineStepItem.deserializeList(stepsJson)
    }
}
