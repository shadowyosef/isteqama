package com.example.ui.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ReciterListProvider
import com.example.util.AppStrings
import com.example.util.AudioPlayerManager

@Composable
fun AudioPlayerBottomBar(
    onOpenFullPlayer: (() -> Unit)? = null
) {
    val isPlaying by AudioPlayerManager.isPlaying.collectAsState()
    val surah by AudioPlayerManager.currentSurah.collectAsState()
    val ayah by AudioPlayerManager.currentAyah.collectAsState()
    val isContinuous by AudioPlayerManager.isContinuousMode.collectAsState()
    val reciterInfo = remember(AudioPlayerManager.selectedReciter) {
        ReciterListProvider.getReciter(AudioPlayerManager.selectedReciter)
    }

    if (surah != null) {
        Surface(
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onOpenFullPlayer?.invoke() }
                .testTag("audio_player_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "${AppStrings.listen}: سورة $surah - آية ${ayah ?: 1}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (isContinuous) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
                                Text("مستمر", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    Text(
                        text = "${AppStrings.get("القارئ: ", "Reciter: ")}${if (AppStrings.isArabic) reciterInfo.nameAr else reciterInfo.nameEn}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onOpenFullPlayer != null) {
                        IconButton(onClick = onOpenFullPlayer) {
                            Icon(Icons.Default.ExpandLess, contentDescription = "Full Player", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    IconButton(
                        onClick = { AudioPlayerManager.togglePlayPause() },
                        modifier = Modifier.testTag("audio_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) AppStrings.pause else AppStrings.play,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { AudioPlayerManager.playNextAyah() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { AudioPlayerManager.stop() },
                        modifier = Modifier.testTag("audio_stop_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
