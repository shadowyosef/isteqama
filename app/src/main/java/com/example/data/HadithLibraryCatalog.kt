package com.example.data

data class HadithItem(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val arabicText: String,
    val englishText: String,
    val narratorAr: String,
    val sourceAr: String,
    val benefitAr: String
)

object HadithLibraryCatalog {
    val hadiths = listOf(
        HadithItem("h1", "الأعمال بالنيات", "Actions by Intentions",
            "«إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى»",
            "Actions are judged by intentions, and every person shall have what they intended.",
            "عمر بن الخطاب رضي الله عنه", "متفق عليه (البخاري ومسلم)",
            "استحضر نية خالصة لله في كل طاعة وعمل دنيوي مباح ليتضاعف أجرك."),
        HadithItem("h2", "الدين النصيحة", "Religion is Sincere Advice",
            "«الدِّينُ النَّصِيحَةُ» قلنا: لمن يا رسول الله؟ قال: «لِلَّهِ وَلِكِتَابِهِ وَلِرَسُولِهِ وَلِأَئِمَّةِ الْمُسْلِمِينَ وَعَامَّتِهِمْ»",
            "Religion is sincerity and sincere advice: to Allah, His Book, His Messenger, and all Muslims.",
            "تميم الداري رضي الله عنه", "رواه مسلم",
            "الإخلاص لله ومحبة الخير للناس أساس ثبات القلب واستقامة العمل."),
        HadithItem("h3", "حُسْن إسلام المرء", "Leaving What Concerns You Not",
            "«مِنْ حُسْنِ إِسْلَامِ الْمَرْءِ تَرْكُهُ مَا لَا يَعْنِيهِ»",
            "Part of the perfection of one's Islam is leaving that which does not concern them.",
            "أبو هريرة رضي الله عنه", "رواه الترمذي",
            "انشغل بما ينفع قلبك ووقتك واترك فضول التصفح والجدال."),
        HadithItem("h4", "لا تغضب", "Control Your Anger",
            "قال رجل للنبي ﷺ: أوصني. قال: «لَا تَغْضَبْ»، فردَّدَ مِراراً، قال: «لَا تَغْضَبْ»",
            "Do not become angry; hold your composure and respond with patience.",
            "أبو هريرة رضي الله عنه", "رواه البخاري",
            "الحِلم وضبط النفس عند الاستفزاز من أعظم أبواب السلامة والبركة."),
        HadithItem("h5", "أحب الأعمال أدومها", "Consistent Deeds",
            "«أَحَبُّ الأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ»",
            "The most beloved of deeds to Allah are those done most consistently, even if small.",
            "عائشة رضي الله عنها", "متفق عليه",
            "ورد يومي ثابت من القرآن والذكر خير من جهد متقطع لا يدوم."),
        HadithItem("h6", "الكلمة الطيبة صدقة", "A Good Word is Charity",
            "«الكَلِمَةُ الطَّيِّبَةُ صَدَقَةٌ، وَكُلُّ خُطْوَةٍ تَمْشِيهَا إِلَى الصَّلَاةِ صَدَقَةٌ»",
            "A kind word is charity, and every step walked toward prayer is charity.",
            "أبو هريرة رضي الله عنه", "متفق عليه",
            "المسارعة إلى الصلاة واللين في الكلام صدقات جارية ميسرة كل يوم."),
        HadithItem("h7", "يسّروا ولا تعسّروا", "Bring Ease and Glad Tidings",
            "«يَسِّرُوا وَلَا تُعَسِّرُوا، وَبَشِّرُوا وَلَا تُنَفِّرُوا»",
            "Make things easy and do not make them difficult; give glad tidings and do not repel people.",
            "أنس بن مالك رضي الله عنه", "متفق عليه",
            "روح الإسلام تقوم على التيسير وبث الأمل والرحمة في القلوب."),
        HadithItem("h8", "اتق الله حيثما كنت", "Be Mindful of Allah",
            "«اتَّقِ اللَّهَ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الْحَسَنَةَ تَمْحُهَا، وَخَالِقِ النَّاسَ بِخُلُقٍ حَسَنٍ»",
            "Be mindful of Allah wherever you are, follow a bad deed with a good deed to erase it, and treat people kindly.",
            "أبو ذر ومعاذ رضي الله عنهما", "رواه الترمذي",
            "مراقبة الله في السر والعلن والمبادرة بالاستغفار وحسن الخلق منهج حياة."),
        HadithItem("h9", "من سلك طريقاً للعلم", "Path of Knowledge",
            "«مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا، سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ»",
            "Whoever treads a path seeking knowledge, Allah makes easy for them a path to Paradise.",
            "أبو هريرة رضي الله عنه", "رواه مسلم",
            "التفقه في القرآن والحديث يرفع الدرجات ويزيد الخشية."),
        HadithItem("h10", "لسان رطب بذكر الله", "Keep Tongue Moist with Dhikr",
            "«لَا يَزَالُ لِسَانُكَ رَطْبًا مِنْ ذِكْرِ اللَّهِ»",
            "Let your tongue remain constantly moist with the remembrance of Allah.",
            "عبد الله بن بسر رضي الله عنه", "رواه الترمذي",
            "التسبيح والاستغفار الدائم يحيي القلب ويطرد الغفلة.")
    )

    fun search(query: String): List<HadithItem> {
        val q = query.trim()
        if (q.isEmpty()) return hadiths
        return hadiths.filter {
            it.titleAr.contains(q, true) || it.arabicText.contains(q, true) ||
                it.titleEn.contains(q, true) || it.englishText.contains(q, true)
        }
    }
}
