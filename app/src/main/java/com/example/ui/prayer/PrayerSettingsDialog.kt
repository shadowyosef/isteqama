package com.example.ui.prayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.PrayerPresetCities
import com.example.data.PrayerTimesRepository
import com.example.model.PrayerAdjustments
import com.example.model.PrayerType
import com.example.util.AppStrings

@Composable
fun PrayerSettingsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val isOnline by PrayerTimesRepository.isOnlineEnabled.collectAsState()
    val isAdhan by PrayerTimesRepository.isAdhanAlertEnabled.collectAsState()
    val isLock by PrayerTimesRepository.isPrayerLockEnabled.collectAsState()
    val currentLocation by PrayerTimesRepository.currentLocation.collectAsState()
    val adjustments by PrayerTimesRepository.prayerAdjustments.collectAsState()
    var currentAdj by remember { mutableStateOf(adjustments) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "إعدادات مواقيت الصلاة" else "Prayer Times Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "تحديث المواقيت أونلاين" else "Online Prayer Times", fontSize = 13.sp)
                    Switch(checked = isOnline, onCheckedChange = { PrayerTimesRepository.toggleOnlineMode(context, it) })
                }
                Text(if (isOnline) (if (isAr) "يتم الجلب عبر الإنترنت مع الرجوع للأوفلاين تلقائياً" else "Fetches online with automatic offline fallback") else (if (isAr) "الحساب الفلكي الدقيق أوفلاين بالكامل" else "Fully offline astronomical calculation"), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "تنبيه صوت الأذان" else "Adhan Sound Alert", fontSize = 13.sp)
                    Switch(checked = isAdhan, onCheckedChange = { PrayerTimesRepository.toggleAdhanAlert(context, it) })
                }

                val iqamaMins by PrayerTimesRepository.iqamaOffsetMinutes.collectAsState()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "قفل الشاشة وقت الصلاة" else "Prayer Lock Screen", fontSize = 13.sp)
                    Switch(checked = isLock, onCheckedChange = { PrayerTimesRepository.togglePrayerLock(context, it) })
                }

                Text(if (isAr) "تنبيه الإقامة بعد الأذان:" else "Iqama Reminder After Adhan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0, 10, 15, 20).forEach { mins ->
                        FilterChip(
                            selected = iqamaMins == mins,
                            onClick = { PrayerTimesRepository.setIqamaOffset(context, mins) },
                            label = { Text(if (mins == 0) (if (isAr) "إيقاف" else "Off") else "$mins ${if (isAr) "د" else "m"}", fontSize = 11.sp, maxLines = 1) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider()

                Text(if (isAr) "المدينة / الموقع:" else "Location / City:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                PrayerCitySelector(currentLocation = currentLocation, isAr = isAr, onSelect = { PrayerTimesRepository.setLocation(context, it) })

                HorizontalDivider()

                Text(if (isAr) "تعديل أوقات الصلاة (دقائق +/-):" else "Prayer Time Adjustments (+/- min):", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                PrayerAdjustmentSliders(
                    adjustments = currentAdj,
                    isAr = isAr,
                    onChange = {
                        currentAdj = it
                        PrayerTimesRepository.updateAdjustments(context, it)
                    }
                )

                SeasonalAndAdhanSection(isAr = isAr)

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text(if (isAr) "حفظ وإغلاق" else "Save & Close")
                }
            }
        }
    }
}
