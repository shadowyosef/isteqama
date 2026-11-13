package com.example.model

enum class TasbeehPresetCategory(val id: String, val labelAr: String, val labelEn: String) {
    ALL("all", "الكل", "All"),
    TASBEEH("tasbeeh", "تسبيح وتنزيه", "Glorification"),
    TAHMEED_TAKBEER("tahmeed", "تحميد وتكبير", "Praise & Takbeer"),
    ISTIGHFAR_TAWBAH("istighfar", "استغفار وتوبة", "Istighfar"),
    HAWQALA_TAWHEED("tawheed", "حوقلة وتوحيد", "Tawheed & Hawqala"),
    SALAWAT("salawat", "صلاة على النبي ﷺ", "Salawat"),
    DUAA_RELIEF("duaa", "أدعية وتفريج كرب", "Duaas & Relief")
}

data class ComprehensiveTasbeehItem(
    val id: String,
    val phraseAr: String,
    val phraseEn: String,
    val defaultTarget: Int = 33,
    val category: TasbeehPresetCategory,
    val virtueAr: String = "",
    val virtueEn: String = ""
)
