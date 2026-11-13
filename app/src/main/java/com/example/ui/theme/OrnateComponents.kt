package com.example.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.AppAppearancePreferences

@Composable
fun OrnateIslamicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    accentColor: Color = MaterialTheme.colorScheme.secondary,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val currentTheme by AppAppearancePreferences.themeStyle.collectAsState()
    val customTheme by AppAppearancePreferences.customTheme.collectAsState()
    val patOverride by AppAppearancePreferences.patternOverride.collectAsState()
    val activePattern = AppAppearancePreferences.resolvePattern(currentTheme, patOverride, customTheme)

    val topStop = lerp(containerColor, primary, 0.08f).copy(alpha = 1f)
    val bottomStop = lerp(containerColor, accentColor, 0.07f).copy(alpha = 1f)
    val shape = RoundedCornerShape(cornerRadius)

    Surface(
        shape = shape,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp,
        color = containerColor,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .islamicFloralBorder(primary = primary, goldAccent = accentColor, cornerRadius = cornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(topStop, bottomStop)))
                .islamicGeometricPattern(primary = primary, secondary = accentColor, patternStyle = activePattern, alpha = 0.11f),
            content = content
        )
    }
}

@Composable
fun OrnateIslamicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cornerRadius: Dp = 14.dp,
    content: @Composable RowScope.() -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val gradStart = lerp(primary, secondary, 0.18f)
    val shape = RoundedCornerShape(cornerRadius)

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(shape)
            .background(if (enabled) Brush.horizontalGradient(listOf(primary, gradStart)) else Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant)), shape)
            .arabesqueButtonRim(rimColor = secondary.copy(alpha = 0.9f), cornerRadius = cornerRadius),
        content = content
    )
}

@Composable
fun OrnateIslamicTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 14.dp,
    content: @Composable RowScope.() -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val shape = RoundedCornerShape(cornerRadius)

    FilledTonalButton(
        onClick = onClick,
        shape = shape,
        border = BorderStroke(1.dp, secondary.copy(alpha = 0.5f)),
        modifier = modifier
            .heightIn(min = 48.dp)
            .arabesqueButtonRim(rimColor = primary.copy(alpha = 0.45f), cornerRadius = cornerRadius),
        content = content
    )
}

@Composable
fun IslamicSectionDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary
) {
    val primary = MaterialTheme.colorScheme.primary
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(18.dp)) {
            val midY = size.height / 2f
            val midX = size.width / 2f
            drawLine(color.copy(alpha = 0.45f), Offset(12f, midY), Offset(midX - 22f, midY), strokeWidth = 2f)
            drawLine(color.copy(alpha = 0.45f), Offset(midX + 22f, midY), Offset(size.width - 12f, midY), strokeWidth = 2f)
            drawKhatamStar(Offset(midX, midY), radius = 11f, strokeColor = primary.copy(alpha = 0.85f), strokeWidth = 1.6f)
            drawCircle(color.copy(alpha = 0.9f), radius = 3.5f, center = Offset(midX - 22f, midY))
            drawCircle(color.copy(alpha = 0.9f), radius = 3.5f, center = Offset(midX + 22f, midY))
        }
    }
}
