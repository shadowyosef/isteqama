package com.example.data

import com.example.model.TasbeehItem
import com.example.model.TasbeehSet

object AuthenticTasbeehCatalog {
    val allPhrases = listOf(
        TasbeehItem("t1", "سُبْحَانَ اللَّهِ", "SubhanAllah (Glory be to Allah)", 33),
        TasbeehItem("t2", "الْحَمْدُ لِلَّهِ", "Alhamdulillah (All praise is due to Allah)", 33),
        TasbeehItem("t3", "لَا إِلَٰهَ إِلَّا اللَّهُ", "La ilaha illa Allah (None has the right to be worshipped but Allah)", 33),
        TasbeehItem("t4", "اللَّهُ أَكْبَرُ", "Allahu Akbar (Allah is the Greatest)", 34),
        TasbeehItem("t5", "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", "SubhanAllahi wa bihamdih", 100),
        TasbeehItem("t6", "سُبْحَانَ اللَّهِ الْعَظِيمِ وَبِحَمْدِهِ", "SubhanAllahil Azeem wa bihamdih", 100),
        TasbeehItem("t7", "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ وَأَتُوبُ إِلَيْهِ", "Astaghfirullah al-Azeem wa atoobu ilayh", 100),
        TasbeehItem("t8", "أَسْتَغْفِرُ اللَّهَ الَّذِي لَا إِلَهَ إِلَّا هُوَ الْحَيَّ الْقَيُّومَ وَأَتُوبُ إِلَيْهِ", "I seek forgiveness from the Ever-Living", 3),
        TasbeehItem("t9", "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ", "There is no power nor strength except with Allah", 100),
        TasbeehItem("t10", "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ", "Blessings and peace upon Prophet Muhammad", 100),
        TasbeehItem("t11", "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ", "Salawat Ibrahimiyyah", 10),
        TasbeehItem("t12", "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ", "Tahlil at-Tawheed", 100),
        TasbeehItem("t13", "سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَلَا إِلَهَ إِلَّا اللَّهُ، وَاللَّهُ أَكْبَرُ (الباقيات الصالحات)", "The Everlasting Good Deeds", 100),
        TasbeehItem("t14", "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ", "Dua of Dhu an-Nun (Relief from distress)", 40),
        TasbeehItem("t15", "رَبِّ اغْفِرْ لِي وَتُبْ عَلَيَّ إِنَّكَ أَنْتَ التَّوَّابُ الرَّحِيمُ", "Lord forgive me and accept my repentance", 100),
        TasbeehItem("t16", "يَا ذَا الْجَلَالِ وَالإِكْرَامِ", "O Owner of Majesty and Honor", 10),
        TasbeehItem("t17", "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ", "O Ever Living, O Sustainer by Your mercy I seek help", 33),
        TasbeehItem("t18", "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ", "Allah is sufficient for us and He is the best Disposer", 100),
        TasbeehItem("t19", "رَبِّ إِنِّي ظَلَمْتُ نَفْسِي فَاغْفِرْ لِي", "My Lord I have wronged myself so forgive me", 33),
        TasbeehItem("t20", "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي", "O Allah You are forgiving and love forgiveness", 100),
        TasbeehItem("t21", "سُبُّوحٌ قُدُّوسٌ رَبُّ الْمَلَائِكَةِ وَالرُّوحِ", "Subboohun Quddoos", 10),
        TasbeehItem("t22", "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ", "Help me remember You, thank You and worship well", 10),
        TasbeehItem("t23", "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ", "Best Dua for this world and hereafter", 20),
        TasbeehItem("t24", "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي", "My Lord expand my chest and ease my task", 33),
        TasbeehItem("t25", "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ لِذَنْبِي وَلِلْمُؤْمِنِينَ وَالْمُؤْمِنَاتِ", "Istighfar for all believers", 70),
        TasbeehItem("t26", "رَبِّ زِدْنِي عِلْمًا", "My Lord increase me in knowledge", 33),
        TasbeehItem("t27", "اللَّهُمَّ اكْفِنِي بِحَلَالِكَ عَنْ حَرَامِكَ وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ", "Freedom from debt and self-sufficiency", 33),
        TasbeehItem("t28", "رَبِّ إِنِّي لِمَا أَنْزَلْتَ إِلَيَّ مِنْ خَيْرٍ فَقِيرٌ", "Lord I am in need of whatever good You bestow", 33),
        TasbeehItem("t29", "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ (كنز من كنوز الجنة)", "Treasure of Paradise", 100),
        TasbeehItem("t30", "اللَّهُمَّ حَاسِبْنِي حِسَابًا يَسِيرًا", "O Allah grant me an easy accounting", 10),
        TasbeehItem("t31", "سُبْحَانَ اللَّهِ عَدَدَ مَا خَلَقَ فِي السَّمَاءِ وَالأَرْضِ", "SubhanAllah by number of creations", 33),
        TasbeehItem("t32", "الْحَمْدُ لِلَّهِ حَمْدًا كَثِيرًا طَيِّبًا مُبَارَكًا فِيهِ", "Praise be to Allah, abundant and blessed", 33)
    )

    val defaultSets = listOf(
        TasbeehSet("set_prayer", "تسبيح ما بعد الصلاة (33x3)", "After Prayer Set", listOf(allPhrases[0], allPhrases[1], allPhrases[3])),
        TasbeehSet("set_istighfar", "سيت الاستغفار والتوبة المائة", "Istighfar 100x Set", listOf(allPhrases[6], allPhrases[7], allPhrases[14])),
        TasbeehSet("set_salawat", "سيت الصلاة على النبي ﷺ والكنوز", "Salawat & Treasures", listOf(allPhrases[9], allPhrases[10], allPhrases[8]))
    )
}
