package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AppStrings
import com.example.util.AudioPlayerManager

@Composable
fun FullPlayerControls(
    surahNumber: Int,
    reciterId: String,
    modifier: Modifier = Modifier
) {
    val isPlaying by AudioPlayerManager.isPlaying.collectAsState()
    val currentSpeed by AudioPlayerManager.playbackSpeed.collectAsState()

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        PlayerActionChips(surahNumber = surahNumber, reciterId = reciterId)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { AudioPlayerManager.playPreviousAyah() }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(32.dp))
            }

            FilledIconButton(
                onClick = { AudioPlayerManager.togglePlayPause() },
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(onClick = { AudioPlayerManager.playNextAyah() }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(AppStrings.get("السرعة: ", "Speed: "), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                TextButton(
                    onClick = { AudioPlayerManager.setSpeed(speed) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (currentSpeed == speed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("${speed}x", fontWeight = if (currentSpeed == speed) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
                }
            }
        }
    }
}
