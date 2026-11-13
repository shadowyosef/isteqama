package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TasbeehScheduleRepository {
    private val _schedules = MutableStateFlow<List<TasbeehScheduleModel>>(seedDefaults())
    val schedules: StateFlow<List<TasbeehScheduleModel>> = _schedules.asStateFlow()

    private fun seedDefaults(): List<TasbeehScheduleModel> {
        val allJoined = AuthenticTasbeehCatalog.allPhrases.joinToString(";") {
            "${it.phraseAr}~${it.phraseEn}~${it.target}"
        }
        return listOf(
            TasbeehScheduleModel(
                id = "tasbeeh_default_all_random",
                phraseAr = "سبحان الله والحمد لله ولا إله إلا الله والله أكبر (كل التسابيح عشوائياً)",
                phraseEn = "All Tasbeehs (Random Every 30m)",
                targetCount = 33,
                hour = 9,
                minute = 0,
                repeatIntervalMins = 30,
                repeatIntervalSeconds = 1800,
                startFromNow = true,
                timesPerDay = 30,
                durationSeconds = 10,
                deliveryStyle = TasbeehDeliveryStyle.TOAST_POPUP,
                themeColor = TasbeehThemeColor.EMERALD,
                cardShape = TasbeehCardShape.PILL,
                isEnabled = true,
                quietHoursEnabled = true,
                quietHoursStart = 23,
                quietHoursEnd = 8,
                phrasesJson = allJoined,
                isRandomOrder = true
            )
        )
    }

    fun addSchedule(item: TasbeehScheduleModel) {
        _schedules.value = listOf(item) + _schedules.value
    }

    fun updateSchedule(item: TasbeehScheduleModel) {
        _schedules.value = _schedules.value.map { if (it.id == item.id) item else it }
    }

    fun toggleSchedule(id: String) {
        _schedules.value = _schedules.value.map {
            if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    fun setScheduleEnabled(id: String, enabled: Boolean) {
        _schedules.value = _schedules.value.map {
            if (it.id == id) it.copy(isEnabled = enabled) else it
        }
    }

    fun recordTriggered(id: String, timestamp: Long = System.currentTimeMillis()) {
        _schedules.value = _schedules.value.map {
            if (it.id == id) {
                val size = it.getPhrases().size.coerceAtLeast(1)
                val nextIdx = if (it.isRandomOrder) kotlin.random.Random.nextInt(size) else (it.currentPhraseIndex + 1) % size
                it.copy(lastTriggeredMillis = timestamp, currentPhraseIndex = nextIdx)
            } else it
        }
    }

    fun deleteSchedule(id: String) {
        _schedules.value = _schedules.value.filterNot { it.id == id }
    }
}
