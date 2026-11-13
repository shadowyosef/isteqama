package com.example.util

import com.example.model.ZakatCalculationResult

object ZakatCalculatorHelper {
    // Standard Shar'i Nisab: 85g gold or 595g silver
    const val GOLD_NISAB_GRAMS = 85.0
    const val SILVER_NISAB_GRAMS = 595.0
    const val ZAKAT_PERCENTAGE = 0.025 // 2.5%

    fun calculateZakat(
        cashAmount: Double,
        goldGrams24: Double,
        goldGrams21: Double,
        silverGrams: Double,
        tradeGoodsValue: Double,
        goldPricePerGram24: Double = 60.0,
        silverPricePerGram: Double = 0.85
    ): ZakatCalculationResult {
        val goldValue = (goldGrams24 * goldPricePerGram24) + (goldGrams21 * (goldPricePerGram24 * 21.0 / 24.0))
        val silverValue = silverGrams * silverPricePerGram
        val totalWealth = cashAmount + goldValue + silverValue + tradeGoodsValue

        val nisabValue = GOLD_NISAB_GRAMS * goldPricePerGram24
        val isEligible = totalWealth >= nisabValue
        val due = if (isEligible) totalWealth * ZAKAT_PERCENTAGE else 0.0

        return ZakatCalculationResult(
            totalWealth = totalWealth,
            nisabValue = nisabValue,
            isZakatEligible = isEligible,
            zakatAmountDue = due
        )
    }
}
