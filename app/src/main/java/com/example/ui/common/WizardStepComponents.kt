package com.example.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OrnateIslamicButton

@Composable
fun WizardStepProgressHeader(
    currentStep: Int,
    totalSteps: Int,
    stepTitle: String,
    stepSubtitle: String = "",
    isArabic: Boolean
) {
    val progress = ((currentStep + 1).toFloat() / totalSteps.coerceAtLeast(1)).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) "الخطوة ${currentStep + 1} من $totalSteps" else "Step ${currentStep + 1} of $totalSteps",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = stepTitle,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        if (stepSubtitle.isNotBlank()) {
            Text(text = stepSubtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun WizardStepFooter(
    currentStep: Int,
    totalSteps: Int,
    isArabic: Boolean,
    onCancelOrBack: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit,
    finishLabel: String,
    finishTestTag: String = "wizard_finish_btn"
) {
    val isFirst = currentStep == 0
    val isLast = currentStep >= totalSteps - 1
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onCancelOrBack,
            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
        ) {
            if (!isFirst) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = if (isFirst) (if (isArabic) "إلغاء" else "Cancel") else (if (isArabic) "السابق" else "Back"),
                fontSize = 12.sp
            )
        }
        OrnateIslamicButton(
            onClick = { if (isLast) onFinish() else onNext() },
            modifier = Modifier.weight(1.4f).heightIn(min = 48.dp).testTag(if (isLast) finishTestTag else "wizard_next_btn")
        ) {
            Text(
                text = if (isLast) finishLabel else (if (isArabic) "التالي" else "Next"),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(6.dp))
            Icon(if (isLast) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(16.dp))
        }
    }
}
