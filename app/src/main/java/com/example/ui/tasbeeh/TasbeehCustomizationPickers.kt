package com.example.ui.tasbeeh

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*

@Composable
fun TasbeehColorPickerRow(selected: TasbeehThemeColor, onSelect: (TasbeehThemeColor) -> Unit) {
    val colors = listOf(
        TasbeehThemeColor.EMERALD to Color(0xFF2E7D32),
        TasbeehThemeColor.GOLD to Color(0xFFD4AF37),
        TasbeehThemeColor.OCEAN to Color(0xFF1976D2),
        TasbeehThemeColor.PURPLE to Color(0xFF8E24AA),
        TasbeehThemeColor.ROSE to Color(0xFFE91E63)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(vertical = 4.dp)) {
        colors.forEach { (theme, color) ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (selected == theme) 3.dp else 1.dp,
                        color = if (selected == theme) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
                    .clickable { onSelect(theme) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TasbeehShapePickerRow(selected: TasbeehCardShape, isArabic: Boolean, onSelect: (TasbeehCardShape) -> Unit) {
    val shapes = listOf(
        TasbeehCardShape.PILL to if (isArabic) "كبسولة (Pill)" else "Pill",
        TasbeehCardShape.ROUNDED to if (isArabic) "دائري الحواف" else "Rounded",
        TasbeehCardShape.SLEEK to if (isArabic) "حواف حادة" else "Sleek"
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        shapes.forEach { (shape, label) ->
            FilterChip(
                selected = selected == shape,
                onClick = { onSelect(shape) },
                label = { Text(label, fontSize = 11.sp) },
                modifier = Modifier.height(30.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TasbeehDurationPickerRow(selectedSeconds: Int, isArabic: Boolean, onSelect: (Int) -> Unit) {
    val durations = listOf(5 to "5s", 10 to if (isArabic) "10 ث (افتراضي)" else "10s (default)", 15 to "15s", 30 to "30s", 60 to "60s")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        durations.forEach { (sec, label) ->
            FilterChip(
                selected = selectedSeconds == sec,
                onClick = { onSelect(sec) },
                label = { Text(label, fontSize = 11.sp) },
                modifier = Modifier.height(30.dp)
            )
        }
    }
}
