package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdhkarRepository
import com.example.data.QuranRepository
import com.example.data.TasbeehRepository
import com.example.model.LockSchedule
import com.example.model.LockType

@Composable
fun ActiveLockContentCard(
    schedule: LockSchedule,
    progressCount: Int
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.12f),
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (schedule.lockType) {
                LockType.QURAN_VERSES -> {
                    val surah = QuranRepository.getSurah(schedule.targetSurah)
                    val ayahs = QuranRepository.getAyahsForSurah(schedule.targetSurah)
                    val targetAyahIndex = (schedule.startAyah - 1 + progressCount).coerceIn(0, ayahs.lastIndex)
                    val currentAyah = ayahs.getOrNull(targetAyahIndex)

                    Text(
                        text = "سورة ${surah?.nameAr ?: "القرآن"} (الآية ${currentAyah?.numberInSurah ?: (progressCount + 1)})",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentAyah?.textArabic ?: "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Medium
                    )
                    currentAyah?.tafseer?.let { tafseer ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = tafseer,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                LockType.ADHKAR_ROUTINE -> {
                    val customList = com.example.data.CustomDhikrRepository.lists.value.find { "lock_dhikr_${it.id}" == schedule.id }
                    val customItem = customList?.items?.getOrNull(progressCount % (customList.items.size.coerceAtLeast(1)))
                    val items = AdhkarRepository.adhkarState.value
                    val text = customItem?.textAr ?: items.getOrElse(progressCount % items.size) { items[0] }.textAr
                    Text(
                        text = "الذكر (${progressCount + 1}/${schedule.targetAmount})",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = text,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }
                LockType.TASBEEH_COUNT -> {
                    val tasbeeh = TasbeehRepository.getCurrentItem()
                    Text(
                        text = "التسبيح المستهدف (${progressCount}/${schedule.targetAmount})",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = tasbeeh.phraseAr,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
