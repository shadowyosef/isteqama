package com.example.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OrnateIslamicCard
import com.example.util.AppStrings

@Composable
fun HomeAdhkarShortcutGrid(
    onOpenRoutines: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = AppStrings.isArabic

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = AppStrings.get("أذكار اليوم والليلة", "Daily Adhkar & Sequences"),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            TextButton(onClick = onOpenRoutines, contentPadding = PaddingValues(0.dp)) {
                Text(AppStrings.get("عرض الكل", "View All"), fontSize = 11.sp)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AdhkarShortcutTile(
                title = AppStrings.get("أذكار الصباح", "Morning Adhkar"),
                subtitle = AppStrings.get("بعد الفجر بساعة", "1h after Fajr") + " • 25",
                icon = Icons.Default.WbSunny,
                iconColor = Color(0xFFF59E0B),
                onClick = onOpenRoutines,
                modifier = Modifier.weight(1f).testTag("shortcut_morning_adhkar")
            )
            AdhkarShortcutTile(
                title = AppStrings.get("أذكار المساء", "Evening Adhkar"),
                subtitle = AppStrings.get("بعد العصر بساعة", "1h after Asr") + " • 14",
                icon = Icons.Default.NightsStay,
                iconColor = Color(0xFF8B5CF6),
                onClick = onOpenRoutines,
                modifier = Modifier.weight(1f).testTag("shortcut_evening_adhkar")
            )
        }

        AdhkarShortcutTile(
            title = AppStrings.get("ورد النوم - سورة الملك كاملة", "Bedtime Ward - Surat Al-Mulk"),
            subtitle = AppStrings.get("30 آية منجية من عذاب القبر • 10:00 PM", "30 protective ayahs • 10:00 PM"),
            icon = Icons.Default.Bedtime,
            iconColor = Color(0xFF10B981),
            onClick = onOpenRoutines,
            modifier = Modifier.fillMaxWidth().testTag("shortcut_sleep_ward")
        )
    }
}

@Composable
private fun AdhkarShortcutTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OrnateIslamicCard(
        cornerRadius = 16.dp,
        accentColor = iconColor,
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(shape = RoundedCornerShape(10.dp), color = iconColor.copy(alpha = 0.16f), modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
