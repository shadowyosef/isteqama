package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.PrayerTimesRepository
import com.example.ui.theme.islamicGeometricPattern
import com.example.util.AppStrings
import com.example.util.PrayerSeasonsHelper
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BedsideClockDialog(onDismiss: () -> Unit) {
    val isAr = AppStrings.isArabic
    var nowTime by remember { mutableStateOf("") }
    var wakeMode by remember { mutableStateOf(false) }
    val prayers by PrayerTimesRepository.prayerTimes.collectAsState()
    val lastThird = remember(prayers) { PrayerSeasonsHelper.calculateLastThirdStart(prayers) }
    val fajr = prayers.firstOrNull()
    val fajrTime = if (fajr != null) String.format(Locale.US, "%02d:%02d", fajr.hour, fajr.minute) else "05:00"
    val todayLabel = remember(isAr) {
        val locale = if (isAr) Locale.forLanguageTag("ar") else Locale.ENGLISH
        SimpleDateFormat("EEEE، d MMMM yyyy", locale).format(Date())
    }

    LaunchedEffect(Unit) {
        val fmt = SimpleDateFormat("HH:mm:ss", Locale.US)
        while (true) { nowTime = fmt.format(Date()); delay(1000) }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF040907))
                .islamicGeometricPattern(Color(0xFF1B5E20), Color(0xFFD4AF37), stepPx = 110f, alpha = 0.08f)
                .padding(24.dp)
        ) {
            IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).size(48.dp)) {
                Icon(Icons.Default.Close, "Close", tint = Color(0xFF81C784))
            }
            Column(
                modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(Icons.Default.Bedtime, null, tint = Color(0xFFD4AF37), modifier = Modifier.size(36.dp))
                Text(nowTime, fontSize = 54.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFE8F5E9))
                Text(todayLabel, fontSize = 13.sp, color = Color(0xFFD4AF37))
                Text(
                    text = "${if (isAr) "الثلث الأخير:" else "Last Third:"} $lastThird  •  ${if (isAr) "الفجر:" else "Fajr:"} $fajrTime",
                    fontSize = 12.sp, color = Color(0xFFA5D6A7)
                )
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF0B1F17),
                    modifier = Modifier.fillMaxWidth().clickable { wakeMode = !wakeMode }
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            if (wakeMode) (if (isAr) "دعاء الاستيقاظ من النوم" else "Wake-up Duaa")
                            else (if (isAr) "دعاء النوم المأثور" else "Bedtime Duaa"),
                            fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFD4AF37)
                        )
                        Text(
                            if (wakeMode) "«الحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ»"
                            else "«بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا» • آية الكرسي والمعوذات",
                            fontSize = 15.sp, color = Color.White, textAlign = TextAlign.Center, lineHeight = 24.sp
                        )
                        Text(if (isAr) "اضغط للتبديل بين أذكار النوم والاستيقاظ" else "Tap to switch Sleep / Wake Duaa", fontSize = 10.sp, color = Color(0xFF81C784))
                    }
                }
            }
        }
    }
}
