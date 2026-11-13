package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

enum class AppFontStyle(val labelAr: String, val labelEn: String) {
    DEFAULT("الافتراضي", "Default"),
    AMIRI("أميري التراثي", "Amiri"),
    UTHMANI("العثماني (المصحف)", "Mushaf Script"),
    CAIRO("كايرو العصري", "Cairo")
}

val AmiriFontFamily = FontFamily(Font(R.font.amiri, FontWeight.Normal))
val UthmaniFontFamily = FontFamily(Font(R.font.scheherazade_new, FontWeight.Normal))
val CairoFontFamily = FontFamily(Font(R.font.cairo, FontWeight.Normal))

fun getFontFamily(style: AppFontStyle): FontFamily = when (style) {
    AppFontStyle.DEFAULT -> FontFamily.Default
    AppFontStyle.AMIRI -> AmiriFontFamily
    AppFontStyle.UTHMANI -> UthmaniFontFamily
    AppFontStyle.CAIRO -> CairoFontFamily
}

fun buildAppTypography(style: AppFontStyle = AppFontStyle.DEFAULT): Typography {
    val family = getFontFamily(style)
    return Typography(
        displayLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 36.sp),
        headlineMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 24.sp),
        titleLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.Bold, fontSize = 20.sp),
        titleMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
        titleSmall = TextStyle(fontFamily = family, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
        bodyLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 26.sp),
        bodyMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 22.sp),
        bodySmall = TextStyle(fontFamily = family, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp),
        labelLarge = TextStyle(fontFamily = family, fontWeight = FontWeight.Medium, fontSize = 14.sp),
        labelMedium = TextStyle(fontFamily = family, fontWeight = FontWeight.Medium, fontSize = 12.sp),
        labelSmall = TextStyle(fontFamily = family, fontWeight = FontWeight.Medium, fontSize = 11.sp)
    )
}

val Typography = buildAppTypography(AppFontStyle.DEFAULT)
