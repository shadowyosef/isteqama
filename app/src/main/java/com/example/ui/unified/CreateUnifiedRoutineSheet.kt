package com.example.ui.unified

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.data.UnifiedRoutineRepository
import com.example.model.*
import com.example.util.AppStrings
import com.example.util.TimeFormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUnifiedRoutineSheet(
    initialRoutine: UnifiedRoutine? = null,
    onDismiss: () -> Unit
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val rKey = initialRoutine?.id ?: "new"
    var titleAr by remember(rKey) { mutableStateOf(initialRoutine?.titleAr ?: "") }
    var category by remember(rKey) { mutableStateOf(initialRoutine?.category ?: RoutineCategory.ADHKAR_SET) }
    var surahNumStr by remember(rKey) { mutableStateOf(initialRoutine?.surahNumber?.toString() ?: "1") }
    var isLockEnabled by remember(rKey) { mutableStateOf(initialRoutine?.isLockActive ?: false) }
    var intervalMinutes by remember(rKey) { mutableIntStateOf(initialRoutine?.repeatIntervalMinutes ?: 0) }
    var startFromNow by remember(rKey) { mutableStateOf(initialRoutine?.startFromNow ?: true) }
    var triggerHour by remember(rKey) { mutableIntStateOf(initialRoutine?.triggerHour ?: 6) }
    var triggerMinute by remember(rKey) { mutableIntStateOf(initialRoutine?.triggerMinute ?: 0) }
    var steps by remember(rKey) {
        mutableStateOf(
            if (initialRoutine != null) {
                com.example.util.RoutineAdhkarResolver.getAdhkarForRoutine(initialRoutine.id, initialRoutine.titleAr)
                    .map { "${it.titleAr}: ${it.textAr}" to it.targetCount.toString() }
            } else listOf("سُبْحَانَ اللَّهِ" to "33")
        )
    }

    ModalBottomSheet(onDismissRequest = onDismiss, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = if (initialRoutine == null) (if (isArabic) "إنشاء روتين أذكار أو ورد جديد" else "Create New Routine") else (if (isArabic) "تعديل الروتين الإيماني" else "Edit Routine"), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(selected = category == RoutineCategory.ADHKAR_SET, onClick = { category = RoutineCategory.ADHKAR_SET }, label = { Text(if (isArabic) "أذكار متسلسلة" else "Adhkar") }, modifier = Modifier.weight(1f))
                FilterChip(selected = category == RoutineCategory.TASBEEH_CHAIN, onClick = { category = RoutineCategory.TASBEEH_CHAIN }, label = { Text(if (isArabic) "تسبيح" else "Tasbeeh") }, modifier = Modifier.weight(1f))
                FilterChip(selected = category == RoutineCategory.QURAN_WARD, onClick = { category = RoutineCategory.QURAN_WARD }, label = { Text(if (isArabic) "ورد قرآن" else "Quran") }, modifier = Modifier.weight(1f))
            }

            OutlinedTextField(value = titleAr, onValueChange = { titleAr = it }, label = { Text(if (isArabic) "عنوان الروتين (مثال: أذكار الصباح)" else "Routine Title") }, modifier = Modifier.fillMaxWidth().testTag("routine_title_input"))

            if (category == RoutineCategory.QURAN_WARD) {
                OutlinedTextField(value = surahNumStr, onValueChange = { surahNumStr = it.filter { c -> c.isDigit() } }, label = { Text(if (isArabic) "رقم السورة في المصحف (1-114)" else "Surah Number (1-114)") }, modifier = Modifier.fillMaxWidth())
            } else {
                Text(if (isArabic) "الأذكار المتسلسلة (تنتقل تلقائياً للذكر التالي):" else "Sequential Dhikrs:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                steps.forEachIndexed { index, (text, countStr) ->
                    RoutineSequenceStepRow(
                        index = index, text = text, countStr = countStr, canDelete = steps.size > 1,
                        onTextChange = { newT -> steps = steps.toMutableList().also { it[index] = newT to countStr } },
                        onCountChange = { newC -> steps = steps.toMutableList().also { it[index] = text to newC } },
                        onDelete = { if (steps.size > 1) steps = steps.toMutableList().also { it.removeAt(index) } }
                    )
                }
                OutlinedButton(
                    onClick = {
                        val nextDhikr = when (steps.size) { 1 -> "الْحَمْدُ لِلَّهِ"; 2 -> "اللَّهُ أَكْبَرُ"; else -> "أَسْتَغْفِرُ اللَّهَ" }
                        steps = steps + (nextDhikr to "33")
                    },
                    modifier = Modifier.fillMaxWidth().testTag("add_routine_dhikr_step_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isArabic) "+ أضف ذكراً آخر للروتين" else "+ Add Another Dhikr", fontSize = 12.sp)
                }
            }

            RoutineScheduleSection(
                intervalMinutes = intervalMinutes,
                onIntervalChange = { intervalMinutes = it },
                startFromNow = startFromNow,
                onStartFromNowChange = { startFromNow = it },
                triggerHour = triggerHour,
                triggerMinute = triggerMinute,
                onTimeChange = { h, m -> triggerHour = h; triggerMinute = m },
                isArabic = isArabic
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(if (isArabic) "🔒 تفعيل قفل الشاشة عند موعد الروتين" else "🔒 Enable Actual Screen Lock", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Switch(checked = isLockEnabled, onCheckedChange = { isLockEnabled = it })
            }

            Button(
                onClick = {
                    val stepItems = steps.mapIndexed { idx, (text, countStr) ->
                        val target = countStr.toIntOrNull()?.coerceAtLeast(1) ?: 33
                        RoutineStepItem(textAr = text.ifBlank { "سُبْحَانَ اللَّهِ" }, targetCount = target, order = idx)
                    }
                    val totalTarget = if (category == RoutineCategory.QURAN_WARD) 7 else stepItems.sumOf { it.targetCount }
                    val schedSummary = if (intervalMinutes > 0) {
                        if (isArabic) "تكرار كل $intervalMinutes دقيقة" else "Every $intervalMinutes min"
                    } else {
                        TimeFormatUtils.format12Hour(triggerHour, triggerMinute, isArabic)
                    }
                    val routine = UnifiedRoutine(
                        id = initialRoutine?.id ?: "r_${System.currentTimeMillis()}",
                        titleAr = titleAr.ifBlank { if (category == RoutineCategory.QURAN_WARD) "ورد القرآن" else steps.firstOrNull()?.first ?: "روتين أذكار" },
                        titleEn = titleAr, category = category, targetCount = totalTarget,
                        surahNumber = surahNumStr.toIntOrNull() ?: 1, endAyah = 7,
                        triggerHour = triggerHour, triggerMinute = triggerMinute,
                        repeatIntervalMinutes = intervalMinutes, startFromNow = startFromNow,
                        createdAtMillis = initialRoutine?.createdAtMillis ?: System.currentTimeMillis(),
                        lastTriggeredMillis = System.currentTimeMillis(),
                        isLockActive = isLockEnabled,
                        stepsJson = RoutineStepItem.serializeList(stepItems),
                        detailsSummary = "الهدف: $totalTarget • $schedSummary"
                    )
                    if (initialRoutine == null) UnifiedRoutineRepository.addRoutine(routine) else UnifiedRoutineRepository.updateRoutine(routine)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_routine_sheet_btn")
            ) {
                Text(AppStrings.save)
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
