package com.example.ui.tasbeeh

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AppStrings

@Composable
fun TasbeehBeadsRing(
    count: Int,
    onTap: () -> Unit
) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        val ringSize = minOf(maxWidth, maxHeight, 250.dp)
        Surface(
            modifier = Modifier
                .size(ringSize)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTap
                )
                .testTag("tasbeeh_bead_button"),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$count",
                        fontSize = 54.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = AppStrings.get("اضغط للتسبيح", "Tap to count"),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
