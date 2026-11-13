package com.example.ui.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppAppearancePreferences
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.CustomThemeConfig
import com.example.ui.theme.IslamicPatternStyle
import com.example.util.AppStrings

@Composable
fun CustomThemeEditorDialog(
    initialConfig: CustomThemeConfig,
    onDismiss: () -> Unit,
    onThemeApplied: (AppThemeStyle) -> Unit
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var cfg by remember { mutableStateOf(initialConfig) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().widthIn(max = 520.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isAr) "استوديو تخصيص الثيم والزخرفة" else "Custom Theme & Ornament Studio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                CustomThemePreviewCard(cfg = cfg, isAr = isAr)
                ThemeColorSwatchRow(
                    label = if (isAr) "لون الأزرار والأساسي (Buttons)" else "Buttons / Primary Color",
                    selectedHex = cfg.primaryHex,
                    swatches = listOf(0xFF00796B, 0xFF047857, 0xFF0284C7, 0xFFB45309, 0xFFBE185D, 0xFF6D28D9),
                    onSelect = { cfg = cfg.copy(primaryHex = it) }
                )
                ThemeColorSwatchRow(
                    label = if (isAr) "اللون الثانوي والتمييز (Accent)" else "Secondary / Accent Color",
                    selectedHex = cfg.secondaryHex,
                    swatches = listOf(0xFFE69500, 0xFFF59E0B, 0xFFBE123C, 0xFF14B8A6, 0xFF6366F1, 0xFF059669),
                    onSelect = { cfg = cfg.copy(secondaryHex = it) }
                )
                ThemeColorSwatchRow(
                    label = if (isAr) "لون الخلفية (Background)" else "Background Color",
                    selectedHex = cfg.backgroundHex,
                    swatches = listOf(0xFFF2FBF9, 0xFFFFFBF2, 0xFFF0F7FF, 0xFFFAF5EE, 0xFF061B1A, 0xFF0B1C14, 0xFF0F172A),
                    onSelect = {
                        val isDark = it in listOf(0xFF061B1A, 0xFF0B1C14, 0xFF0F172A)
                        cfg = cfg.copy(backgroundHex = it, foregroundHex = if (isDark) 0xFFE6FFFA else 0xFF0D2824, surfaceHex = if (isDark) 0xFF132C20 else 0xFFFFFFFF)
                    }
                )
                ThemeColorSwatchRow(
                    label = if (isAr) "لون البطاقات (Surface)" else "Surface / Cards Color",
                    selectedHex = cfg.surfaceHex,
                    swatches = listOf(0xFFFFFFFF, 0xFFE6F7F4, 0xFFFEF3C7, 0xFFE0F2FE, 0xFF132C20, 0xFF1E293B),
                    onSelect = { cfg = cfg.copy(surfaceHex = it) }
                )
                ThemeColorSwatchRow(
                    label = if (isAr) "لون الخط والأيقونات (Foreground)" else "Foreground / Text Color",
                    selectedHex = cfg.foregroundHex,
                    swatches = listOf(0xFF0D2824, 0xFF0F172A, 0xFF2A2006, 0xFF31102F, 0xFFE6FFFA, 0xFFFFF8E7),
                    onSelect = { cfg = cfg.copy(foregroundHex = it) }
                )

                Text(if (isAr) "نمط الزخرفة الإسلامية (Pattern)" else "Islamic Ornament Pattern", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IslamicPatternStyle.values().forEach { pat ->
                        FilterChip(
                            selected = cfg.patternStyle == pat,
                            onClick = { cfg = cfg.copy(patternStyle = pat) },
                            label = { Text(if (isAr) pat.labelAr else pat.labelEn, fontSize = 11.sp) }
                        )
                    }
                }
                Text((if (isAr) "شفافية الزخرفة: " else "Pattern Opacity: ") + "${(cfg.patternOpacity * 100).toInt()}%", fontSize = 12.sp)
                Slider(value = cfg.patternOpacity, onValueChange = { cfg = cfg.copy(patternOpacity = it.coerceIn(0f, 0.22f)) }, valueRange = 0f..0.22f)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(if (isAr) "إلغاء" else "Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            AppAppearancePreferences.saveCustomTheme(context, cfg)
                            AppAppearancePreferences.setThemeStyle(context, AppThemeStyle.CUSTOM_STUDIO)
                            onThemeApplied(AppThemeStyle.CUSTOM_STUDIO)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("save_custom_theme_btn")
                    ) { Text(if (isAr) "تطبيق الثيم" else "Apply Theme") }
                }
            }
        }
    }
}
