package com.example.model

enum class IslamicQuizCategory(val labelAr: String, val labelEn: String) {
    QURAN("القرآن الكريم", "Holy Quran"),
    PROPHETS("قصص الأنبياء", "Prophets Stories"),
    FIQH("فقه وعبادات", "Fiqh & Worship"),
    SEERAH("السيرة النبوية", "Prophetic Seerah")
}

data class IslamicQuizQuestion(
    val id: String,
    val questionAr: String,
    val questionEn: String,
    val optionsAr: List<String>,
    val optionsEn: List<String>,
    val correctIndex: Int,
    val explanationAr: String = "",
    val explanationEn: String = "",
    val category: IslamicQuizCategory = IslamicQuizCategory.QURAN
)
