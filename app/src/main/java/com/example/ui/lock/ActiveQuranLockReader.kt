package com.example.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranRepository
import com.example.model.LockContentConfig
import com.example.util.AppStrings
import kotlinx.coroutines.delay

@Composable
fun ActiveQuranLockReader(
    config: LockContentConfig,
    targetAmount: Int,
    onFinish: () -> Unit,
    onResetToStart: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val surahIds = remember(config.selectedSurahIds) { config.selectedSurahIds.ifEmpty { listOf(1) } }
    var surahListIndex by remember { mutableIntStateOf(config.currentSurahIndex.coerceIn(0, surahIds.size - 1)) }
    var currentProgressAyah by remember(config.currentProgressAyah) { mutableIntStateOf(config.currentProgressAyah) }
    val currentSurahNum = surahIds.getOrElse(surahListIndex) { 1 }
    val currentSurah = remember(currentSurahNum) { QuranRepository.getSurah(currentSurahNum) }
    val allAyahs = remember(currentSurahNum) { QuranRepository.getAyahsForSurah(currentSurahNum) }

    val startAyah = if (surahListIndex == config.currentSurahIndex) currentProgressAyah.coerceIn(1, allAyahs.size.coerceAtLeast(1)) else 1
    val endAyah = (startAyah + config.ayahsPerSession - 1).coerceIn(startAyah, allAyahs.size.coerceAtLeast(1))
    val sessionAyahs = remember(allAyahs, startAyah, endAyah) {
        if (allAyahs.isEmpty()) emptyList() else allAyahs.filter { it.numberInSurah in startAyah..endAyah }
    }
    var ayahIndex by remember(surahListIndex, currentProgressAyah) { mutableIntStateOf(0) }
    val currentAyah = sessionAyahs.getOrNull(ayahIndex)
    var secondsLeft by remember { mutableIntStateOf(config.readingDurationMinutes * 60) }

    LaunchedEffect(config.isTimedMode) {
        if (config.isTimedMode) {
            while (secondsLeft > 0) { delay(1000); secondsLeft -= 1 }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("سورة ${currentSurah?.nameAr ?: ""} (${surahListIndex + 1}/${surahIds.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("الآية ${currentAyah?.numberInSurah ?: startAyah} (ورد ${sessionAyahs.size} آيات)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = {
                        surahListIndex = 0
                        currentProgressAyah = config.startAyah
                        ayahIndex = 0
                        secondsLeft = config.readingDurationMinutes * 60
                        onResetToStart?.invoke()
                    },
                    modifier = Modifier.height(30.dp).testTag("quran_reset_to_start_btn"),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB74D))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "تصفير من أول آية", modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(AppStrings.get("البدء من أول آية", "Reset"), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                if (config.isTimedMode) {
                    Badge(containerColor = if (secondsLeft == 0) MaterialTheme.colorScheme.primary else Color(0xFFD4AF37)) {
                        Text(String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(2.dp))
                    }
                } else {
                    Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Text("${ayahIndex + 1} / ${sessionAyahs.size.coerceAtLeast(1)}", fontSize = 11.sp, modifier = Modifier.padding(2.dp))
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().heightIn(min = 190.dp, max = 290.dp).border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xF00A1F18))
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(14.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                if (currentAyah?.numberInSurah == 1 && currentSurahNum != 9) {
                    Text("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 6.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Text("${currentAyah?.numberInSurah ?: startAyah}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Text("الجزء ${currentAyah?.juz ?: 1} • صفحة ${currentAyah?.page ?: 1}", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(currentAyah?.textArabic ?: "جاري التحميل...", fontSize = 19.sp, lineHeight = 34.sp, color = Color.White, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (ayahIndex > 0) {
                OutlinedButton(onClick = { ayahIndex -= 1 }, modifier = Modifier.weight(0.7f).height(46.dp), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("السابق", fontSize = 12.sp)
                }
            }
            Button(
                onClick = {
                    if (ayahIndex + 1 < sessionAyahs.size) ayahIndex += 1
                    else if (surahListIndex + 1 < surahIds.size) { surahListIndex += 1; ayahIndex = 0 }
                    else onFinish()
                },
                enabled = !config.isTimedMode || secondsLeft == 0,
                modifier = Modifier.weight(1f).height(46.dp).testTag("quran_lock_action_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                val isLastSurah = surahListIndex + 1 >= surahIds.size
                val isLastAyah = ayahIndex + 1 >= sessionAyahs.size
                val btnLabel = when {
                    config.isTimedMode && secondsLeft > 0 -> "اقرأ وتدبر (متبقي $secondsLeft ث)"
                    !isLastAyah -> "الآية التالية (${ayahIndex + 2}/${sessionAyahs.size})"
                    !isLastSurah -> "السورة التالية ➔"
                    else -> "أتممت الورد وفتح القفل ✓"
                }
                Icon(if (isLastSurah && isLastAyah) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(btnLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
