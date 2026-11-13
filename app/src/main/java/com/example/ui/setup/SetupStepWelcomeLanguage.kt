package com.example.ui.setup

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppPreferencesManager
import com.example.data.PrayerPresetCities
import com.example.data.PrayerTimesRepository
import com.example.ui.theme.IslamicSectionDivider
import com.example.ui.theme.OrnateIslamicCard

@Composable
fun SetupStepWelcomeLanguage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val currentLoc by PrayerTimesRepository.currentLocation.collectAsState()

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Image(
            painter = painterResource(id = R.drawable.img_islamic_hero_banner_1794560217113),
            contentDescription = if (isArabic) "زخرفة استقامة الإسلامية" else "Istaqama Islamic Artwork",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(124.dp)
                .clip(RoundedCornerShape(18.dp))
        )

        Text(
            text = if (isArabic) "أهلاً بك في رحاب استقامة" else "Welcome to Istaqama",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = if (isArabic)
                "سنمر معاً في ٤ خطوات سريعة لضبط لغتك، أذونات القفل، مظهرك الإسلامي، وأورادك التلقائية."
            else "Let's walk through 4 quick steps to set your language, lock permissions, Islamic theme, and daily defaults.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        OrnateIslamicCard {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Language, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (isArabic) "اختر لغة التطبيق (App Language)" else "Choose App Language",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = isArabic,
                        onClick = { AppPreferencesManager.setLanguage(true) },
                        label = { Text("العربية 🇸🇦", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("setup_lang_ar_chip")
                    )
                    FilterChip(
                        selected = !isArabic,
                        onClick = { AppPreferencesManager.setLanguage(false) },
                        label = { Text("English 🌐", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("setup_lang_en_chip")
                    )
                }
            }
        }

        IslamicSectionDivider()

        OrnateIslamicCard {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (isArabic) "مدينة حساب مواقيت الصلاة والأذكار:" else "Prayer & Adhkar City:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (isArabic) "المدينة الحالية: ${currentLoc.nameAr} (${currentLoc.countryAr})"
                    else "Current City: ${currentLoc.nameEn} (${currentLoc.countryEn})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PrayerPresetCities.presets.forEach { city ->
                        FilterChip(
                            selected = currentLoc.nameEn == city.nameEn,
                            onClick = { PrayerTimesRepository.setLocation(context, city) },
                            label = { Text(if (isArabic) city.nameAr else city.nameEn, fontSize = 11.sp) },
                            modifier = Modifier.heightIn(min = 40.dp)
                        )
                    }
                }
            }
        }

        SetupPrayerLiveAdjustCard(location = currentLoc, isArabic = isArabic)
    }
}
