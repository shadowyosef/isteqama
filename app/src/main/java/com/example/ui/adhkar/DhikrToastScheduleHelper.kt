package com.example.ui.adhkar

import android.content.Context
import com.example.data.AppPreferencesManager
import com.example.data.TasbeehScheduleRepository
import com.example.model.*
import com.example.service.FloatingTasbeehToastManager

object DhikrToastScheduleHelper {

    fun createScheduleFromDhikrItem(item: DhikrItem): TasbeehScheduleModel {
        val phrase = TasbeehPhraseItem(
            phraseAr = item.textAr,
            phraseEn = item.textEn.ifBlank { item.textAr },
            targetCount = item.targetCount
        )
        return TasbeehScheduleModel(
            phraseAr = item.textAr,
            phraseEn = item.textEn.ifBlank { item.textAr },
            targetCount = item.targetCount,
            repeatIntervalMins = 60,
            repeatIntervalSeconds = 3600,
            deliveryStyle = TasbeehDeliveryStyle.TOAST_POPUP,
            themeColor = TasbeehThemeColor.EMERALD,
            cardShape = TasbeehCardShape.PILL,
            phrasesJson = phrase.serialize(),
            isRandomOrder = false,
            countOnClickWithDismiss = true
        )
    }

    fun createScheduleFromDhikrList(list: CustomDhikrList): TasbeehScheduleModel {
        val phrases = if (list.items.isNotEmpty()) {
            list.items.map { TasbeehPhraseItem(it.textAr, it.textEn.ifBlank { it.textAr }, it.targetCount) }
        } else {
            listOf(TasbeehPhraseItem(list.titleAr, list.titleEn, list.totalTarget.coerceAtLeast(33)))
        }
        val first = phrases.first()
        return TasbeehScheduleModel(
            phraseAr = first.phraseAr,
            phraseEn = first.phraseEn,
            targetCount = first.targetCount,
            hour = list.scheduledHour,
            minute = list.scheduledMinute,
            repeatIntervalMins = 60,
            repeatIntervalSeconds = 3600,
            startHour = list.scheduledHour,
            startMinute = list.scheduledMinute,
            deliveryStyle = TasbeehDeliveryStyle.TOAST_POPUP,
            themeColor = TasbeehThemeColor.EMERALD,
            cardShape = TasbeehCardShape.PILL,
            phrasesJson = phrases.joinToString(";") { it.serialize() },
            isRandomOrder = false,
            countOnClickWithDismiss = true
        )
    }

    fun triggerToastNow(context: Context, item: DhikrItem) {
        val model = createScheduleFromDhikrItem(item)
        FloatingTasbeehToastManager.triggerTasbeehAlert(context, model)
    }

    fun triggerToastNow(context: Context, list: CustomDhikrList) {
        val model = createScheduleFromDhikrList(list)
        FloatingTasbeehToastManager.triggerTasbeehAlert(context, model)
    }

    fun triggerToastNow(context: Context, routineTitle: String, phrases: List<TasbeehPhraseItem>) {
        val primary = phrases.firstOrNull() ?: TasbeehPhraseItem("سُبْحَانَ اللَّهِ", "Subhan Allah", 33)
        val model = TasbeehScheduleModel(
            phraseAr = primary.phraseAr,
            phraseEn = primary.phraseEn,
            targetCount = primary.targetCount,
            deliveryStyle = TasbeehDeliveryStyle.TOAST_POPUP,
            themeColor = TasbeehThemeColor.EMERALD,
            cardShape = TasbeehCardShape.PILL,
            phrasesJson = phrases.joinToString(";") { it.serialize() },
            isRandomOrder = false,
            countOnClickWithDismiss = true
        )
        FloatingTasbeehToastManager.triggerTasbeehAlert(context, model)
    }
}
