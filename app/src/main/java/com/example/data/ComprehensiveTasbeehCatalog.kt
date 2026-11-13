package com.example.data

import com.example.model.ComprehensiveTasbeehItem
import com.example.model.TasbeehPresetCategory

object ComprehensiveTasbeehCatalog {
    val items: List<ComprehensiveTasbeehItem> = listOf(
        // Tasbeeh
        ComprehensiveTasbeehItem("t1", "سُبْحَانَ اللَّهِ", "Subhan Allah", 33, TasbeehPresetCategory.TASBEEH, "تنزيه الله عن كل نقص", "Glorifying Allah above all deficiency"),
        ComprehensiveTasbeehItem("t2", "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", "Subhan Allahi wa bihamdihi", 100, TasbeehPresetCategory.TASBEEH, "حُطّت خطاياه وإن كانت مثل زبد البحر", "Sins forgiven even if like sea foam"),
        ComprehensiveTasbeehItem("t3", "سُبْحَانَ اللَّهِ الْعَظِيمِ", "Subhan Allahil Azeem", 33, TasbeehPresetCategory.TASBEEH, "تسبيح العظيم جل جلاله", "Glorifying the Almighty"),
        ComprehensiveTasbeehItem("t4", "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ", "Subhan Allahi wa bihamdihi, Subhan Allahil Azeem", 33, TasbeehPresetCategory.TASBEEH, "كلمتان خفيفتان على اللسان ثقيلتان في الميزان", "Two words beloved to the Most Merciful"),
        ComprehensiveTasbeehItem("t5", "سُبْحَانَ الْمَلِكِ الْقُدُّوسِ", "Subhanal Malikil Quddoos", 3, TasbeehPresetCategory.TASBEEH, "يقال دبر الوتر", "Sunnah recited after Witr"),
        ComprehensiveTasbeehItem("t6", "سُبْحَانَ اللَّهِ عَدَدَ خَلْقِهِ وَرِضَا نَفْسِهِ وَزِنَةَ عَرْشِهِ وَمِدَادَ كَلِمَاتِهِ", "Subhan Allah adada khalqihi...", 3, TasbeehPresetCategory.TASBEEH, "تعدل ساعات من الذكر المتواصل", "Weighs more than hours of Dhikr"),

        // Tahmeed & Takbeer
        ComprehensiveTasbeehItem("h1", "الْحَمْدُ لِلَّهِ", "Alhamdulillah", 33, TasbeehPresetCategory.TAHMEED_TAKBEER, "أفضل الدعاء والثناء", "Best praise filling the scales"),
        ComprehensiveTasbeehItem("h2", "اللَّهُ أَكْبَرُ", "Allahu Akbar", 33, TasbeehPresetCategory.TAHMEED_TAKBEER, "تكبير وتعظيم لله سبحانه", "Declaring Allah's greatness"),
        ComprehensiveTasbeehItem("h3", "الْحَمْدُ لِلَّهِ حَمْدًا كَثِيرًا طَيِّبًا مُبَارَكًا فِيهِ", "Alhamdulillahi hamdan katheeran...", 10, TasbeehPresetCategory.TAHMEED_TAKBEER, "ابتدرها بضعة وثلاثون ملكاً", "Thirty+ angels hasten to write it"),
        ComprehensiveTasbeehItem("h4", "اللَّهُ أَكْبَرُ كَبِيرًا وَالْحَمْدُ لِلَّهِ كَثِيرًا وَسُبْحَانَ اللَّهِ بُكْرَةً وَأَصِيلًا", "Allahu Akbaru kabeera...", 10, TasbeehPresetCategory.TAHMEED_TAKBEER, "فُتحت لها أبواب السماء", "Sky gates opened for this phrase"),

        // Istighfar & Tawbah
        ComprehensiveTasbeehItem("i1", "أَسْتَغْفِرُ اللَّهَ", "Astaghfirullah", 100, TasbeehPresetCategory.ISTIGHFAR_TAWBAH, "ممحاة الذنوب وجالبة للرزق", "Erase sins and bring barakah"),
        ComprehensiveTasbeehItem("i2", "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ", "Astaghfirullah wa atoobu ilayh", 100, TasbeehPresetCategory.ISTIGHFAR_TAWBAH, "كان النبي ﷺ يقولها في اليوم أكثر من 70 مرة", "Prophet ﷺ said it 70+ times daily"),
        ComprehensiveTasbeehItem("i3", "رَبِّ اغْفِرْ لِي وَتُبْ عَلَيَّ إِنَّكَ أَنْتَ التَّوَّابُ الرَّحِيمُ", "Rabbi-ghfir li wa tub 'alayya...", 100, TasbeehPresetCategory.ISTIGHFAR_TAWBAH, "دعاء التوبة النبوي في المجلس", "Prophetic prayer during gatherings"),
        ComprehensiveTasbeehItem("i4", "أَسْتَغْفِرُ اللَّهَ الَّذِي لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ", "Astaghfirullah alladhi la ilaha illa Huwa...", 3, TasbeehPresetCategory.ISTIGHFAR_TAWBAH, "غُفرت ذنوبه وإن كان فرّ من الزحف", "Forgives major offenses"),
        ComprehensiveTasbeehItem("i5", "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ خَلَقْتَنِي وَأَنَا عَبْدُكَ...", "Sayyidul Istighfar", 1, TasbeehPresetCategory.ISTIGHFAR_TAWBAH, "سيد الاستغفار من قاله موقناً فمات دخل الجنة", "Chief of repentance prayer"),

        // Hawqala & Tawheed
        ComprehensiveTasbeehItem("w1", "لَا إِلَهَ إِلَّا اللَّهُ", "La ilaha illa Allah", 100, TasbeehPresetCategory.HAWQALA_TAWHEED, "أفضل ما قاله النبيون", "Best word spoken by all prophets"),
        ComprehensiveTasbeehItem("w2", "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ", "La hawla wa la quwwata illa billah", 100, TasbeehPresetCategory.HAWQALA_TAWHEED, "كنز من كنوز الجنة ودواء لـ 99 داء", "Treasure of Paradise"),
        ComprehensiveTasbeehItem("w3", "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ", "La ilaha illallah wahdahu la shareeka lah...", 100, TasbeehPresetCategory.HAWQALA_TAWHEED, "حرز من الشيطان وعتق رقاب", "Protection from Shaytan"),
        ComprehensiveTasbeehItem("w4", "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ", "Ya Hayyu Ya Qayyoom bi rahmatika astagheeth", 33, TasbeehPresetCategory.HAWQALA_TAWHEED, "دعاء تفريج الشدائد وإصلاح الشأن", "Call for relief and guidance"),

        // Salawat
        ComprehensiveTasbeehItem("s1", "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ", "Allahumma salli ala Muhammad...", 100, TasbeehPresetCategory.SALAWAT, "من صلى عليّ صلاة صلى الله عليه بها عشراً", "Allah blesses 10x for each prayer"),
        ComprehensiveTasbeehItem("s2", "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ", "Allahumma salli wa sallim ala Nabiyyina...", 10, TasbeehPresetCategory.SALAWAT, "أدركته شفاعة النبي يوم القيامة", "Attains Prophet's intercession"),
        ComprehensiveTasbeehItem("s3", "اللَّهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ عَدَدَ مَا ذَكَرَهُ الذَّاكِرُونَ", "Allahumma salli ala sayyidina Muhammad...", 33, TasbeehPresetCategory.SALAWAT, "صلاة مضاعفة على الحبيب المصطفى", "Multiplied blessings on the Prophet"),

        // Duaas & Relief
        ComprehensiveTasbeehItem("d1", "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ", "La ilaha illa Anta subhanaka...", 40, TasbeehPresetCategory.DUAA_RELIEF, "دعوة ذي النون لا يدعو بها مكروب إلا فرّج الله عنه", "Supplication of Yunus (AS) in distress"),
        ComprehensiveTasbeehItem("d2", "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ", "Hasbunallahu wa ni'mal wakeel", 100, TasbeehPresetCategory.DUAA_RELIEF, "قالها إبراهيم والنبي ﷺ عند الشدائد", "Spoken by Ibrahim and Muhammad ﷺ"),
        ComprehensiveTasbeehItem("d3", "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ", "Rabbana atina fid-dunya hasanah...", 10, TasbeehPresetCategory.DUAA_RELIEF, "أكثر دعاء كان يدعو به النبي ﷺ", "Most frequent prayer of the Prophet"),
        ComprehensiveTasbeehItem("d4", "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ", "Allahumma inni as'alukal 'afwa wal 'afiyah", 3, TasbeehPresetCategory.DUAA_RELIEF, "دعاء العافية التامة", "Prayer for complete wellness"),
        ComprehensiveTasbeehItem("d5", "اللَّهُمَّ اكْفِنِي بِحَلَالِكَ عَنْ حَرَامِكَ وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ", "Allahummak-fini bihalalika...", 10, TasbeehPresetCategory.DUAA_RELIEF, "دعاء سداد الدين والغنى عن الخلق", "Prayer for debt relief and sufficiency"),
        ComprehensiveTasbeehItem("d6", "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ وَالْعَجْزِ وَالْكَسَلِ", "Allahumma inni a'oodhu bika minal hammi...", 10, TasbeehPresetCategory.DUAA_RELIEF, "الاستعاذة النبوية من الهموم والديون", "Prophetic refuge against distress")
    )
}
