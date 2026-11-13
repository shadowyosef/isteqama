package com.example.ui.prayer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PrayerAdjustments
import com.example.model.PrayerType

@Composable
fun PrayerAdjustmentSliders(
    adjustments: PrayerAdjustments,
    isAr: Boolean,
    onChange: (PrayerAdjustments) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        val list = listOf(
            PrayerType.FAJR to adjustments.fajrOffset,
            PrayerType.DHUHR to adjustments.dhuhrOffset,
            PrayerType.ASR to adjustments.asrOffset,
            PrayerType.MAGHRIB to adjustments.maghribOffset,
            PrayerType.ISHA to adjustments.ishaOffset
        )
        list.forEach { (type, offset) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (isAr) type.nameAr else type.nameEn, fontSize = 12.sp, modifier = Modifier.width(60.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        val next = offset - 1
                        onChange(adjustments.copyOffset(type, next))
                    }, modifier = Modifier.size(28.dp)) {
                        Text("-", fontSize = 16.sp)
                    }
                    Text(
                        text = (if (offset > 0) "+$offset" else "$offset") + if (isAr) " د" else "m",
                        fontSize = 11.sp,
                        modifier = Modifier.width(36.dp)
                    )
                    IconButton(onClick = {
                        val next = offset + 1
                        onChange(adjustments.copyOffset(type, next))
                    }, modifier = Modifier.size(28.dp)) {
                        Text("+", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

private fun PrayerAdjustments.copyOffset(type: PrayerType, value: Int): PrayerAdjustments {
    return when (type) {
        PrayerType.FAJR -> copy(fajrOffset = value)
        PrayerType.SUNRISE -> copy(sunriseOffset = value)
        PrayerType.DHUHR -> copy(dhuhrOffset = value)
        PrayerType.ASR -> copy(asrOffset = value)
        PrayerType.MAGHRIB -> copy(maghribOffset = value)
        PrayerType.ISHA -> copy(ishaOffset = value)
    }
}
