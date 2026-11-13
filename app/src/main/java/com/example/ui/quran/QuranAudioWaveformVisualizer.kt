package com.example.ui.quran

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun QuranAudioWaveformVisualizer(
    isPlaying: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val animatedPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isPlaying) 6.28f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
    val highlightGold = Color(0xFFD4AF37)

    Canvas(modifier = modifier.fillMaxWidth().height(48.dp)) {
        val totalBars = 32
        val spacing = size.width / totalBars
        val barWidth = (spacing * 0.55f).coerceAtLeast(3f)
        val centerY = size.height / 2f

        for (i in 0 until totalBars) {
            val barFraction = i.toFloat() / totalBars
            val isPassed = barFraction <= progress

            // Wave oscillation math based on index and animatedPhase
            val sineWave = sin(i * 0.45f + animatedPhase)
            val baseHeightFactor = (0.25f + 0.55f * sin((i * 3.14f) / totalBars)).coerceIn(0.15f, 1f)
            val dynamicScale = if (isPlaying) {
                (baseHeightFactor * (0.5f + 0.5f * sineWave)).coerceIn(0.12f, 1f)
            } else {
                baseHeightFactor * 0.35f
            }

            val barHeight = (size.height * 0.85f * dynamicScale).coerceAtLeast(4f)
            val left = i * spacing + (spacing - barWidth) / 2f
            val top = centerY - barHeight / 2f

            val barColor = when {
                isPassed && isPlaying -> highlightGold
                isPassed -> activeColor
                else -> inactiveColor
            }

            drawRoundRect(
                color = barColor,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
