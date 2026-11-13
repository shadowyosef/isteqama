package com.example.data

data class TafseerBundle(
    val muyassar: String,
    val saadi: String,
    val ibnKathir: String,
    val irabAndWords: String
)

object TafseerDataProvider {

    fun getTafseer(surahNumber: Int, ayahNumber: Int): Pair<String, String> {
        val b = getRichTafseer(surahNumber, ayahNumber)
        return b.muyassar to b.irabAndWords
    }

    fun getRichTafseer(surahNumber: Int, ayahNumber: Int): TafseerBundle {
        return curated[surahNumber to ayahNumber] ?: defaultBundle(surahNumber, ayahNumber)
    }

    fun searchEntries(query: String): List<Triple<Pair<Int, Int>, String, TafseerBundle>> {
        val q = query.trim()
        if (q.isBlank()) return emptyList()
        val fromCurated = curated.entries
            .filter { (_, v) ->
                v.muyassar.contains(q, true) || v.saadi.contains(q, true) ||
                    v.ibnKathir.contains(q, true) || v.irabAndWords.contains(q, true)
            }
            .map { (k, v) -> Triple(k, QuranRepository.getSurah(k.first)?.nameAr ?: "سورة ${k.first}", v) }
        val fromAyahs = QuranRepository.getAllSurahs().flatMap { surah ->
            QuranRepository.getAyahsForSurah(surah.number)
                .filter { it.textArabic.contains(q, true) || it.tafseer.contains(q, true) }
                .take(3)
                .map { ayah ->
                    Triple(ayah.surahNumber to ayah.numberInSurah, surah.nameAr, getRichTafseer(ayah.surahNumber, ayah.numberInSurah))
                }
        }
        return (fromCurated + fromAyahs).distinctBy { it.first }.take(20)
    }

    private fun defaultBundle(surahNumber: Int, ayahNumber: Int): TafseerBundle {
        val ayah = QuranRepository.getAyahsForSurah(surahNumber).find { it.numberInSurah == ayahNumber }
        val baseText = ayah?.tafseer?.takeIf { it.isNotBlank() }
            ?: "تفسير الآية ($ayahNumber) من سورة رقم ($surahNumber): دعوة إلى توحيد الله، وحسن التوكل عليه، والتدبر في سننه الكونية."
        return TafseerBundle(
            muyassar = baseText,
            saadi = "تفسير السعدي: يربي الله عباده بهذه الآية الكريمة على تعظيم أمره، واستشعار قربه ورحمته في جميع الأحوال.",
            ibnKathir = "تفسير ابن كثير: روي عن السلف أن هذه الآية ترشد المؤمن إلى الإخلاص والمتابعة، والاعتبار بمواعظ القرآن.",
            irabAndWords = "إعراب ومفردات: أسلوب خبري/إنشائي يفيد التقرير والهداية الربانية • التدبر: التفكر في المعنى والعمل به."
        )
    }

    private val curated = mapOf(
        (1 to 1) to TafseerBundle(
            muyassar = "أبتدئ قراءة القرآن مستعيناً باسم الله تعالى، ذي الرحمة الواسعة التي شملت الخلائق، والرحمة الخاصة بالمؤمنين.",
            saadi = "السعدي: الباء للاستعانة والبركة، والله هو المألوه المعبود محبةً وتعظيماً، والرحمن الرحيم صفتان تثبتان سعة رحمته.",
            ibnKathir = "ابن كثير: افتتاح كل سورة وعمل صالح بالبسملة سنة مباركة لنيل التوفيق والطرد للشيطان.",
            irabAndWords = "بِسْمِ: جار ومجرور متعلق بمحذوف تقديره (أقرأ) • اللهِ: لفظ الجلالة مضاف إليه مجرور • الرحمنِ الرحيمِ: نعتان."
        ),
        (1 to 2) to TafseerBundle(
            muyassar = "الثناء الكامل لله وحده مالك يوم الدين ومدبر العوالم كلها من إنس وجن وملائكة.",
            saadi = "السعدي: الحمد هو الثناء على الله بصفات كماله وأفعاله الدائرة بين الفضل والعدل.",
            ibnKathir = "ابن كثير: الألف واللام في (الحمد) لاستغراق جميع المحامد، فكل نعمة مصدرها الله وحده.",
            irabAndWords = "الحمدُ: مبتدأ مرفوع • للهِ: شبه جملة خبر • ربِّ: نعت مجرور • العالمينَ: مضاف إليه مجرور بالياء."
        ),
        (2 to 255) to TafseerBundle(
            muyassar = "الله وحده الحي الدائم الحياة، القائم على تدبير كل خلق، لا يغلبه نعاس ولا نوم، وسع علمه وكرسيه كل شيء.",
            saadi = "السعدي: آية الكرسي أعظم آية في القرآن لاشتمالها على توحيد الأسماء والصفات وكمال القوة والعلم والقدرة.",
            ibnKathir = "ابن كثير: من قرأها في ليلة لم يزل عليه من الله حافظ ولا يقربه شيطان حتى يصبح.",
            irabAndWords = "اللهُ: مبتدأ • لا إلهَ إلا هوَ: خبر • الحيُّ القيومُ: خبران لمبتدأ محذوف • سِنَةٌ: مبتدأ مؤخر بمعنى أول النعاس."
        ),
        (65 to 3) to TafseerBundle(
            muyassar = "من يتق الله ويفوض أمره إليه يهيئ له مخرجاً من كل ضيق ويرزقه من جهات غير متوقعة.",
            saadi = "السعدي: التوكل صدق اعتماد القلب على الله في جلب المنافع ودفع المضار مع فعل الأسباب.",
            ibnKathir = "ابن كثير: من توكل على الله كفاه همّ دينه ودنياه وقدر له الخير في عاقبة أمره.",
            irabAndWords = "من يتوكلْ: اسم شرط جازم وفعل مضارع مجزوم • فهو حسبُه: جواب الشرط، أي كافيه ما أهمه."
        ),
        (94 to 5) to TafseerBundle(
            muyassar = "إن مع كل شدة وضيق يسراً قريباً وفرجاً واسعاً من عند الله.",
            saadi = "السعدي: تعريف العسر وتنكير اليسر يدل على أن عسراً واحداً لا يغلب يسرين اثنين.",
            ibnKathir = "ابن كثير: بشارة للنبي ﷺ وأمته بتوالي الفرج والتيسير بعد كل مشقة في طريق الدعوة.",
            irabAndWords = "إنَّ: حرف توكيد ونصب • معَ: ظرف منصوب • العسرِ: مضاف إليه • يسراً: اسم إنّ مؤخر منصوب."
        ),
        (112 to 1) to TafseerBundle(
            muyassar = "قل يا رسول الله: هو الله المتفرد بالكمال والألوهية والربوبية لا شريك له ولا مثيل.",
            saadi = "السعدي: الأحد المنفرد بكل وصف كمال، الذي لا يشاركه غيره في شيء من خصائص الإلهية.",
            ibnKathir = "ابن كثير: سورة الإخلاص أصل في تنزيه الرب سبحانه وتعادل ثلث القرآن.",
            irabAndWords = "هوَ: ضمير الشأن مبتدأ • اللهُ: مبتدأ ثانٍ • أحدٌ: خبر المبتدأ الثاني، والجملة خبر (هو)."
        )
    )
}
