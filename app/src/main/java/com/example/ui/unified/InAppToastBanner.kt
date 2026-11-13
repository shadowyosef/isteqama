package com.example.ui.unified

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.util.AppStrings

@Composable
fun InAppToastBanner(
    toast: TasbeehScheduleModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = AppStrings.isArabic
    val accentColor = when (toast.themeColor) {
        TasbeehThemeColor.GOLD -> Color(0xFFD4AF37)
        TasbeehThemeColor.OCEAN -> Color(0xFF1976D2)
        TasbeehThemeColor.PURPLE -> Color(0xFF8E24AA)
        TasbeehThemeColor.ROSE -> Color(0xFFE91E63)
        else -> Color(0xFF2E7D32)
    }
    val shape = when (toast.cardShape) {
        TasbeehCardShape.PILL -> CircleShape
        TasbeehCardShape.SLEEK -> RoundedCornerShape(8.dp)
        else -> RoundedCornerShape(20.dp)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(2.dp, accentColor, shape)
            .background(Color(0xF00A1F18), shape)
            .clickable { onDismiss() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isAr) toast.phraseAr else toast.phraseEn,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${toast.targetCount}x  •  ${if (isAr) "اضغط للإخفاء (تختفي تلقائياً)" else "Tap to dismiss (auto)"}",
                    color = accentColor,
                    fontSize = 11.sp
                )
            }
            Badge(containerColor = accentColor) {
                Text("${toast.durationSeconds}s", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(2.dp))
            }
        }
    }
}
