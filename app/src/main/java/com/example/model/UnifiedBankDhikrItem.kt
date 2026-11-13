package com.example.model

enum class UnifiedBankItemType {
    ALL,
    ROUTINE,
    TASBEEH,
    MORNING,
    EVENING,
    SLEEP,
    PRAYER,
    CUSTOM
}

data class UnifiedBankDhikrItem(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val textAr: String,
    val textEn: String,
    val targetCount: Int,
    val virtue: String,
    val itemType: UnifiedBankItemType = UnifiedBankItemType.ALL,
    val isRoutineSet: Boolean = false,
    val routineChildCount: Int = 1
)
