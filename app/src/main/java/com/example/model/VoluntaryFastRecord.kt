package com.example.model

enum class VoluntaryFastType(val labelAr: String, val labelEn: String, val rewardNoteAr: String) {
    MONDAY_THURSDAY("صيام الإثنين والخميس", "Monday & Thursday", "تُعرض فيهما الأعمال على الله تعالى"),
    WHITE_DAYS("الأيام البيض (13، 14، 15)", "White Days (13, 14, 15)", "صيام ثلاثة أيام من كل شهر كصيام الدهر"),
    ASHURA("عاشوراء وتاسوعاء", "Ashura & Tasu'a", "يكفّر السنة الماضية"),
    ARAFAT("يوم عرفة لغير الحاج", "Day of Arafat", "يكفّر السنة الماضية والباقية"),
    GENERAL_VOLUNTARY("صيام تطوع عام", "General Voluntary Fast", "من صام يوماً باعد الله وجهه عن النار سبعين خريفاً")
}

data class VoluntaryFastRecord(
    val id: String,
    val dateIso: String,
    val fastType: VoluntaryFastType,
    val isCompleted: Boolean = false
)
