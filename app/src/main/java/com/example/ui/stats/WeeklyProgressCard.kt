package com.example.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyProgressRecord
import com.example.ui.theme.OrnateIslamicCard
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeeklyProgressCard(
    progressMap: Map<String, DailyProgressRecord>,
    isArabic: Boolean
) {
    val weekDays = remember(progressMap) {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.SATURDAY
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayNameFormat = SimpleDateFormat("EEE", if (isArabic) Locale("ar") else Locale.ENGLISH)
        (0..6).map { i ->
            val dayCal = (cal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, -(6 - i))
            }
            val key = format.format(dayCal.time)
            val name = dayNameFormat.format(dayCal.time)
            val rec = progressMap[key]
            Triple(name, rec?.totalObligatoryPrayed ?: 0, (rec?.quranMinutes ?: 0) + (rec?.ayahsRead ?: 0) / 10)
        }
    }

    OrnateIslamicCard(cornerRadius = 18.dp) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = if (isArabic) "إنجاز الأيام السبعة الأخيرة" else "Last 7 Days Progress",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Row(modifier = Modifier.fillMaxWidth().height(100.dp), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.Bottom) {
                weekDays.forEach { (dayName, prayersCount, activityScore) ->
                    val prayerHeight = (prayersCount.toFloat() / 5f).coerceIn(0.1f, 1f)
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Text("${prayersCount}/5", fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height((60 * prayerHeight).dp)
                                .background(if (prayersCount >= 5) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(dayName, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
