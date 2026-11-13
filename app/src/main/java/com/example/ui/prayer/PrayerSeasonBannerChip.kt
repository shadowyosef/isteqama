package com.example.ui.prayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PrayerTimeItem
import com.example.util.PrayerSeasonsHelper
import java.util.Calendar

@Composable
fun PrayerSeasonBannerChip(prayerTimes: List<PrayerTimeItem>, isAr: Boolean) {
    val banner = remember(isAr) { PrayerSeasonsHelper.getSeasonalBanner(isAr) }
    val cal = remember { Calendar.getInstance() }
    val forbidden = remember(prayerTimes, isAr) {
        PrayerSeasonsHelper.getForbiddenNaflReason(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), prayerTimes, isAr)
    }
    val lastThird = remember(prayerTimes) { PrayerSeasonsHelper.calculateLastThirdStart(prayerTimes) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (banner != null) {
            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)) {
                Text(banner, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth().padding(8.dp))
            }
        }
        Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)) {
            Text(
                text = if (isAr) "الثلث الأخير من الليل يبدأ: $lastThird" else "Last Third of Night starts: $lastThird",
                fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 5.dp)
            )
        }
        if (forbidden != null) {
            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)) {
                Text("⚠ $forbidden", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.fillMaxWidth().padding(8.dp))
            }
        }
    }
}
