package com.example.model

enum class ZakatAssetType(val labelAr: String, val labelEn: String) {
    CASH("أموال نقدية وسيولة", "Cash & Bank Balance"),
    GOLD_24("ذهب عيار 24 (بالغرام)", "24k Gold (grams)"),
    GOLD_21("ذهب عيار 21 (بالغرام)", "21k Gold (grams)"),
    SILVER("فضة (بالغرام)", "Silver (grams)"),
    TRADE_GOODS("عروض تجارة وبضائع", "Business Merchandise")
}

data class ZakatCalculationResult(
    val totalWealth: Double,
    val nisabValue: Double,
    val isZakatEligible: Boolean,
    val zakatAmountDue: Double
)
