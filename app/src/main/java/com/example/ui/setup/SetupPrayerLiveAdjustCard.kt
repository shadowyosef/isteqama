package com.example.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PrayerTimesRepository
import com.example.model.PrayerAdjustments
import com.example.model.PrayerLocation
import com.example.model.PrayerType
import com.example.ui.theme.OrnateIslamicCard
import com.example.util.PrayerTimesCalculator
import com.example.util.TimeFormatUtils
import java.util.Calendar

@Composable
fun SetupPrayerLiveAdjustCard(
    location: PrayerLocation,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val adjustments by PrayerTimesRepository.prayerAdjustments.collectAsState()
    val livePrayers = remember(location, adjustments) {
        PrayerTimesCalculator.calculate(
            latitude = location.latitude,
            longitude = location.longitude,
            calendar = Calendar.getInstance(),
            adjustments = adjustments,
            timezoneHours = location.timezoneHours
        )
    }
    val hasOffsets = adjustments != PrayerAdjustments()

    OrnateIslamicCard(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Schedule, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (isArabic) "مواقيت الصلاة اليوم (تعديل مباشر ± دقيقة):" else "Live Prayer Times (Manual ±1 min):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (hasOffsets) {
                    TextButton(
                        onClick = { PrayerTimesRepository.updateAdjustments(context, PrayerAdjustments()) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text(if (isArabic) "تصفير" else "Reset", fontSize = 11.sp)
                    }
                }
            }

            livePrayers.forEach { item ->
                val offset = adjustments.getOffset(item.type)
                val timeStr = TimeFormatUtils.format12Hour(item.hour, item.minute, isArabic)
                Row(
                    modifier = Modifier.fillMaxWidth().testTag("setup_prayer_row_${item.type.id}"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (isArabic) item.type.nameAr else item.type.nameEn, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(timeStr, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilledTonalIconButton(
                            onClick = { PrayerTimesRepository.updateAdjustments(context, adjustments.withOffset(item.type, offset - 1)) },
                            modifier = Modifier.size(40.dp).testTag("prayer_minus_${item.type.id}")
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "-1m", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = if (offset > 0) "+$offset" else "$offset",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.widthIn(min = 28.dp)
                        )
                        FilledTonalIconButton(
                            onClick = { PrayerTimesRepository.updateAdjustments(context, adjustments.withOffset(item.type, offset + 1)) },
                            modifier = Modifier.size(40.dp).testTag("prayer_plus_${item.type.id}")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "+1m", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun PrayerAdjustments.withOffset(type: PrayerType, value: Int): PrayerAdjustments = when (type) {
    PrayerType.FAJR -> copy(fajrOffset = value.coerceIn(-60, 60))
    PrayerType.SUNRISE -> copy(sunriseOffset = value.coerceIn(-60, 60))
    PrayerType.DHUHR -> copy(dhuhrOffset = value.coerceIn(-60, 60))
    PrayerType.ASR -> copy(asrOffset = value.coerceIn(-60, 60))
    PrayerType.MAGHRIB -> copy(maghribOffset = value.coerceIn(-60, 60))
    PrayerType.ISHA -> copy(ishaOffset = value.coerceIn(-60, 60))
}
