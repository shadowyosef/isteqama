package com.example.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp

@Composable
fun IslamicOrnamentBackground(
    patternStyle: IslamicPatternStyle,
    patternOpacity: Float,
    modifier: Modifier = Modifier
) {
    val bgColor = MaterialTheme.colorScheme.background.copy(alpha = 1f)
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    val stopTop = bgColor
    val stopMid1 = lerp(bgColor, primary, 0.06f)
    val stopMid2 = lerp(bgColor, secondary, 0.05f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .background(Brush.verticalGradient(listOf(stopTop, stopMid1, stopMid2, stopTop)))
    ) {
        if (patternStyle != IslamicPatternStyle.NONE && patternOpacity > 0.005f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 44.dp.toPx().coerceAtLeast(48f)
                val strokePx = 1.25.dp.toPx()
                val alpha = patternOpacity.coerceIn(0.14f, 0.34f)
                val c1 = primary.copy(alpha = alpha)
                val c2 = secondary.copy(alpha = alpha * 0.95f)

                var y = step / 2f
                while (y < size.height + step) {
                    var x = step / 2f
                    while (x < size.width + step) {
                        drawGeometricTile(patternStyle, Offset(x, y), step, c1, c2, strokePx)
                        x += step
                    }
                    y += step
                }
                val cornerSpan = 36.dp.toPx()
                drawCornerFloralFiligree(Offset(0f, 0f), 1f, 1f, secondary, cornerSpan)
                drawCornerFloralFiligree(Offset(size.width, 0f), -1f, 1f, secondary, cornerSpan)
                drawCornerFloralFiligree(Offset(0f, size.height), 1f, -1f, secondary, cornerSpan)
                drawCornerFloralFiligree(Offset(size.width, size.height), -1f, -1f, secondary, cornerSpan)
            }
        }
    }
}
