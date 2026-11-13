package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import com.example.ui.theme.*
import com.example.util.AppStrings

@Composable
fun AppearanceSettingsSection(
    currentTheme: AppThemeStyle,
    onThemeChange: (AppThemeStyle) -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onLanguageChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val currentFont by AppAppearancePreferences.fontStyle.collectAsState()
    val customCfg by AppAppearancePreferences.customTheme.collectAsState()
    val patOverride by AppAppearancePreferences.patternOverride.collectAsState()
    var showCustomStudio by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = if (isAr) "المظهر والثيمات الإسلامية والخطوط" else "Appearance, Islamic Themes & Fonts",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AppThemeStyle.values().forEach { style ->
                val swatch = themeSwatchColor(style, customCfg)
                FilterChip(
                    selected = currentTheme == style,
                    onClick = {
                        onThemeChange(style)
                        AppAppearancePreferences.setThemeStyle(context, style)
                    },
                    leadingIcon = {
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(swatch))
                    },
                    label = { Text(if (isAr) style.labelAr else style.labelEn, fontSize = 11.sp) },
                    modifier = Modifier.testTag("theme_chip_${style.name}")
                )
            }
        }

        val activePattern = AppAppearancePreferences.resolvePattern(currentTheme, patOverride, customCfg)
        Text(if (isAr) "الزخرفة الإسلامية بالخلفية:" else "Islamic Background Ornament:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IslamicPatternStyle.values().forEach { pat ->
                FilterChip(
                    selected = activePattern == pat,
                    onClick = { AppAppearancePreferences.setPatternOverride(context, pat) },
                    label = { Text(if (isAr) pat.labelAr else pat.labelEn, fontSize = 11.sp) },
                    modifier = Modifier.testTag("pattern_chip_${pat.name}")
                )
            }
        }

        Button(
            onClick = { showCustomStudio = true },
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("open_custom_theme_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isAr) "تخصيص الألوان والأزرار يدوياً (Custom Studio)" else "Customize Colors & Pattern Manually")
        }

        Text(if (isAr) "خط المصحف والتطبيق:" else "App & Quranic Font:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AppFontStyle.values().forEach { font ->
                FilterChip(
                    selected = currentFont == font,
                    onClick = { AppAppearancePreferences.setFontStyle(context, font) },
                    label = { Text(if (isAr) font.labelAr else font.labelEn, fontSize = 11.sp) },
                    modifier = Modifier.testTag("font_chip_${font.name}")
                )
            }
        }
        QuranFontPreviewCard(selectedFont = currentFont, isArabic = isAr)
        FontScaleControlCard()

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(if (isAr) "الوضع الليلي (Dark Mode)" else "Dark Mode", fontWeight = FontWeight.SemiBold)
            Switch(checked = isDarkMode, onCheckedChange = onDarkModeToggle, modifier = Modifier.testTag("dark_mode_switch"))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(if (isAr) "لغة التطبيق (Language)" else "Language", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = isAr, onClick = { onLanguageChange(true) }, label = { Text("العربية") })
                FilterChip(selected = !isAr, onClick = { onLanguageChange(false) }, label = { Text("English") })
            }
        }
    }

    if (showCustomStudio) {
        CustomThemeEditorDialog(
            initialConfig = customCfg,
            onDismiss = { showCustomStudio = false },
            onThemeApplied = { onThemeChange(it) }
        )
    }
}

private fun themeSwatchColor(style: AppThemeStyle, custom: CustomThemeConfig): Color = when (style) {
    AppThemeStyle.ANDALUS_ORNATE -> ThemePalettes.AndalusPrimary
    AppThemeStyle.MADINAH_ROYAL -> ThemePalettes.MadinahPrimary
    AppThemeStyle.ISTAQAMA -> ThemePalettes.IstaqamaPrimary
    AppThemeStyle.ROYAL_GOLD -> ThemePalettes.RoyalPrimary
    AppThemeStyle.NOOR -> ThemePalettes.NoorPrimary
    AppThemeStyle.WARQAA -> ThemePalettes.WarqaaPrimary
    AppThemeStyle.SOFT_ROSE -> ThemePalettes.RosePrimary
    AppThemeStyle.CUSTOM_STUDIO -> Color(custom.primaryHex)
}
