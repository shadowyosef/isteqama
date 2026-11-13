package com.example.model

data class ReciterInfo(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val styleAr: String = "مرتل",
    val styleEn: String = "Murattal",
    val everyAyahFolder: String = "Alafasy_128kbps"
)

object ReciterListProvider {
    val reciters = listOf(
        ReciterInfo("ar.alafasy", "الشيخ مشاري راشد العفاسي", "Mishary Rashid Alafasy", "حفص عن عاصم", "Hafs", "Alafasy_128kbps"),
        ReciterInfo("ar.abdulbasitmurattal", "الشيخ عبد الباسط عبد الصمد", "Abdulbasit Abdulsamad", "مرتل", "Murattal", "Abdul_Basit_Murattal_192kbps"),
        ReciterInfo("ar.abdulbasitmujawwad", "الشيخ عبد الباسط عبد الصمد", "Abdulbasit Abdulsamad", "مجود", "Mujawwad", "Abdul_Basit_Mujawwad_128kbps"),
        ReciterInfo("ar.husary", "الشيخ محمود خليل الحصري", "Mahmoud Khalil Al-Husary", "مرتل", "Murattal", "Husary_128kbps"),
        ReciterInfo("ar.minshawi", "الشيخ محمد صديق المنشاوي", "Mohamed Siddiq Al-Minshawi", "مرتل", "Murattal", "Minshawy_Murattal_128kbps"),
        ReciterInfo("ar.minshawimujawwad", "الشيخ محمد صديق المنشاوي", "Mohamed Siddiq Al-Minshawi", "مجود", "Mujawwad", "Minshawy_Mujawwad_192kbps"),
        ReciterInfo("ar.saoodshuraym", "الشيخ سعود الشريم", "Saood Ash-Shuraym", "الحرم المكي", "Mecca", "Saood_ash-Shuraym_128kbps"),
        ReciterInfo("ar.abdurrahmaansudais", "الشيخ عبد الرحمن السديس", "Abdur-Rahman As-Sudais", "الحرم المكي", "Mecca", "Abdurrahmaan_As-Sudais_192kbps"),
        ReciterInfo("ar.mahermuaiqly", "الشيخ ماهر المعيقلي", "Maher Al-Muaiqly", "الحرم المكي", "Mecca", "Maher_AlMuaiqly_64kbps"),
        ReciterInfo("ar.ahmedajamy", "الشيخ أحمد بن علي العجمي", "Ahmed Al-Ajamy", "مرتل", "Murattal", "Ahmed_ibn_Ali_al-Ajamy_128kbps_ketaballah.net"),
        ReciterInfo("ar.saadalghamdi", "الشيخ سعد الغامدي", "Saad Al-Ghamdi", "مرتل", "Murattal", "Ghamadi_40kbps"),
        ReciterInfo("ar.hudhaify", "الشيخ علي بن عبد الرحمن الحذيفي", "Ali Al-Hudhaify", "الحرم النبوي", "Medina", "Hudhaify_128kbps"),
        ReciterInfo("ar.shaatree", "الشيخ أبو بكر الشاطري", "Abu Bakr Ash-Shatri", "مرتل", "Murattal", "Abu_Bakr_Ash-Shaatree_128kbps"),
        ReciterInfo("ar.aymanswoid", "د. أيمن رشدي سويد", "Dr. Ayman Suwayd", "تجويد وإتقان", "Tajweed", "Ayman_Sowaid_64kbps"),
        ReciterInfo("ar.faresabbad", "الشيخ فارس عباد", "Fares Abbad", "مرتل خاشع", "Emotional", "Fares_Abbad_64kbps"),
        ReciterInfo("ar.nasserqatami", "الشيخ ناصر القطامي", "Nasser Al-Qatami", "تلاوة خاشعة", "Emotional", "Nasser_Alqatami_128kbps"),
        ReciterInfo("ar.yasserdossari", "الشيخ ياسر الدوسري", "Yasser Al-Dossari", "الحرم المكي", "Mecca", "Yasser_Ad-Dussary_128kbps"),
        ReciterInfo("ar.muhammadayyub", "الشيخ محمد أيوب", "Muhammad Ayyub", "الحرم النبوي", "Medina", "Muhammad_Ayyoub_128kbps"),
        ReciterInfo("ar.muhammadjibreel", "الشيخ محمد جبريل", "Muhammad Jibreel", "مرتل", "Murattal", "Muhammad_Jibreel_128kbps"),
        ReciterInfo("ar.hanirifai", "الشيخ هاني الرفاعي", "Hani Ar-Rifai", "تلاوة خاشعة", "Emotional", "Hani_Rifai_192kbps"),
        ReciterInfo("ar.ibrahimalakhdar", "الشيخ إبراهيم الأخضر", "Ibrahim Al-Akhdar", "الحرم النبوي", "Medina", "Ibrahim_Akhdar_32kbps")
    )

    fun getReciter(id: String): ReciterInfo {
        return reciters.find { it.id == id } ?: reciters.first()
    }
}
