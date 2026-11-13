package com.example.ui.tasbeeh

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppPreferencesManager
import com.example.model.*
import com.example.ui.common.WizardStepFooter
import com.example.ui.common.WizardStepProgressHeader

@Composable
fun CreateTasbeehScheduleDialog(
    initialItem: TasbeehScheduleModel? = null,
    onDismiss: () -> Unit,
    onSave: (TasbeehScheduleModel) -> Unit
) {
    val isAr by AppPreferencesManager.isArabic.collectAsState()
    val idKey = initialItem?.id ?: "new"
    var step by remember(idKey) { mutableIntStateOf(0) }
    var phrases by remember(idKey) { mutableStateOf(initialItem?.getPhrases() ?: listOf(TasbeehPhraseItem("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", "Subhan Allah", 33))) }
    var isRandomOrder by remember(idKey) { mutableStateOf(initialItem?.isRandomOrder ?: false) }
    val initSecs = initialItem?.getEffectiveIntervalSeconds() ?: 1800
    var isSpecificTimes by remember(idKey) { mutableStateOf(initialItem != null && initSecs == 0) }
    var intervalValue by remember(idKey) { mutableIntStateOf(if (initSecs == 0) 30 else if (initSecs < 60) initSecs else if (initSecs % 3600 == 0) initSecs / 3600 else initSecs / 60) }
    var repeatUnit by remember(idKey) { mutableStateOf(if (initSecs in 1..59) RepeatUnit.SECONDS else if (initSecs >= 3600 && initSecs % 3600 == 0) RepeatUnit.HOURS else RepeatUnit.MINUTES) }
    var maxDailyRepeats by remember(idKey) { mutableIntStateOf(initialItem?.maxDailyRepeats ?: 0) }
    var startFromNow by remember(idKey) { mutableStateOf(initialItem?.startFromNow ?: true) }
    var startHour by remember(idKey) { mutableIntStateOf(initialItem?.startHour ?: 9) }
    var startMinute by remember(idKey) { mutableIntStateOf(initialItem?.startMinute ?: 0) }
    var durationSecs by remember(idKey) { mutableIntStateOf(initialItem?.durationSeconds ?: 10) }
    var deliveryStyle by remember(idKey) { mutableStateOf(initialItem?.deliveryStyle ?: TasbeehDeliveryStyle.TOAST_POPUP) }
    var themeColor by remember(idKey) { mutableStateOf(initialItem?.themeColor ?: TasbeehThemeColor.EMERALD) }
    var cardShape by remember(idKey) { mutableStateOf(initialItem?.cardShape ?: TasbeehCardShape.PILL) }
    var quietHours by remember(idKey) { mutableStateOf(initialItem?.quietHoursEnabled ?: true) }
    var quietStart by remember(idKey) { mutableIntStateOf(initialItem?.quietHoursStart ?: 23) }
    var quietEnd by remember(idKey) { mutableIntStateOf(initialItem?.quietHoursEnd ?: 8) }
    var specificTimes by remember(idKey) { mutableStateOf(initialItem?.getSpecificTimesList() ?: listOf("09:00")) }
    var countOnClick by remember(idKey) { mutableStateOf(initialItem?.countOnClickWithDismiss ?: true) }
    var whisperVoice by remember(idKey) { mutableStateOf(initialItem?.whisperVoiceEnabled ?: false) }

    val titles = if (isAr) listOf("١. اختيار التسابيح", "٢. التكرار ووقت الهدوء", "٣. الشكل والظهور")
    else listOf("1. Pick Phrases", "2. Timing & Quiet Hours", "3. Style & Delivery")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth().heightIn(max = 660.dp).testTag("tasbeeh_schedule_dialog"),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (0..2).forEach { idx ->
                        FilterChip(selected = step == idx, onClick = { step = idx }, label = { Text("${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
                    }
                }
                WizardStepProgressHeader(currentStep = step, totalSteps = 3, stepTitle = titles[step], isArabic = isAr)

                TasbeehWizardStepContent(
                    step = step, isAr = isAr, phrases = phrases, onPhrasesChange = { phrases = it },
                    isRandomOrder = isRandomOrder, onRandomChange = { isRandomOrder = it },
                    isSpecificTimes = isSpecificTimes, onModeChange = { isSpecificTimes = it },
                    intervalValue = intervalValue, onIntervalValueChange = { intervalValue = it },
                    repeatUnit = repeatUnit, onUnitChange = { repeatUnit = it },
                    maxDailyRepeats = maxDailyRepeats, onMaxRepeatsChange = { maxDailyRepeats = it },
                    startFromNow = startFromNow, onStartFromNowChange = { startFromNow = it },
                    startHour = startHour, startMinute = startMinute, onStartTimeChange = { h, m -> startHour = h; startMinute = m },
                    specificTimes = specificTimes, onSpecificTimesChange = { specificTimes = it },
                    durationSecs = durationSecs, onDurationChange = { durationSecs = it },
                    quietHours = quietHours, quietStart = quietStart, quietEnd = quietEnd,
                    onQuietToggle = { quietHours = it }, onQuietRangeChange = { s, e -> quietStart = s; quietEnd = e },
                    deliveryStyle = deliveryStyle, onDeliveryStyleChange = { deliveryStyle = it },
                    themeColor = themeColor, onThemeColorChange = { themeColor = it },
                    cardShape = cardShape, onCardShapeChange = { cardShape = it },
                    countOnClick = countOnClick, onCountOnClickChange = { countOnClick = it },
                    whisperVoice = whisperVoice, onWhisperVoiceChange = { whisperVoice = it }
                )

                WizardStepFooter(
                    currentStep = step, totalSteps = 3, isArabic = isAr,
                    onCancelOrBack = { if (step > 0) step-- else onDismiss() },
                    onNext = { if (step < 2) step++ },
                    onFinish = {
                        val totalSecs = if (isSpecificTimes) 0 else when (repeatUnit) {
                            RepeatUnit.SECONDS -> intervalValue; RepeatUnit.MINUTES -> intervalValue * 60; RepeatUnit.HOURS -> intervalValue * 3600
                        }
                        val firstH = specificTimes.firstOrNull()?.split(":")?.getOrNull(0)?.toIntOrNull() ?: 9
                        val firstM = specificTimes.firstOrNull()?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0
                        val primary = phrases.firstOrNull() ?: TasbeehPhraseItem("سُبْحَانَ اللَّهِ", "Subhan Allah", 33)
                        onSave((initialItem ?: TasbeehScheduleModel(phraseAr = primary.phraseAr, phraseEn = primary.phraseEn)).copy(
                            phraseAr = primary.phraseAr, phraseEn = primary.phraseEn, targetCount = primary.targetCount,
                            repeatIntervalMins = totalSecs / 60, repeatIntervalSeconds = totalSecs,
                            startFromNow = startFromNow, startHour = startHour, startMinute = startMinute,
                            createdAtMillis = initialItem?.createdAtMillis ?: System.currentTimeMillis(),
                            lastTriggeredMillis = System.currentTimeMillis(), durationSeconds = durationSecs,
                            deliveryStyle = deliveryStyle, themeColor = themeColor, cardShape = cardShape,
                            quietHoursEnabled = quietHours, quietHoursStart = quietStart, quietHoursEnd = quietEnd,
                            specificTimesJson = if (isSpecificTimes) specificTimes.joinToString(",") else "",
                            hour = if (isSpecificTimes) firstH else startHour, minute = if (isSpecificTimes) firstM else startMinute,
                            phrasesJson = phrases.joinToString(";") { it.serialize() }, isRandomOrder = isRandomOrder,
                            maxDailyRepeats = maxDailyRepeats, countOnClickWithDismiss = countOnClick, whisperVoiceEnabled = whisperVoice
                        ))
                    },
                    finishLabel = if (isAr) "حفظ التذكير" else "Save",
                    finishTestTag = "save_tasbeeh_schedule_btn"
                )
            }
        }
    }
}
