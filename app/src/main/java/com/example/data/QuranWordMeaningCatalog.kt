package com.example.data

import com.example.util.VoiceHifzVerifier

data class WordMeaningEntry(
    val wordAr: String,
    val rootAr: String,
    val meaningAr: String,
    val meaningEn: String
)

object QuranWordMeaningCatalog {
    private val glossary = listOf(
        WordMeaningEntry("الحمد", "ح م د", "الثناء الكامل على الله بصفاته وأفعاله مع المحبة والتعظيم.", "All praise and gratitude with love and reverence."),
        WordMeaningEntry("العالمين", "ع ل م", "كل ما سوى الله من الإنس والجن والملائكة والكون.", "All creation: worlds of humans, jinn, angels, and cosmos."),
        WordMeaningEntry("الرحمن", "ر ح م", "ذو الرحمة الواسعة الشاملة لجميع الخلق في الدنيا.", "The Entirely Merciful whose mercy encompasses all creation."),
        WordMeaningEntry("الرحيم", "ر ح م", "المتفضل بالرحمة الخاصة بعباده المؤمنين.", "The Especially Merciful toward the believers."),
        WordMeaningEntry("الصراط", "ص ر ط", "الطريق الواضح المستقيم الموصل إلى رضوان الله.", "The clear, straight path leading to Allah's pleasure."),
        WordMeaningEntry("المستقيم", "ق و م", "الذي لا اعوجاج فيه، وهو الإسلام واتباع الوحي.", "Upright without any crookedness; the path of revelation."),
        WordMeaningEntry("القيوم", "ق ي م", "القائم بنفسه المقيم لغيره، لا يغفل ولا يزول.", "The Sustainer and Maintainer of all existence."),
        WordMeaningEntry("سنة", "و س ن", "نعاس أو فتور يسبق النوم، والله منزه عن ذلك.", "Drowsiness or slumber preceding sleep (Allah is free from it)."),
        WordMeaningEntry("الكهف", "ك ه ف", "الغار الواسع في الجبل الذي أوى إليه الفتية المؤمنون.", "The spacious mountain cave where the believing youths sought refuge."),
        WordMeaningEntry("رقيم", "ر ق م", "اللوح المكتوب فيه أسماء أصحاب الكهف وقصتهم.", "The inscribed tablet recording the story of the Cave Companions."),
        WordMeaningEntry("تبارك", "ب ر ك", "تعالى وكثر خيره ودام فضله سبحانه.", "Blessed, exalted, and abundant in endless goodness."),
        WordMeaningEntry("تفاوت", "ف و ت", "خلل أو عدم تناسق واضطراب في الخلق.", "Inconsistency, flaw, or disproportion in creation."),
        WordMeaningEntry("فطور", "ف ط ر", "شقوق وصدوع؛ أي أن السماء خالية من أي عيب.", "Cracks, fissures, or defects in the heavens."),
        WordMeaningEntry("الصمد", "ص م د", "السيد الذي يقصد في الحوائج كلها، المستغني عن كل أحد.", "The Eternal Refuge sought by all creation, needing none."),
        WordMeaningEntry("كفوا", "ك ف أ", "مماثلاً ونظيراً ومساوياً في الكمال.", "Comparable, equal, or equivalent in perfection."),
        WordMeaningEntry("الفلق", "ف ل ق", "الصبح الذي ينفلق عنه ظلام الليل.", "Daybreak cleaving open the darkness of night."),
        WordMeaningEntry("غاسق", "غ س ق", "الليل الشديد الظلام وما ينتشر فيه.", "Darkness of night as it gathers deeply."),
        WordMeaningEntry("وقب", "و ق ب", "دخل وأظلم واشتد ظلامه.", "Settled and spread intensely in darkness."),
        WordMeaningEntry("الوسواس", "و س و س", "الذي يلقي الخواطر السيئة في الصدور خفية.", "The persistent whisperer casting doubts secretly into hearts."),
        WordMeaningEntry("الخناس", "خ ن س", "الذي يتأخر وينقبض ويختفي عند ذكر الله.", "The one who retreats and shrinks away upon remembrance of Allah."),
        WordMeaningEntry("كوثر", "ك ث ر", "الخير الكثير ونهر عظيم في الجنة أُعطي للنبي ﷺ.", "Abundant goodness and a blessed river in Paradise."),
        WordMeaningEntry("ابتر", "ب ت ر", "المنقطع الأثر والذكر القليل الخير.", "Cut off from legacy, memory, and blessing."),
        WordMeaningEntry("تقوى", "و ق ي", "أن تجعل بينك وبين عذاب الله وقاية بفعل أوامره واجتناب نواهيه.", "Mindful reverence and shield against harm through obedience."),
        WordMeaningEntry("مغفرة", "غ ف ر", "ستر الذنب والعفو عنه ومحوه بفضل الله.", "Covering and erasing sins completely by Divine grace."),
        WordMeaningEntry("سبحان", "س ب ح", "تنزيه الله وتقديسه عن كل نقص وعيب.", "Glorifying Allah far above every imperfection."),
        WordMeaningEntry("تواب", "ت و ب", "كثير القبول لتوبة عباده والرجوع عليهم بالرحمة.", "The Ever-Accepting of sincere repentance."),
        WordMeaningEntry("علي", "ع ل و", "الرفيع الشأن والقدر فوق خلقه جميعاً.", "The Most High in majesty, authority, and rank."),
        WordMeaningEntry("عظيم", "ع ظ م", "الجامع لصفات الجلال والكمال بلا حدود.", "The Supreme, possessing boundless greatness.")
    ).associateBy { VoiceHifzVerifier.normalizeArabic(it.wordAr) }

    fun lookupWord(rawToken: String): WordMeaningEntry {
        val clean = VoiceHifzVerifier.normalizeArabic(rawToken).removePrefix("و").removePrefix("ف").removePrefix("ب")
        val direct = glossary[VoiceHifzVerifier.normalizeArabic(rawToken)] ?: glossary[clean]
            ?: glossary[clean.removePrefix("ال")] ?: glossary.entries.firstOrNull { clean.contains(it.key) || it.key.contains(clean) }?.value
        if (direct != null) return direct
        val rootGuess = clean.removePrefix("ال").take(4)
        return WordMeaningEntry(
            wordAr = rawToken.trim(),
            rootAr = rootGuess.toCharArray().joinToString(" "),
            meaningAr = "لفظة قرآنية كريمة من جذرها ($rootGuess) — اضغط أيقونة التفسير لقراءة السياق الكامل للآية.",
            meaningEn = "Quranic term derived near root ($rootGuess) — open full Tafseer for complete verse context."
        )
    }
}
