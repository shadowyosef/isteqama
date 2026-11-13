package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.data.AppAppearancePreferences

@Composable
fun MyApplicationTheme(
    themeStyle: AppThemeStyle = AppThemeStyle.ANDALUS_ORNATE,
    fontStyle: AppFontStyle = AppFontStyle.DEFAULT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    customTheme: CustomThemeConfig = CustomThemeConfig(),
    content: @Composable () -> Unit
) {
    val colorScheme = resolveColorScheme(themeStyle, darkTheme, customTheme)
    val userFontScale by AppAppearancePreferences.fontScale.collectAsState()
    val baseDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = baseDensity.density,
            fontScale = (baseDensity.fontScale * userFontScale).coerceIn(0.85f, 3.2f)
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = buildAppTypography(fontStyle),
            content = content
        )
    }
}

private fun contrastOn(bg: Color): Color = if (bg.luminance() > 0.52f) Color(0xFF0F172A) else Color.White

private fun buildVibrantScheme(
    primary: Color,
    secondary: Color,
    tertiary: Color,
    bg: Color,
    surface: Color,
    surfaceVar: Color,
    fg: Color,
    isDark: Boolean
): ColorScheme {
    val pCont = lerp(surface, primary, if (isDark) 0.34f else 0.16f).copy(alpha = 1f)
    val sCont = lerp(surface, secondary, if (isDark) 0.34f else 0.20f).copy(alpha = 1f)
    val tCont = lerp(surface, tertiary, if (isDark) 0.34f else 0.18f).copy(alpha = 1f)
    return if (isDark) {
        darkColorScheme(
            primary = primary, onPrimary = contrastOn(primary),
            primaryContainer = pCont, onPrimaryContainer = Color.White,
            secondary = secondary, onSecondary = contrastOn(secondary),
            secondaryContainer = sCont, onSecondaryContainer = Color.White,
            tertiary = tertiary, onTertiary = contrastOn(tertiary),
            tertiaryContainer = tCont, onTertiaryContainer = Color.White,
            background = bg, onBackground = fg,
            surface = surface, onSurface = fg,
            surfaceVariant = surfaceVar, onSurfaceVariant = lerp(fg, surface, 0.22f),
            outline = lerp(bg, primary, 0.55f)
        )
    } else {
        lightColorScheme(
            primary = primary, onPrimary = contrastOn(primary),
            primaryContainer = pCont, onPrimaryContainer = lerp(primary, Color.Black, 0.38f),
            secondary = secondary, onSecondary = contrastOn(secondary),
            secondaryContainer = sCont, onSecondaryContainer = lerp(secondary, Color.Black, 0.45f),
            tertiary = tertiary, onTertiary = contrastOn(tertiary),
            tertiaryContainer = tCont, onTertiaryContainer = lerp(tertiary, Color.Black, 0.40f),
            background = bg, onBackground = fg,
            surface = surface, onSurface = fg,
            surfaceVariant = surfaceVar, onSurfaceVariant = lerp(fg, surface, 0.28f),
            outline = lerp(bg, primary, 0.45f)
        )
    }
}

