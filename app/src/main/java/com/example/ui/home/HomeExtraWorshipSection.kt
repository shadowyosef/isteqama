package com.example.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.adhkar.OccasionAdhkarDialog
import com.example.ui.circles.SharedKhatmaCirclesDialog
import com.example.ui.kids.KidsModeDialog
import com.example.ui.lock.AmbientSoundSheet
import com.example.ui.lock.DigitalWellbeingProSheet
import com.example.ui.lock.FocusProfilesSheet
import com.example.ui.prayer.AutoSilentMosqueCard
import com.example.ui.prayer.MissedPrayersTrackerCard
import com.example.ui.prayer.NearbyMosquesDialog
import com.example.ui.prayer.VoluntaryFastingCard
import com.example.ui.quran.QiyamPlannerDialog
import com.example.ui.quran.QuranRadioDialog
import com.example.ui.settings.ZakatCalculatorDialog
import com.example.ui.theme.OrnateIslamicTonalButton
import com.example.util.AppStrings

@Composable
fun HomeExtraWorshipSection() {
    val isAr = AppStrings.isArabic
    var showZakatDialog by remember { mutableStateOf(false) }
    var showOccasionDialog by remember { mutableStateOf(false) }
    var showFocusProfiles by remember { mutableStateOf(false) }
    var showQiyamPlanner by remember { mutableStateOf(false) }
    var showCircles by remember { mutableStateOf(false) }
    var showKidsMode by remember { mutableStateOf(false) }
    var showHadithLibrary by remember { mutableStateOf(false) }
    var showQuranRadio by remember { mutableStateOf(false) }
    var showNearbyMosques by remember { mutableStateOf(false) }
    var showWellbeingPro by remember { mutableStateOf(false) }
    var showBedsideClock by remember { mutableStateOf(false) }
    var showAmbientSound by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OrnateIslamicTonalButton(onClick = { showFocusProfiles = true }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.CenterFocusStrong, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isAr) "أنماط التركيز" else "Focus Modes", fontSize = 11.sp, maxLines = 1)
            }
            OrnateIslamicTonalButton(onClick = { showQiyamPlanner = true }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.NightsStay, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isAr) "مخطط القيام" else "Qiyam Plan", fontSize = 11.sp, maxLines = 1)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OrnateIslamicTonalButton(onClick = { showCircles = true }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Group, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isAr) "حلقات الختمة" else "Circles", fontSize = 11.sp, maxLines = 1)
            }
            OrnateIslamicTonalButton(onClick = { showKidsMode = true }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.ChildCare, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isAr) "وضع الصغار" else "Kids Mode", fontSize = 11.sp, maxLines = 1)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OrnateIslamicTonalButton(onClick = { showHadithLibrary = true }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.MenuBook, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isAr) "مكتبة الأحاديث" else "Hadith Bank", fontSize = 11.sp, maxLines = 1)
            }
            OrnateIslamicTonalButton(onClick = { showQuranRadio = true }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Radio, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isAr) "إذاعة القرآن" else "Quran Radio", fontSize = 11.sp, maxLines = 1)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OrnateIslamicTonalButton(onClick = { showNearbyMosques = true }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Mosque, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isAr) "أقرب المساجد" else "Mosques", fontSize = 11.sp, maxLines = 1)
            }
            OrnateIslamicTonalButton(onClick = { showWellbeingPro = true }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Spa, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isAr) "الدرع الرقمي" else "Mindful Gate", fontSize = 11.sp, maxLines = 1)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OrnateIslamicTonalButton(onClick = { showBedsideClock = true }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Bedtime, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isAr) "الساعة الليلية" else "Night Clock", fontSize = 11.sp, maxLines = 1)
            }
            OrnateIslamicTonalButton(onClick = { showAmbientSound = true }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.GraphicEq, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isAr) "أصوات الخشوع" else "Nature Audio", fontSize = 11.sp, maxLines = 1)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OrnateIslamicTonalButton(onClick = { showOccasionDialog = true }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.FormatQuote, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isAr) "أدعية المناسبات" else "Occasion Duaas", fontSize = 11.sp, maxLines = 1)
            }
            OrnateIslamicTonalButton(onClick = { showZakatDialog = true }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Calculate, null, Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (isAr) "حاسبة الزكاة" else "Zakat Calc", fontSize = 11.sp, maxLines = 1)
            }
        }

        VoluntaryFastingCard()
        MissedPrayersTrackerCard()
        AutoSilentMosqueCard()
    }

    if (showZakatDialog) ZakatCalculatorDialog(onDismiss = { showZakatDialog = false })
    if (showOccasionDialog) OccasionAdhkarDialog(onDismiss = { showOccasionDialog = false })
    if (showFocusProfiles) FocusProfilesSheet(onDismiss = { showFocusProfiles = false })
    if (showQiyamPlanner) QiyamPlannerDialog(onDismiss = { showQiyamPlanner = false })
    if (showCircles) SharedKhatmaCirclesDialog(onDismiss = { showCircles = false })
    if (showKidsMode) KidsModeDialog(onDismiss = { showKidsMode = false })
    if (showHadithLibrary) HadithLibraryDialog(onDismiss = { showHadithLibrary = false })
    if (showQuranRadio) QuranRadioDialog(onDismiss = { showQuranRadio = false })
    if (showNearbyMosques) NearbyMosquesDialog(onDismiss = { showNearbyMosques = false })
    if (showWellbeingPro) DigitalWellbeingProSheet(onDismiss = { showWellbeingPro = false })
    if (showBedsideClock) BedsideClockDialog(onDismiss = { showBedsideClock = false })
    if (showAmbientSound) AmbientSoundSheet(onDismiss = { showAmbientSound = false })
}
