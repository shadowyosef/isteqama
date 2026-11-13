package com.example.ui.tasbeeh

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.common.QuietHoursEditorRow
import com.example.ui.theme.OrnateIslamicCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TasbeehWizardStepContent(
    step: Int,
    isAr: Boolean,
    phrases: List<TasbeehPhraseItem>,
    onPhrasesChange: (List<TasbeehPhraseItem>) -> Unit,
    isRandomOrder: Boolean,
    onRandomChange: (Boolean) -> Unit,
    isSpecificTimes: Boolean,
    onModeChange: (Boolean) -> Unit,
    intervalValue: Int,
    onIntervalValueChange: (Int) -> Unit,
    repeatUnit: RepeatUnit,
    onUnitChange: (RepeatUnit) -> Unit,
    maxDailyRepeats: Int,
    onMaxRepeatsChange: (Int) -> Unit,
    startFromNow: Boolean,
    onStartFromNowChange: (Boolean) -> Unit,
    startHour: Int,
    startMinute: Int,
    onStartTimeChange: (Int, Int) -> Unit,
    specificTimes: List<String>,
    onSpecificTimesChange: (List<String>) -> Unit,
    durationSecs: Int,
    onDurationChange: (Int) -> Unit,
    quietHours: Boolean,
    quietStart: Int,
    quietEnd: Int,
    onQuietToggle: (Boolean) -> Unit,
    onQuietRangeChange: (Int, Int) -> Unit,
    deliveryStyle: TasbeehDeliveryStyle,
    onDeliveryStyleChange: (TasbeehDeliveryStyle) -> Unit,
    themeColor: TasbeehThemeColor,
    onThemeColorChange: (TasbeehThemeColor) -> Unit,
    cardShape: TasbeehCardShape,
    onCardShapeChange: (TasbeehCardShape) -> Unit,
    countOnClick: Boolean,
    onCountOnClickChange: (Boolean) -> Unit,
    whisperVoice: Boolean,
    onWhisperVoiceChange: (Boolean) -> Unit
) {
    when (step) {
        0 -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OrnateIslamicCard {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        if (isAr) "اختر التسابيح من البنك أو أضف ذكراً جديداً وحدد عشوائية الظهور:" else "Pick phrases from bank or add custom and set order:",
                        fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            TasbeehMultiPhraseSection(phrases = phrases, isRandom = isRandomOrder, onPhrasesChange = onPhrasesChange, onRandomChange = onRandomChange, isArabic = isAr)
        }
        1 -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TasbeehIntervalPickerSection(
                isSpecificTimes = isSpecificTimes, onModeChange = onModeChange,
                intervalValue = intervalValue, onValueChange = onIntervalValueChange,
                unit = repeatUnit, onUnitChange = onUnitChange,
                startFromNow = startFromNow, onStartFromNowChange = onStartFromNowChange,
                startHour = startHour, startMinute = startMinute, onStartTimeChange = onStartTimeChange,
                isArabic = isAr, maxDailyRepeats = maxDailyRepeats, onMaxRepeatsChange = onMaxRepeatsChange
            )
            if (isSpecificTimes) SpecificTimesPickerSection(times = specificTimes, onTimesChange = onSpecificTimesChange, isArabic = isAr)
            TasbeehDurationPickerRow(selectedSeconds = durationSecs, isArabic = isAr, onSelect = onDurationChange)
            QuietHoursEditorRow(enabled = quietHours, startHour = quietStart, endHour = quietEnd, isArabic = isAr, onToggle = onQuietToggle, onRangeChange = onQuietRangeChange)
        }
        else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(if (isAr) "طريقة عرض التذكير:" else "Popup Delivery Style:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    TasbeehDeliveryStyle.TOAST_POPUP to if (isAr) "توست عائم" else "Float Toast",
                    TasbeehDeliveryStyle.NOTIFICATION to if (isAr) "إشعار" else "Notification",
                    TasbeehDeliveryStyle.BOTH to if (isAr) "كلاهما" else "Both"
                ).forEach { (st, lbl) -> FilterChip(selected = deliveryStyle == st, onClick = { onDeliveryStyleChange(st) }, label = { Text(lbl, fontSize = 11.sp) }) }
            }
            Text(if (isAr) "اللون والشكل:" else "Color & Shape:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            TasbeehColorPickerRow(selected = themeColor, onSelect = onThemeColorChange)
            TasbeehShapePickerRow(selected = cardShape, isArabic = isAr, onSelect = onCardShapeChange)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isAr) "التسبيح بالضغط على التنبيه" else "Count on tap", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Switch(checked = countOnClick, onCheckedChange = onCountOnClickChange)
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (isAr) "نطق صوتي هادئ (همس)" else "Calm voice whisper (TTS)", fontSize = 12.sp)
                Switch(checked = whisperVoice, onCheckedChange = onWhisperVoiceChange)
            }
        }
    }
}
