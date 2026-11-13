package com.example.ui.prayer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PrayerTimesRepository
import com.example.util.MuadhinVoice
import com.example.util.PrayerSeasonsHelper

@Composable
fun SeasonalAndAdhanSection(isAr: Boolean) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { PrayerSeasonsHelper.init(context) }

    val muadhin by PrayerSeasonsHelper.selectedMuadhin.collectAsState()
    val pre15 by PrayerSeasonsHelper.preAdhan15mEnabled.collectAsState()
    val lastThird by PrayerSeasonsHelper.lastThirdAlarmEnabled.collectAsState()
    val prayerTimes by PrayerTimesRepository.prayerTimes.collectAsState()
    val lastThirdTime = remember(prayerTimes) { PrayerSeasonsHelper.calculateLastThirdStart(prayerTimes) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HorizontalDivider()
        Text(
            text = if (isAr) "اختيار صوت المؤذن:" else "Muadhin Voice:",
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MuadhinVoice.entries.forEach { voice ->
                FilterChip(
                    selected = muadhin == voice,
                    onClick = { PrayerSeasonsHelper.setMuadhin(context, voice) },
                    label = { Text(if (isAr) voice.labelAr else voice.labelEn, fontSize = 11.sp) }
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(if (isAr) "تنبيه قبل الأذان بـ 15 دقيقة" else "15-Min Pre-Adhan Reminder", fontSize = 12.sp)
            Switch(checked = pre15, onCheckedChange = { PrayerSeasonsHelper.setPreAdhan15m(context, it) })
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(if (isAr) "منبه الثلث الأخير من الليل" else "Last Third of Night Alarm", fontSize = 12.sp)
                Text(if (isAr) "يبدأ الليلة الساعة $lastThirdTime" else "Starts tonight at $lastThirdTime", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            }
            Switch(checked = lastThird, onCheckedChange = { PrayerSeasonsHelper.setLastThirdAlarm(context, it) })
        }
    }
}
