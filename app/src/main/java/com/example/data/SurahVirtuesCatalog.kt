package com.example.data

import com.example.model.SurahInfo

data class SurahVirtuesInfo(
    val surahNumber: Int,
    val revelationTypeAr: String,
    val purposeAr: String,
    val purposeEn: String,
    val virtueAr: String,
    val virtueEn: String,
    val reasonAr: String
)

object SurahVirtuesCatalog {
    fun getInfo(surah: SurahInfo): SurahVirtuesInfo {
        val revAr = if (surah.revelationType.contains("Med", ignoreCase = true) || surah.revelationType.contains("مدن")) "مدنية" else "مكية"
        return curated[surah.number] ?: SurahVirtuesInfo(
            surahNumber = surah.number,
            revelationTypeAr = revAr,
            purposeAr = if (revAr == "مكية") "ترسيخ أصول العقيدة والتوحيد والبعث وتزكية النفس." else "تنظيم أحكام الشريعة وبناء المجتمع المسلم المتماسك.",
            purposeEn = if (revAr == "مكية") "Establishing core Tawheed, faith in the Hereafter, and spiritual purification." else "Establishing community laws, worship discipline, and social harmony.",
            virtueAr = "قراءة كل حرف من كتاب الله بعشر حسنات وتنزل السكينة على قلب قارئها.",
            virtueEn = "Every letter recited earns ten rewards and brings tranquility to the heart.",
            reasonAr = "نزلت لهداية الناس وتثبيت قلب النبي ﷺ والمؤمنين وبيان سنن الله في الكون."
        )
    }

    private val curated = mapOf(
        1 to SurahVirtuesInfo(
            1, "مكية",
            "أم القرآن، تجمع معاني التوحيد والعبادة والدعاء بالهداية.",
            "The Mother of the Quran; summarizes Tawheed, worship, and seeking guidance.",
            "أعظم سورة في القرآن، لا تصح الصلاة إلا بها، وهي رقية وشفاء بإذن الله.",
            "The greatest Surah in the Quran and a cure and pillar of every prayer.",
            "نزلت مجتمعة بمكة لتكون فاتحة الكتاب ومناجاة العبد لربه في كل ركعة."
        ),
        2 to SurahVirtuesInfo(
            2, "مدنية",
            "بيان منهج الاستخلاف في الأرض وأحكام التشريع والتقوى.",
            "Clarifies stewardship on earth, legislation, and God-consciousness.",
            "تطرد الشياطين من البيت وتقرأ فيها أعظم آية (آية الكرسي) وتأتي غمامة يوم القيامة.",
            "Protects the home from Shaytan, houses Ayat Al-Kursi, and shades its reciter on Judgment Day.",
            "نزلت آياتها تباعاً بالمدينة لتأسيس الدولة والمجتمع المسلم وتثبيت المؤمنين."
        ),
        18 to SurahVirtuesInfo(
            18, "مكية",
            "العصمة من الفتن الأربع: فتنة الدين والمال والعلم والسلطان.",
            "Protection against the four trials: faith, wealth, knowledge, and power.",
            "من قرأها يوم الجمعة أضاء له من النور ما بين الجمعتين، وعشر آيات منها تعصم من الدجال.",
            "Illuminates Friday to Friday with light; its first 10 verses protect from Dajjal.",
            "نزلت جواباً لأسئلة قريش بتوجيه أحبار المدينة عن أهل الكهف وذي القرنين والروح."
        ),
        36 to SurahVirtuesInfo(
            36, "مكية",
            "إثبات النبوة والوحي والبعث والنشور بدلائل الآفاق والأنفس.",
            "Affirms prophethood, revelation, and resurrection through cosmic signs.",
            "قلب القرآن، وتغرس اليقين بلقاء الله في القلب عند تلاوتها وتدبرها.",
            "Known as the Heart of the Quran, inspiring certainty in meeting Allah.",
            "نزلت بمكة لتسلية النبي ﷺ عن تكذيب المشركين وبيان عاقبة المكذبين."
        ),
        55 to SurahVirtuesInfo(
            55, "مدنية",
            "تذكير بنعم الله الظاهرة والباطنة في الدنيا والآخرة ودعوة للشكر.",
            "Reminds of Allah's visible and hidden blessings, calling to gratitude.",
            "عروس القرآن، تكررت فيها (فبأي آلاء ربكما تكذبان) لإيقاظ القلب بالامتنان.",
            "Called the Bride of the Quran, cultivating deep gratitude for Allah's blessings.",
            "نزلت لبيان فضل تعليم القرآن وتسخير الكون ولطف الله بعباده المؤمنين."
        ),
        67 to SurahVirtuesInfo(
            67, "مكية",
            "بيان ملك الله التام للسموات والأرض والتدبر في إحكام الخلق.",
            "Declares Allah's absolute sovereignty and perfection in creation.",
            "المانعة المنجية، تشفع لصاحبها حتى يُغفر له وتنجي من عذاب القبر.",
            "The Protector Surah; intercedes for its constant reciter and protects in the grave.",
            "نزلت لتنبيه الغافلين أن الله عليم بما في الصدور وأنه وحده بيده الملك والرزق."
        ),
        112 to SurahVirtuesInfo(
            112, "مكية",
            "تنزيه الله وتفرده بالوحدانية المطلقة والكمال المطلق.",
            "Pure declaration of Allah's absolute Oneness and self-sufficiency.",
            "تعدل ثلث القرآن الكريم في الثواب والمعنى، وحبها سبب لدخول الجنة.",
            "Equivalent to one-third of the Quran in reward and core theological meaning.",
            "سأل المشركون النبي ﷺ: انسب لنا ربك، فأنزل الله (قل هو الله أحد) جواباً فاصلاً."
        )
    )
}
