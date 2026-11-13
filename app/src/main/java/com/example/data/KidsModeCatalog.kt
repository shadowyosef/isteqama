package com.example.data

data class KidsStepItem(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val textAr: String,
    val emoji: String,
    val starsReward: Int = 5
)

data class KidsQuizQuestion(
    val questionAr: String,
    val questionEn: String,
    val options: List<String>,
    val correctIdx: Int
)

object KidsModeCatalog {
    val dailySteps = listOf(
        KidsStepItem("k_bismillah", "أذكار الصباح للأطفال", "Kids Morning Dhikr", "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، رَبِّ زِدْنِي عِلْمًا", "☀️", 5),
        KidsStepItem("k_ikhlas", "سورة الإخلاص (٣ مرات)", "Surah Al-Ikhlas (3x)", "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "🌟", 10),
        KidsStepItem("k_parents", "دعاء الوالدين", "Dua for Parents", "رَبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا", "❤️", 5),
        KidsStepItem("k_sleep", "ذكر قبل النوم", "Before Sleep Dhikr", "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا", "🌙", 5)
    )

    val kidsQuiz = listOf(
        KidsQuizQuestion("ما هو عدد الصلوات المفروضة في اليوم والليلة؟", "How many daily obligatory prayers?", listOf("٥ صلوات", "٣ صلوات", "٧ صلوات"), 0),
        KidsQuizQuestion("ما أول سورة في المصحف الشريف؟", "What is the first Surah in the Quran?", listOf("سورة الفاتحة", "سورة الناس", "سورة البقرة"), 0),
        KidsQuizQuestion("ماذا نقول قبل البدء بالطعام؟", "What do we say before eating?", listOf("بسم الله", "الحمد لله", "سبحان الله"), 0)
    )
}
