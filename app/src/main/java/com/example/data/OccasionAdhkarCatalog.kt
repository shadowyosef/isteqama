package com.example.data

import com.example.model.OccasionAdhkarItem
import com.example.model.OccasionCategory

object OccasionAdhkarCatalog {
    val items: List<OccasionAdhkarItem> = listOf(
        // Daily Life
        OccasionAdhkarItem(
            "occ_1", "دعاء كفارة المجلس", "Expiation of Gathering",
            "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ، أَشْهَدُ أَنْ لا إِلَهَ إِلا أَنْتَ، أَسْتَغْفِرُكَ وَأَتُوبُ إِلَيْكَ.",
            "Subhanakallahumma wa bihamdika, ashhadu an la ilaha illa Anta, astaghfiruka wa atoobu ilayk.",
            "مغفرة لما كان في ذلك المجلس من لغو", OccasionCategory.DAILY_LIFE
        ),
        OccasionAdhkarItem(
            "occ_2", "دعاء الخروج من المنزل", "Leaving the House",
            "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلا حَوْلَ وَلا قُوَّةَ إِلا بِاللَّهِ.",
            "Bismillahi, tawakkaltu 'alallahi, wa la hawla wa la quwwata illa billah.",
            "يُقال له: هُديتَ وكُفيتَ ووُقيتَ، وتنحّى عنه الشيطان", OccasionCategory.DAILY_LIFE
        ),
        OccasionAdhkarItem(
            "occ_3", "دعاء دخول المنزل", "Entering the House",
            "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى رَبِّنَا تَوَكَّلْنَا.",
            "Bismillahi walajna, wa bismillahi kharajna, wa 'ala Rabbina tawakkalna.",
            "يمنع الشيطان من المبيت في البيت", OccasionCategory.DAILY_LIFE
        ),
        // Travel
        OccasionAdhkarItem(
            "occ_4", "دعاء ركوب الدابة والسيارة", "Riding a Vehicle",
            "بِسْمِ اللَّهِ، الْحَمْدُ لِلَّهِ، سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ، وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ.",
            "Subhanalladhi sakhkhara lana hadha wa ma kunna lahu muqrineen, wa inna ila Rabbina lamunqaliboon.",
            "حفظ المسافر وبركة الرحلة", OccasionCategory.TRAVEL
        ),
        OccasionAdhkarItem(
            "occ_5", "دعاء السفر الكامل", "Travel Prayer",
            "اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَذَا الْبِرَّ وَالتَّقْوَى، وَمِنَ الْعَمَلِ مَا تَرْضَى، اللَّهُمَّ هَوِّنْ عَلَيْنَا سَفَرَنَا هَذَا وَاطْوِ عَنَّا بُعْدَهُ.",
            "Allahumma inna nas'aluka fi safarina hadhal-birra wat-taqwa...",
            "صرف وعثاء السفر وكآبة المنظر", OccasionCategory.TRAVEL
        ),
        // Mosque
        OccasionAdhkarItem(
            "occ_6", "دعاء دخول المسجد", "Entering the Mosque",
            "بِسْمِ اللَّهِ، وَالصَّلاةُ وَالسَّلامُ عَلَى رَسُولِ اللَّهِ، اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ.",
            "Bismillahi wassalatu wassalamu 'ala Rasulillah, Allahummaftah li abwaba rahmatik.",
            "سؤال فتح أبواب الرحمة", OccasionCategory.MOSQUE
        ),
        OccasionAdhkarItem(
            "occ_7", "دعاء الخروج من المسجد", "Leaving the Mosque",
            "بِسْمِ اللَّهِ، وَالصَّلاةُ وَالسَّلامُ عَلَى رَسُولِ اللَّهِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ.",
            "Allahumma inni as'aluka min fadlik.",
            "سؤال فضل الله ورزقه الحلال", OccasionCategory.MOSQUE
        ),
        // Weather
        OccasionAdhkarItem(
            "occ_8", "دعاء نزول المطر", "When it Rains",
            "اللَّهُمَّ صَيِّبًا نَافِعًا.",
            "Allahumma sayyiban nafi'an.",
            "وقت استجابة الدعاء وبركة الغيث", OccasionCategory.WEATHER
        ),
        OccasionAdhkarItem(
            "occ_9", "دعاء سماع الرعد", "Hearing Thunder",
            "سُبْحَانَ الَّذِي يُسَبِّحُ الرَّعْدُ بِحَمْدِهِ وَالْمَلائِكَةُ مِنْ خِيفَتِهِ.",
            "Subhanalladhi yusabbihur-ra'du bihamdihi wal-mala'ikatu min kheefatih.",
            "تعظيم الله عند سماع الرعد", OccasionCategory.WEATHER
        ),
        OccasionAdhkarItem(
            "occ_10", "دعاء هبوب الريح", "Strong Wind",
            "اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَهَا، وَخَيْرَ مَا فِيهَا، وَخَيْرَ مَا أُرْسِلَتْ بِهِ، وَأَعُوذُ بِكَ مِنْ شَرِّهَا.",
            "Allahumma inni as'aluka khayraha wa khayra ma fiha...",
            "الاستعاذة من عذاب الريح", OccasionCategory.WEATHER
        )
    )
}
