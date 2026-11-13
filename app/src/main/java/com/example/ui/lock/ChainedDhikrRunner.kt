package com.example.ui.lock

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.model.RoutineStepItem

@Composable
fun ChainedDhikrRunner(
    steps: List<RoutineStepItem>,
    onAllCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val haptic = LocalHapticFeedback.current
    var currentStepIndex by remember { mutableStateOf(0) }
    var currentCount by remember { mutableStateOf(0) }

    val activeStep = steps.getOrNull(currentStepIndex) ?: RoutineStepItem(textAr = "سُبْحَانَ اللَّهِ", targetCount = 33)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${if (isArabic) "الذكر الحالي" else "Step"} (${currentStepIndex + 1} / ${steps.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            AnimatedContent(targetState = activeStep, label = "step_text") { step ->
                Text(
                    text = if (isArabic || step.textEn.isBlank()) step.textAr else step.textEn,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            val context = androidx.compose.ui.platform.LocalContext.current
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        val next = currentCount + 1
                        val isGoal = next >= activeStep.targetCount
                        com.example.util.TasbeehHapticsHelper.performCountFeedback(context, isGoal)
                        if (isGoal) {
                            if (currentStepIndex + 1 < steps.size) {
                                currentStepIndex += 1
                                currentCount = 0
                            } else {
                                onAllCompleted()
                            }
                        } else {
                            currentCount = next
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$currentCount", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    Text(text = "/ ${activeStep.targetCount}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                }
            }

            LinearProgressIndicator(
                progress = { (currentCount.toFloat() / activeStep.targetCount.coerceAtLeast(1)).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
            )
        }
    }
}
