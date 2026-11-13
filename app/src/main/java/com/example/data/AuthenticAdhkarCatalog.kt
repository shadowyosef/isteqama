package com.example.data

import com.example.model.DhikrItem

object AuthenticAdhkarCatalog {
    val morningAdhkar: List<DhikrItem> = MorningAdhkarData.items
    val eveningAdhkar: List<DhikrItem> = EveningAdhkarData.items
    val sleepAdhkar: List<DhikrItem> = SleepAdhkarData.items
    val prayerAdhkar: List<DhikrItem> = PrayerAdhkarData.items

    val allAdhkar: List<DhikrItem> = morningAdhkar + eveningAdhkar + sleepAdhkar + prayerAdhkar
}
