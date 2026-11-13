package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AppStrings

@Composable
fun QuranPageLockToolbar(
    surahTitle: String,
    juzNumber: Int,
    pageNumber: Int,
    fontSize: Float,
    isDarkTheme: Boolean,
    isPlayingAudio: Boolean,
    isLoadingAudio: Boolean,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleAudio: () -> Unit,
    onResetToStart: () -> Unit,
    onEmergencyBypass: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1E293B)
    val subTextColor = if (isDarkTheme) Color(0xFFB0BEC5) else Color(0xFF64748B)
    val btnTint = if (isDarkTheme) Color(0xFFFFD54F) else Color(0xFFB45309)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (isDarkTheme) Color(0xFF141E18) else Color(0xFFF1EFE6),
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = surahTitle.ifBlank { "القرآن الكريم" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = btnTint
                    )
                    Text(
                        text = "${AppStrings.get("الجزء", "Juz")} $juzNumber • ${AppStrings.get("صفحة", "Page")} $pageNumber / 604",
                        fontSize = 11.sp,
                        color = subTextColor
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Audio Play/Pause button
                    IconButton(
                        onClick = onToggleAudio,
                        modifier = Modifier.size(34.dp).testTag("quran_lock_audio_btn")
                    ) {
                        if (isLoadingAudio) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = btnTint, strokeWidth = 2.dp)
                        } else {
                            Icon(
                                if (isPlayingAudio) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = "Play Quran",
                                tint = btnTint,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Theme toggle
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier.size(34.dp).testTag("quran_lock_theme_btn")
                    ) {
                        Icon(
                            if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Light/Dark",
                            tint = btnTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Zoom Out
                    FilledTonalIconButton(
                        onClick = onZoomOut,
                        enabled = fontSize > 13f,
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape
                    ) {
                        Text("A-", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }

                    // Zoom In
                    FilledTonalIconButton(
                        onClick = onZoomIn,
                        enabled = fontSize < 30f,
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape
                    ) {
                        Text("A+", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }

                    // Emergency Bypass
                    IconButton(
                        onClick = onEmergencyBypass,
                        modifier = Modifier.size(32.dp).testTag("quran_lock_emergency_btn")
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Emergency", tint = Color(0xFFFF9800), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