fun resolveColorScheme(style: AppThemeStyle, dark: Boolean, custom: CustomThemeConfig = CustomThemeConfig()): ColorScheme = when (style) {
    AppThemeStyle.ANDALUS_ORNATE -> if (dark) buildVibrantScheme(
        ThemePalettes.AndalusAccent, ThemePalettes.MadinahAccent, Color(0xFF38BDF8),
        ThemePalettes.AndalusDarkBg, ThemePalettes.AndalusDarkSurface, ThemePalettes.AndalusDarkSurfaceVar, Color(0xFFE6FFFA), true
    ) else buildVibrantScheme(
        ThemePalettes.AndalusPrimary, ThemePalettes.AndalusSecondary, ThemePalettes.AndalusAccent,
        ThemePalettes.AndalusLightBg, ThemePalettes.AndalusLightSurface, ThemePalettes.AndalusLightSurfaceVar, Color(0xFF0D2824), false
    )
    AppThemeStyle.MADINAH_ROYAL -> if (dark) buildVibrantScheme(
        Color(0xFF10B981), ThemePalettes.MadinahAccent, Color(0xFFFB7185),
        ThemePalettes.MadinahDarkBg, ThemePalettes.MadinahDarkSurface, ThemePalettes.MadinahDarkSurfaceVar, Color(0xFFFFF8E7), true
    ) else buildVibrantScheme(
        ThemePalettes.MadinahPrimary, ThemePalettes.MadinahAccent, ThemePalettes.MadinahSecondary,
        ThemePalettes.MadinahLightBg, ThemePalettes.MadinahLightSurface, ThemePalettes.MadinahLightSurfaceVar, Color(0xFF14281D), false
    )
    AppThemeStyle.ISTAQAMA -> if (dark) buildVibrantScheme(
        ThemePalettes.IstaqamaAccent, ThemePalettes.IstaqamaSecondary, Color(0xFFFBBF24),
        ThemePalettes.IstaqamaDarkBg, ThemePalettes.IstaqamaDarkSurface, Color(0xFF1A3C2F), Color(0xFFE2F5EC), true
    ) else buildVibrantScheme(
        ThemePalettes.IstaqamaPrimary, ThemePalettes.IstaqamaSecondary, Color(0xFFD97706),
        ThemePalettes.IstaqamaLightBg, Color.White, Color(0xFFDCFCE7), Color(0xFF14241C), false
    )
    AppThemeStyle.ROYAL_GOLD -> if (dark) buildVibrantScheme(
        ThemePalettes.RoyalAccent, ThemePalettes.RoyalSecondary, Color(0xFF38BDF8),
        ThemePalettes.RoyalDarkBg, ThemePalettes.RoyalDarkSurface, Color(0xFF3A2C11), Color(0xFFFFF8E1), true
    ) else buildVibrantScheme(
        ThemePalettes.RoyalPrimary, ThemePalettes.RoyalSecondary, Color(0xFFD97706),
        ThemePalettes.RoyalLightBg, Color.White, Color(0xFFFEF3C7), Color(0xFF2A2006), false
    )
    AppThemeStyle.NOOR -> if (dark) buildVibrantScheme(
        ThemePalettes.NoorAccent, ThemePalettes.NoorSecondary, Color(0xFF34D399),
        ThemePalettes.NoorDarkBg, ThemePalettes.NoorDarkSurface, Color(0xFF1A2F4E), Color(0xFFE2E8F0), true
    ) else buildVibrantScheme(
        ThemePalettes.NoorPrimary, ThemePalettes.NoorSecondary, Color(0xFF0D9488),
        ThemePalettes.NoorLightBg, Color.White, Color(0xFFE0F2FE), Color(0xFF0F172A), false
    )
    AppThemeStyle.WARQAA -> if (dark) buildVibrantScheme(
        ThemePalettes.WarqaaAccent, Color(0xFF10B981), Color(0xFFF472B6),
        ThemePalettes.WarqaaDarkBg, ThemePalettes.WarqaaDarkSurface, Color(0xFF3D2E27), Color(0xFFF3ECE4), true
    ) else buildVibrantScheme(
        ThemePalettes.WarqaaPrimary, ThemePalettes.WarqaaSecondary, Color(0xFF0D9488),
        ThemePalettes.WarqaaLightBg, Color.White, Color(0xFFF5E6D3), Color(0xFF2C1E14), false
    )
    AppThemeStyle.SOFT_ROSE -> if (dark) buildVibrantScheme(
        ThemePalettes.RoseAccent, Color(0xFFA78BFA), Color(0xFF34D399),
        ThemePalettes.RoseDarkBg, ThemePalettes.RoseDarkSurface, Color(0xFF4A1D31), Color(0xFFFCE7F3), true
    ) else buildVibrantScheme(
        ThemePalettes.RosePrimary, ThemePalettes.RoseSecondary, Color(0xFF059669),
        ThemePalettes.RoseLightBg, Color.White, Color(0xFFFCE7F3), Color(0xFF2B0E18), false
    )
    AppThemeStyle.CUSTOM_STUDIO -> {
        val pri = Color(custom.primaryHex)
        val sec = Color(custom.secondaryHex)
        val bg = Color(custom.backgroundHex)
        val surf = Color(custom.surfaceHex)
        val fg = Color(custom.foregroundHex)
        val isDarkBg = bg.luminance() < 0.35f
        buildVibrantScheme(pri, sec, sec, bg, surf, lerp(surf, pri, 0.12f), fg, isDarkBg)
    }
}
