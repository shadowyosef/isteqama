package com.example.data

data class QuranRadioStation(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val descAr: String,
    val streamUrl: String
)

object QuranRadioCatalog {
    val stations = listOf(
        QuranRadioStation(
            id = "cairo_quran",
            nameAr = "إذاعة القرآن الكريم (القاهرة)",
            nameEn = "Quran Radio Cairo",
            descAr = "تلاوات خاشعة لكبار القراء المصريين على مدار الساعة",
            streamUrl = "https://stream.radiojar.com/8s5u5tpdtwzuv"
        ),
        QuranRadioStation(
            id = "tarateel",
            nameAr = "إذاعة تراتيل وتلاوات نادرة",
            nameEn = "Blessed Tarateel Radio",
            descAr = "تلاوات متواصلة بأصوات ندية خاشعة",
            streamUrl = "https://backup.qurango.net/radio/tarateel"
        ),
        QuranRadioStation(
            id = "alafasy_radio",
            nameAr = "إذاعة الشيخ مشاري العفاسي",
            nameEn = "Mishary Alafasy Radio",
            descAr = "بث متواصل للقرآن الكريم بصوت العفاسي",
            streamUrl = "https://backup.qurango.net/radio/mishary_alafasi"
        ),
        QuranRadioStation(
            id = "abdulbasit_radio",
            nameAr = "إذاعة الشيخ عبد الباسط (المجوّد)",
            nameEn = "Abdulbasit Mojawwad Radio",
            descAr = "تلاوات مجودة خالدة على مدار الساعة",
            streamUrl = "https://backup.qurango.net/radio/abdulbasit_abdulsamad_mojawwad"
        ),
        QuranRadioStation(
            id = "maher_radio",
            nameAr = "إذاعة الشيخ ماهر المعيقلي",
            nameEn = "Maher Al-Muaiqly Radio",
            descAr = "تلاوات الحرم المكي المبارك",
            streamUrl = "https://backup.qurango.net/radio/maher_al_muaiqly"
        ),
        QuranRadioStation(
            id = "sudais_radio",
            nameAr = "إذاعة الحرم المكي (السديس)",
            nameEn = "Al-Sudais Haramain Radio",
            descAr = "بث قرآني روحاني متواصل",
            streamUrl = "https://backup.qurango.net/radio/abdulrahman_alsudaes"
        )
    )
}
