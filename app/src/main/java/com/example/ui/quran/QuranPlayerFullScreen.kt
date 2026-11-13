package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranBookmarksRepository
import com.example.data.QuranRepository
import com.example.model.ReciterListProvider
import com.example.model.SurahInfo
import com.example.util.AppStrings
import com.example.util.AudioPlayerManager
import com.example.util.QuranSleepTimerManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranPlayerFullScreen(
    surah: SurahInfo,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSurahNum by AudioPlayerManager.currentSurah.collectAsState()
    val currentAyahNum by AudioPlayerManager.currentAyah.collectAsState()
    val isPlaying by AudioPlayerManager.isPlaying.collectAsState()
    val progress by AudioPlayerManager.playbackProgress.collectAsState()
    val timerState by QuranSleepTimerManager.timerState.collectAsState()
    val bookmarks by QuranBookmarksRepository.bookmarks.collectAsState()

    val activeSurahNum = currentSurahNum ?: surah.number
    val activeSurah = remember(activeSurahNum) { QuranRepository.getSurah(activeSurahNum) ?: surah }
    val ayahs = remember(activeSurahNum) { QuranRepository.getAyahsForSurah(activeSurahNum) }
    val currentAyahObj = remember(ayahs, currentAyahNum) {
        ayahs.find { it.numberInSurah == (currentAyahNum ?: 1) } ?: ayahs.firstOrNull()
    }

    var showRecitersSheet by remember { mutableStateOf(false) }
    var showSleepTimerSheet by remember { mutableStateOf(false) }
    var showBookmarksSheet by remember { mutableStateOf(false) }
    var showStatsDashboard by remember { mutableStateOf(false) }

    val reciterInfo = remember(AudioPlayerManager.selectedReciter) {
        ReciterListProvider.getReciter(AudioPlayerManager.selectedReciter)
    }

    val isCurrentAyahBookmarked = remember(bookmarks, activeSurahNum, currentAyahObj) {
        currentAyahObj != null && QuranBookmarksRepository.isBookmarked(activeSurahNum, currentAyahObj.numberInSurah)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppStrings.get("مشغل القرآن الكريم", "Quran Player"), fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showStatsDashboard = true }) {
                        Icon(Icons.Default.BarChart, contentDescription = "Dashboard", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { showBookmarksSheet = true }) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Bookmarks", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { showSleepTimerSheet = true }) {
                        Icon(Icons.Default.Bedtime, contentDescription = "Sleep Timer", tint = if (timerState.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { showRecitersSheet = true }) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = "Reciter", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (AppStrings.isArabic) activeSurah.displayNameAr else activeSurah.displayNameEn, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = "${AppStrings.get("القارئ: ", "Reciter: ")}${if (AppStrings.isArabic) reciterInfo.nameAr else reciterInfo.nameEn}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (timerState.isActive) {
                        Text(text = "⏱ ${QuranSleepTimerManager.formatRemainingTime()}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            PlayerAyahCard(
                ayah = currentAyahObj, totalAyahs = ayahs.size, isBookmarked = isCurrentAyahBookmarked,
                onToggleBookmark = {
                    currentAyahObj?.let {
                        QuranBookmarksRepository.toggleBookmark(activeSurahNum, it.numberInSurah, it.page, activeSurah.nameAr, it.textArabic)
                    }
                }
            )

            // Dynamic Audio Waveform Visualizer
            QuranAudioWaveformVisualizer(isPlaying = isPlaying, progress = progress)

            FullPlayerControls(surahNumber = activeSurah.number, reciterId = AudioPlayerManager.selectedReciter, modifier = Modifier.padding(bottom = 10.dp))
        }
    }

    if (showRecitersSheet) ReciterSelectionSheet(onDismiss = { showRecitersSheet = false })
    if (showSleepTimerSheet) QuranSleepTimerSheet(surahNumber = activeSurah.number, surahNameAr = activeSurah.nameAr, onDismiss = { showSleepTimerSheet = false })
    if (showBookmarksSheet) QuranBookmarksSheet(onSelectAyah = { _, _ -> }, onDismiss = { showBookmarksSheet = false })
    if (showStatsDashboard) {
        QuranReadingStatsDashboard(
            surahNumber = activeSurah.number, currentAyah = currentAyahObj?.numberInSurah ?: 1,
            totalAyahsInSurah = ayahs.size, onDismiss = { showStatsDashboard = false }
        )
    }
}
