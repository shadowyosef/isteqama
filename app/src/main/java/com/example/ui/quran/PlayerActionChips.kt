package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranAudioDownloadManager
import com.example.util.AppStrings
import com.example.util.AudioPlayerManager
import kotlinx.coroutines.launch

@Composable
fun PlayerActionChips(
    surahNumber: Int,
    reciterId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isContinuous by AudioPlayerManager.isContinuousMode.collectAsState()
    val isLoop by AudioPlayerManager.isSurahLoopMode.collectAsState()
    val downloadedSurahs by QuranAudioDownloadManager.downloadedSurahs.collectAsState()
    var isDownloading by remember { mutableStateOf(false) }

    val fileKey = "${reciterId}_surah_${String.format("%03d", surahNumber)}"
    val isDownloaded = downloadedSurahs.contains(fileKey) || QuranAudioDownloadManager.isSurahDownloaded(context, reciterId, surahNumber)

    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = isContinuous,
            onClick = { AudioPlayerManager.toggleContinuousMode() },
            label = { Text(if (isContinuous) AppStrings.get("مستمر ✓", "Continuous ✓") else AppStrings.get("آية بآية", "Verse"), fontSize = 11.sp) },
            leadingIcon = { Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(14.dp)) }
        )

        FilterChip(
            selected = isLoop,
            onClick = { AudioPlayerManager.toggleLoopMode() },
            label = { Text(if (isLoop) AppStrings.get("تكرار", "Loop") else AppStrings.get("مرة واحدة", "Once"), fontSize = 11.sp) },
            leadingIcon = { Icon(Icons.Default.Repeat, contentDescription = null, modifier = Modifier.size(14.dp)) }
        )

        if (isDownloading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        } else {
            IconButton(
                onClick = {
                    if (isDownloaded) {
                        QuranAudioDownloadManager.deleteDownloadedSurah(context, reciterId, surahNumber)
                    } else {
                        isDownloading = true
                        scope.launch {
                            QuranAudioDownloadManager.downloadSurahAudio(context, reciterId, surahNumber)
                            isDownloading = false
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (isDownloaded) Icons.Default.CloudDone else Icons.Default.CloudDownload,
                    contentDescription = "Offline Download",
                    tint = if (isDownloaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
