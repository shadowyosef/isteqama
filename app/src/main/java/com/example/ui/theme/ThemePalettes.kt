package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class IslamicPatternStyle(val labelAr: String, val labelEn: String) {
    NONE("بدون زخرفة", "None"),
    STAR_ROSETTE("نجمة ثمانية إسلامية", "8-Point Star"),
    ANDALUS_LATTICE("شبكة أندلسية", "Andalus Lattice"),
    MIHRAB_ARCH("عقود المحراب", "Mihrab Arches")
}

data class CustomThemeConfig(
    val primaryHex: Long = 0xFF00796B,
    val secondaryHex: Long = 0xFFE69500,
    val backgroundHex: Long = 0xFFF2FBF9,
    val surfaceHex: Long = 0xFFFFFFFF,
    val foregroundHex: Long = 0xFF0D2824,
    val patternStyle: IslamicPatternStyle = IslamicPatternStyle.STAR_ROSETTE,
    val patternOpacity: Float = 0.18f
)

enum class AppThemeStyle(val labelAr: String, val labelEn: String, val defaultPattern: IslamicPatternStyle) {
    ANDALUS_ORNATE("الزخرف الأندلسي", "Andalus Ornate", IslamicPatternStyle.ANDALUS_LATTICE),
    MADINAH_ROYAL("روضة المدينة", "Madinah Royal", IslamicPatternStyle.STAR_ROSETTE),
    ISTAQAMA("استقامة زمردي", "Istaqama", IslamicPatternStyle.STAR_ROSETTE),
    ROYAL_GOLD("ذهبي ملكي", "Royal Gold", IslamicPatternStyle.MIHRAB_ARCH),
    NOOR("نور سماوي", "Noor Sky", IslamicPatternStyle.MIHRAB_ARCH),
    WARQAA("ورقاء دافئ", "Warqaa Warm", IslamicPatternStyle.ANDALUS_LATTICE),
    SOFT_ROSE("وردية زاهية", "Vibrant Rose", IslamicPatternStyle.STAR_ROSETTE),
    CUSTOM_STUDIO("ثيم مخصص يدوي", "Custom Studio", IslamicPatternStyle.STAR_ROSETTE)
}

object ThemePalettes {
    // 1. Andalus Ornate (Teal Jewel + Warm Saffron Gold + Mint Ivory)
    val AndalusPrimary = Color(0xFF00796B)
    val AndalusSecondary = Color(0xFFD97706)
    val AndalusAccent = Color(0xFF14B8A6)
    val AndalusLightBg = Color(0xFFF0FAF8)
    val AndalusLightSurface = Color(0xFFFFFFFF)
    val AndalusLightSurfaceVar = Color(0xFFD7F2EC)
    val AndalusDarkBg = Color(0xFF061B1A)
    val AndalusDarkSurface = Color(0xFF0E2B29)
    val AndalusDarkSurfaceVar = Color(0xFF163D3A)

    // 2. Madinah Royal (Imperial Emerald + Ruby Crimson + Burnished Gold)
    val MadinahPrimary = Color(0xFF047857)
    val MadinahSecondary = Color(0xFFBE123C)
    val MadinahAccent = Color(0xFFF59E0B)
    val MadinahLightBg = Color(0xFFFFFBF2)
    val MadinahLightSurface = Color(0xFFFFFFFF)
    val MadinahLightSurfaceVar = Color(0xFFFCEFD7)
    val MadinahDarkBg = Color(0xFF0B1C14)
    val MadinahDarkSurface = Color(0xFF132C20)
    val MadinahDarkSurfaceVar = Color(0xFF1D4030)

    // Upgraded vivid presets so all buttons & containers pop cleanly
    val IstaqamaPrimary = Color(0xFF059669)
    val IstaqamaSecondary = Color(0xFF0284C7)
    val IstaqamaAccent = Color(0xFF34D399)
    val IstaqamaLightBg = Color(0xFFF2FBF7)
    val IstaqamaDarkBg = Color(0xFF091B15)
    val IstaqamaDarkSurface = Color(0xFF122C22)

    val WarqaaPrimary = Color(0xFF8D4E2A)
    val WarqaaSecondary = Color(0xFFD97706)
    val WarqaaAccent = Color(0xFFF59E0B)
    val WarqaaLightBg = Color(0xFFFAF5EE)
    val WarqaaDarkBg = Color(0xFF1C1410)
    val WarqaaDarkSurface = Color(0xFF2B201B)

    val NoorPrimary = Color(0xFF0284C7)
    val NoorSecondary = Color(0xFF6366F1)
    val NoorAccent = Color(0xFF38BDF8)
    val NoorLightBg = Color(0xFFF0F7FF)
    val NoorDarkBg = Color(0xFF081120)
    val NoorDarkSurface = Color(0xFF102038)

    val RoyalPrimary = Color(0xFFB45309)
    val RoyalSecondary = Color(0xFF047857)
    val RoyalAccent = Color(0xFFFBBF24)
    val RoyalLightBg = Color(0xFFFFFBEB)
    val RoyalDarkBg = Color(0xFF1A1305)
    val RoyalDarkSurface = Color(0xFF291F0B)

    val RosePrimary = Color(0xFFBE185D)
    val RoseSecondary = Color(0xFF7C3AED)
    val RoseAccent = Color(0xFFF472B6)
    val RoseLightBg = Color(0xFFFFF1F6)
    val RoseDarkBg = Color(0xFF220B15)
    val RoseDarkSurface = Color(0xFF351322)
}
