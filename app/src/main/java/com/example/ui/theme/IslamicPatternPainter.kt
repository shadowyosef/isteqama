package com.example.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun DrawScope.drawKhatamStar(center: Offset, radius: Float, strokeColor: Color, strokeWidth: Float = 2f) {
    val half = radius * 0.72f
    val stroke = Stroke(width = strokeWidth)
    drawRect(strokeColor, Offset(center.x - half, center.y - half), Size(half * 2, half * 2), style = stroke)
    rotate(45f, center) {
        drawRect(strokeColor, Offset(center.x - half, center.y - half), Size(half * 2, half * 2), style = stroke)
    }
    drawCircle(strokeColor, radius = radius * 0.24f, center = center, style = stroke)
}

fun DrawScope.drawGeometricTile(style: IslamicPatternStyle, center: Offset, step: Float, c1: Color, c2: Color, strokePx: Float) {
    val stroke = Stroke(width = strokePx)
    when (style) {
        IslamicPatternStyle.STAR_ROSETTE -> {
            drawKhatamStar(center, radius = step * 0.36f, strokeColor = c1, strokeWidth = strokePx)
            drawCircle(c2, radius = step * 0.44f, center = center, style = Stroke(width = strokePx * 0.75f))
            drawCircle(c2, radius = step * 0.06f, center = Offset(center.x + step / 2f, center.y + step / 2f))
        }
        IslamicPatternStyle.ANDALUS_LATTICE -> {
            val r = step * 0.46f
            val diamond = Path().apply {
                moveTo(center.x, center.y - r); lineTo(center.x + r, center.y)
                lineTo(center.x, center.y + r); lineTo(center.x - r, center.y); close()
            }
            drawPath(diamond, c1, style = stroke)
            drawKhatamStar(center, radius = r * 0.56f, strokeColor = c2, strokeWidth = strokePx * 0.9f)
        }
        IslamicPatternStyle.MIHRAB_ARCH -> {
            val w = step * 0.58f
            val h = step * 0.72f
            val arch = Path().apply {
                moveTo(center.x - w / 2f, center.y + h / 2f); lineTo(center.x - w / 2f, center.y)
                quadraticTo(center.x - w / 2f, center.y - h / 2f, center.x, center.y - h * 0.64f)
                quadraticTo(center.x + w / 2f, center.y - h / 2f, center.x + w / 2f, center.y)
                lineTo(center.x + w / 2f, center.y + h / 2f); close()
            }
            drawPath(arch, c1, style = stroke)
            drawCircle(c2, radius = strokePx * 1.4f, center = Offset(center.x, center.y - h * 0.24f))
        }
        IslamicPatternStyle.NONE -> {}
    }
}

fun DrawScope.drawCornerFloralFiligree(corner: Offset, signX: Float, signY: Float, accent: Color, span: Float = 38f) {
    val petal = Path().apply {
        moveTo(corner.x + signX * 6f, corner.y + signY * 6f)
        quadraticTo(corner.x + signX * span * 0.65f, corner.y + signY * 4f, corner.x + signX * span, corner.y + signY * 14f)
        quadraticTo(corner.x + signX * span * 0.55f, corner.y + signY * 22f, corner.x + signX * 14f, corner.y + signY * span * 0.55f)
        quadraticTo(corner.x + signX * 4f, corner.y + signY * span * 0.65f, corner.x + signX * 6f, corner.y + signY * 6f)
        close()
    }
    drawPath(petal, accent.copy(alpha = 0.24f))
    drawPath(petal, accent.copy(alpha = 0.72f), style = Stroke(width = 1.6f))
    drawCircle(accent.copy(alpha = 0.85f), radius = 3.5f, center = Offset(corner.x + signX * 11f, corner.y + signY * 11f))
}

fun Modifier.islamicGeometricPattern(
    primary: Color,
    secondary: Color,
    stepPx: Float = 84f,
    alpha: Float = 0.13f,
    patternStyle: IslamicPatternStyle = IslamicPatternStyle.STAR_ROSETTE
): Modifier = this.drawBehind {
    if (patternStyle == IslamicPatternStyle.NONE) return@drawBehind
    val actualStep = (34.dp.toPx() * (stepPx / 84f)).coerceAtLeast(36f)
    val strokePx = 1.05.dp.toPx()
    val c1 = primary.copy(alpha = alpha)
    val c2 = secondary.copy(alpha = alpha * 0.9f)
    var y = actualStep / 2f
    while (y < size.height + actualStep) {
        var x = actualStep / 2f
        while (x < size.width + actualStep) {
            drawGeometricTile(patternStyle, Offset(x, y), actualStep, c1, c2, strokePx)
            x += actualStep
        }
        y += actualStep
    }
}

fun Modifier.islamicFloralBorder(
    primary: Color,
    goldAccent: Color,
    cornerRadius: Dp = 20.dp,
    strokeWidth: Dp = 1.4.dp
): Modifier = this.drawWithContent {
    drawContent()
    val r = cornerRadius.toPx()
    val sw = strokeWidth.toPx()
    drawRoundRect(
        color = goldAccent.copy(alpha = 0.58f), topLeft = Offset(sw, sw),
        size = Size((size.width - sw * 2).coerceAtLeast(0f), (size.height - sw * 2).coerceAtLeast(0f)),
        cornerRadius = CornerRadius(r, r), style = Stroke(width = sw)
    )
    val span = 14.dp.toPx().coerceAtMost(size.minDimension * 0.3f)
    drawCornerFloralFiligree(Offset(4f, 4f), 1f, 1f, goldAccent, span)
    drawCornerFloralFiligree(Offset(size.width - 4f, 4f), -1f, 1f, goldAccent, span)
    drawCornerFloralFiligree(Offset(4f, size.height - 4f), 1f, -1f, goldAccent, span)
    drawCornerFloralFiligree(Offset(size.width - 4f, size.height - 4f), -1f, -1f, goldAccent, span)
}

fun Modifier.arabesqueButtonRim(rimColor: Color, cornerRadius: Dp = 14.dp): Modifier = this.drawWithContent {
    drawContent()
    val r = cornerRadius.toPx()
    val inset = 3.5f
    drawRoundRect(
        color = rimColor.copy(alpha = 0.7f), topLeft = Offset(inset, inset),
        size = Size((size.width - inset * 2).coerceAtLeast(0f), (size.height - inset * 2).coerceAtLeast(0f)),
        cornerRadius = CornerRadius((r - 2f).coerceAtLeast(4f), (r - 2f).coerceAtLeast(4f)), style = Stroke(width = 1.25f)
    )
    val d = 5f
    listOf(Offset(10f, size.height / 2f), Offset(size.width - 10f, size.height / 2f)).forEach { pt ->
        val diamond = Path().apply {
            moveTo(pt.x, pt.y - d); lineTo(pt.x + d, pt.y); lineTo(pt.x, pt.y + d); lineTo(pt.x - d, pt.y); close()
        }
        drawPath(diamond, rimColor.copy(alpha = 0.85f))
    }
}
