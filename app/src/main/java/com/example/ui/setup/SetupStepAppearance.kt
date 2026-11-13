package com.example.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppAppearancePreferences
import com.example.data.AppPreferencesManager
import com.example.ui.settings.CustomThemeEditorDialog
import com.example.ui.theme.*

@Composable
fun SetupStepAppearance(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isAr by AppPreferencesManager.isArabic.collectAsState()
    val isDark by AppPreferencesManager.isDarkMode.collectAsState()
    val currentTheme by AppAppearancePreferences.themeStyle.collectAsState()
    val currentFont by AppAppearancePreferences.fontStyle.collectAsState()
    val customCfg by AppAppearancePreferences.customTheme.collectAsState()
    val patOverride by AppAppearancePreferences.patternOverride.collectAsState()
    val activePattern = AppAppearancePreferences.resolvePattern(currentTheme, patOverride, customCfg)
    var showStudio by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OrnateIslamicCard {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(if (isAr) "اختر الثيم الإسلامي المفضل:" else "Choose Islamic Theme Palette:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppThemeStyle.values().forEach { style ->
                        FilterChip(
                            selected = currentTheme == style,
                            onClick = { AppAppearancePreferences.setThemeStyle(context, style) },
                            leadingIcon = { Box(Modifier.size(12.dp).clip(CircleShape).background(swatchOf(style, customCfg))) },
                            label = { Text(if (isAr) style.labelAr else style.labelEn, fontSize = 11.sp) },
                            modifier = Modifier.heightIn(min = 42.dp).testTag("setup_theme_${style.name}")
                        )
                    }
                }
                Text(if (isAr) "الزخرفة الهندسية للخلفية:" else "Geometric Ornament Style:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IslamicPatternStyle.values().forEach { pat ->
                        FilterChip(
                            selected = activePattern == pat,
                            onClick = { AppAppearancePreferences.setPatternOverride(context, pat) },
                            label = { Text(if (isAr) pat.labelAr else pat.labelEn, fontSize = 11.sp) },
                            modifier = Modifier.heightIn(min = 40.dp)
                        )
                    }
                }
            }
        }

        OrnateIslamicCard {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(if (isAr) "خط المصحف والوضع الليلي:" else "Quranic Font & Dark Mode:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppFontStyle.values().forEach { font ->
                        FilterChip(
                            selected = currentFont == font,
                            onClick = { AppAppearancePreferences.setFontStyle(context, font) },
                            label = { Text(if (isAr) font.labelAr else font.labelEn, fontSize = 11.sp) }
                        )
                    }
                }
                com.example.ui.settings.QuranFontPreviewCard(selectedFont = currentFont, isArabic = isAr)
                com.example.ui.settings.FontScaleControlCard()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.DarkMode, null, tint = MaterialTheme.colorScheme.primary)
                        Text(if (isAr) "تفعيل الوضع الليلي" else "Dark Mode", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Switch(checked = isDark, onCheckedChange = { AppPreferencesManager.setDarkMode(it) })
                }
                OrnateIslamicTonalButton(onClick = { showStudio = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Palette, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (isAr) "فتح استوديو تخصيص الألوان يدوياً" else "Open Custom Color Studio", fontSize = 12.sp)
                }
            }
        }
    }

    if (showStudio) {
        CustomThemeEditorDialog(
            initialConfig = customCfg,
            onDismiss = { showStudio = false },
            onThemeApplied = { AppAppearancePreferences.setThemeStyle(context, it) }
        )
    }
}

private fun swatchOf(style: AppThemeStyle, custom: CustomThemeConfig): Color = when (style) {
    AppThemeStyle.ANDALUS_ORNATE -> ThemePalettes.AndalusPrimary
    AppThemeStyle.MADINAH_ROYAL -> ThemePalettes.MadinahPrimary
    AppThemeStyle.ISTAQAMA -> ThemePalettes.IstaqamaPrimary
    AppThemeStyle.ROYAL_GOLD -> ThemePalettes.RoyalPrimary
    AppThemeStyle.NOOR -> ThemePalettes.NoorPrimary
    AppThemeStyle.WARQAA -> ThemePalettes.WarqaaPrimary
    AppThemeStyle.SOFT_ROSE -> ThemePalettes.RosePrimary
    AppThemeStyle.CUSTOM_STUDIO -> Color(custom.primaryHex)
}
