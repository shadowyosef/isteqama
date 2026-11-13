package com.example.ui.unified

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.CountdownUtils
import kotlinx.coroutines.delay

@Composable
fun CountdownBadge(
    remainingMillisProvider: () -> Long,
    isArabic: Boolean,
    isEnabled: Boolean = true,
    accentColor: Color? = null,
    modifier: Modifier = Modifier
) {
    var tick by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(isEnabled) {
        if (!isEnabled) return@LaunchedEffect
        while (true) {
            delay(1000L)
            tick = System.currentTimeMillis()
        }
    }

    val remaining = remember(tick, isEnabled) { if (isEnabled) remainingMillisProvider() else -1L }
    val label = remember(remaining, isArabic) { CountdownUtils.formatCountdown(remaining, isArabic) }

    val bgColor = (accentColor ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.12f)
    val contentColor = accentColor ?: MaterialTheme.colorScheme.primary

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}
