package com.example.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import kotlinx.coroutines.delay

@Composable
fun ActiveReadingTracker(
    durationMinutes: Int,
    lastInteractionMs: Long,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val verificationMode by AppPreferencesManager.quranVerificationMode.collectAsState()
    val totalSeconds = remember(durationMinutes) { durationMinutes * 60 }
    var remainingSeconds by remember { mutableStateOf(totalSeconds) }
    var isActiveReading by remember { mutableStateOf(true) }

    LaunchedEffect(durationMinutes) {
        while (remainingSeconds > 0) {
            delay(1000)
            val now = System.currentTimeMillis()
            val timeSinceTouch = now - lastInteractionMs
            val isIdle = verificationMode == "INTERACTION_TRACK" && timeSinceTouch > 15000
            isActiveReading = !isIdle
            if (!isIdle) {
                remainingSeconds -= 1
            }
        }
        onComplete()
    }

    val progress = ((totalSeconds - remainingSeconds).toFloat() / totalSeconds.coerceAtLeast(1)).coerceIn(0f, 1f)
    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", mins, secs)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isActiveReading) Icons.Default.HourglassBottom else Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = if (isActiveReading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isActiveReading) (if (isArabic) "جاري التحقق من القراءة النشطة" else "Active Reading Verified")
                        else (if (isArabic) "يرجى التفاعل ومتابعة القراءة!" else "Please interact to resume timer!"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActiveReading) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                    )
                }
                Text(text = timeFormatted, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
            )
        }
    }
}
