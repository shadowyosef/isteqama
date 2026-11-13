package com.example.model

enum class DhikrCategory {
    MORNING,
    EVENING,
    SLEEP,
    PRAYER,
    CUSTOM
}

data class DhikrItem(
    val id: String,
    val category: DhikrCategory,
    val textAr: String,
    val textEn: String,
    val virtue: String,
    val targetCount: Int,
    var currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val isCustom: Boolean = false,
    val titleAr: String = "",
    val titleEn: String = ""
) {
    constructor(
        id: String,
        category: DhikrCategory,
        titleAr: String,
        titleEn: String,
        textAr: String,
        textEn: String,
        virtue: String,
        targetCount: Int
    ) : this(
        id = id,
        category = category,
        textAr = textAr,
        textEn = textEn,
        virtue = virtue,
        targetCount = targetCount,
        currentCount = 0,
        isCompleted = false,
        isCustom = false,
        titleAr = titleAr,
        titleEn = titleEn
    )
}

data class TasbeehItem(
    val id: String,
    val phraseAr: String,
    val phraseEn: String,
    val target: Int = 33,
    var count: Int = 0,
    val totalCount: Int = 0
)

data class TasbeehSet(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val items: List<TasbeehItem>,
    val isCustom: Boolean = false
)
