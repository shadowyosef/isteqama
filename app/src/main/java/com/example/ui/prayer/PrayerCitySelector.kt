package com.example.ui.prayer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PrayerPresetCities
import com.example.model.PrayerLocation

@Composable
fun PrayerCitySelector(
    currentLocation: PrayerLocation,
    isAr: Boolean,
    onSelect: (PrayerLocation) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PrayerPresetCities.presets.forEach { city ->
            val isSelected = city.nameAr == currentLocation.nameAr || city.nameEn == currentLocation.nameEn
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(city) },
                label = { Text(if (isAr) city.nameAr else city.nameEn, fontSize = 11.sp) }
            )
        }
    }
}
