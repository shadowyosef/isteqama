package com.example.data

import android.content.Context
import com.example.ui.theme.AppFontStyle
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.CustomThemeConfig
import com.example.ui.theme.IslamicPatternStyle
import kotlinx.coroutines.flow.MutableStateFlow

object AppAppearancePreferences {
    private const val PREFS = "istaqama_appearance_prefs"
    val themeStyle = MutableStateFlow(AppThemeStyle.ANDALUS_ORNATE)
    val fontStyle = MutableStateFlow(AppFontStyle.DEFAULT)
    val fontScale = MutableStateFlow(1.0f)
    val customTheme = MutableStateFlow(CustomThemeConfig())
    val patternOverride = MutableStateFlow<IslamicPatternStyle?>(null)

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val tName = prefs.getString("theme_style", AppThemeStyle.ANDALUS_ORNATE.name)
        val fName = prefs.getString("font_style", AppFontStyle.DEFAULT.name)
        themeStyle.value = runCatching { AppThemeStyle.valueOf(tName ?: "") }.getOrDefault(AppThemeStyle.ANDALUS_ORNATE)
        fontStyle.value = runCatching { AppFontStyle.valueOf(fName ?: "") }.getOrDefault(AppFontStyle.DEFAULT)
        fontScale.value = prefs.getFloat("font_scale", 1.0f).coerceIn(0.85f, 3.0f)

        val patName = prefs.getString("custom_pattern", IslamicPatternStyle.STAR_ROSETTE.name)
        val pat = runCatching { IslamicPatternStyle.valueOf(patName ?: "") }.getOrDefault(IslamicPatternStyle.STAR_ROSETTE)
        customTheme.value = CustomThemeConfig(
            primaryHex = prefs.getLong("c_primary", 0xFF00796B),
            secondaryHex = prefs.getLong("c_secondary", 0xFFE69500),
            backgroundHex = prefs.getLong("c_bg", 0xFFF2FBF9),
            surfaceHex = prefs.getLong("c_surface", 0xFFFFFFFF),
            foregroundHex = prefs.getLong("c_fg", 0xFF0D2824),
            patternStyle = pat,
            patternOpacity = prefs.getFloat("c_pat_alpha", 0.18f).coerceIn(0f, 0.30f)
        )
        val ovName = prefs.getString("pattern_override", "")
        patternOverride.value = ovName?.takeIf { it.isNotBlank() }?.let { runCatching { IslamicPatternStyle.valueOf(it) }.getOrNull() }
    }

    fun setThemeStyle(context: Context, style: AppThemeStyle) {
        themeStyle.value = style
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("theme_style", style.name).apply()
    }

    fun setFontStyle(context: Context, style: AppFontStyle) {
        fontStyle.value = style
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("font_style", style.name).apply()
    }

    fun setFontScale(context: Context, scale: Float) {
        val clamped = scale.coerceIn(0.85f, 3.0f)
        fontScale.value = clamped
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putFloat("font_scale", clamped).apply()
    }

    fun saveCustomTheme(context: Context, cfg: CustomThemeConfig) {
        customTheme.value = cfg
        patternOverride.value = cfg.patternStyle
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putLong("c_primary", cfg.primaryHex)
            .putLong("c_secondary", cfg.secondaryHex)
            .putLong("c_bg", cfg.backgroundHex)
            .putLong("c_surface", cfg.surfaceHex)
            .putLong("c_fg", cfg.foregroundHex)
            .putString("custom_pattern", cfg.patternStyle.name)
            .putString("pattern_override", cfg.patternStyle.name)
            .putFloat("c_pat_alpha", cfg.patternOpacity)
            .apply()
    }

    fun setPatternOverride(context: Context, style: IslamicPatternStyle?) {
        patternOverride.value = style
        if (style != null) {
            customTheme.value = customTheme.value.copy(patternStyle = style)
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("pattern_override", style?.name ?: "")
            .apply()
    }

    fun resolvePattern(style: AppThemeStyle, override: IslamicPatternStyle?, custom: CustomThemeConfig): IslamicPatternStyle {
        return override ?: if (style == AppThemeStyle.CUSTOM_STUDIO) custom.patternStyle else style.defaultPattern
    }
}
