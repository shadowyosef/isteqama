package com.example.ui.prayer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PrayerCountdownHelper
import com.example.data.PrayerTimesRepository
import com.example.ui.qibla.QiblaCompassDialog
import com.example.util.AppStrings
import kotlinx.coroutines.delay

@Composable
fun PrayerTimesDashboardCard(
    onOpenPostPrayerLock: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val location by PrayerTimesRepository.currentLocation.collectAsState()
    val prayerTimes by PrayerTimesRepository.prayerTimes.collectAsState()
    var showSettings by remember { mutableStateOf(false) }
    var showCityDialog by remember { mutableStateOf(false) }
    var showQibla by remember { mutableStateOf(false) }
    val openQiblaFromNav by com.example.ui.navigation.NavigationState.openQiblaDialog.collectAsState()
    LaunchedEffect(openQiblaFromNav) {
        if (openQiblaFromNav) {
            showQibla = true
            com.example.ui.navigation.NavigationState.openQiblaDialog.value = false
        }
    }

    var nextPrayerPair by remember { mutableStateOf(PrayerCountdownHelper.getNextPrayerAndRemaining(prayerTimes)) }

    LaunchedEffect(prayerTimes) {
        while (true) {
            nextPrayerPair = PrayerCountdownHelper.getNextPrayerAndRemaining(prayerTimes)
            delay(1000L)
        }
    }

    val (nextItem, remainingMs) = nextPrayerPair
    val obligatoryCount = prayerTimes.count { it.type.isObligatoryPrayer }
    val prayedCount = prayerTimes.count { it.type.isObligatoryPrayer && it.isPrayed }

    com.example.ui.theme.OrnateIslamicCard(
        cornerRadius = 20.dp,
        modifier = modifier.testTag("prayer_times_dashboard_card")
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable { showCityDialog = true }
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (isAr) "${location.nameAr} (${location.countryAr})" else "${location.nameEn} (${location.countryEn})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Change Location", modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = { showSettings = true }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Tune, contentDescription = "Settings", modifier = Modifier.size(18.dp))
                }
            }

            if (nextItem != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text(
                            text = (if (isAr) "الصلاة القادمة: صلاة " else "Next Prayer: ") + (if (isAr) nextItem.type.nameAr else nextItem.type.nameEn),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = PrayerCountdownHelper.formatCountdown(remainingMs, isAr),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "$prayedCount / $obligatoryCount " + if (isAr) "صلوات أُدِّيت" else "prayed",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                prayerTimes.forEach { item ->
                    PrayerTimeItemChip(
                        item = item,
                        isAr = isAr,
                        onTogglePrayed = { PrayerTimesRepository.togglePrayerPrayed(context, item.type, it) }
                    )
                }
            }

            PrayerDashboardActionsRow(
                context = context,
                isAr = isAr,
                latitude = location.latitude,
                longitude = location.longitude,
                onOpenQibla = { showQibla = true },
                onOpenPostPrayer = onOpenPostPrayerLock
            )
            PrayerSeasonBannerChip(prayerTimes = prayerTimes, isAr = isAr)
        }
    }

    if (showSettings) PrayerSettingsDialog(onDismiss = { showSettings = false })
    if (showQibla) QiblaCompassDialog(onDismiss = { showQibla = false })
    if (showCityDialog) {
        PrayerCitySearchDialog(
            currentLocation = location,
            onSelect = { newLoc -> PrayerTimesRepository.setLocation(context, newLoc) },
            onDismiss = { showCityDialog = false }
        )
    }
}
