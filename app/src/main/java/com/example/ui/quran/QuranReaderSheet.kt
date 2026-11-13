package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.model.*
import com.example.util.AudioPlayerManager
import kotlinx.coroutines.delay

@Composable
fun QuranReaderSheet(
    surah: SurahInfo,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val ayahs = remember(surah.number) { QuranRepository.getAyahsForSurah(surah.number) }
    var selectedAyahForTafseer by remember { mutableStateOf<Ayah?>(null) }
    var fontSizeSp by remember { mutableStateOf(SmartAlertPreferences.quranFontSizeSp.value) }
    var isAutoScrolling by remember { mutableStateOf(false) }
    var viewMode by remember { mutableStateOf(SmartAlertPreferences.quranViewMode.value) }
    var showReciterSheet by remember { mutableStateOf(false) }
    var showRiwayahSheet by remember { mutableStateOf(false) }
    var showFullPlayer by remember { mutableStateOf(false) }
    var showSurahInfo by remember { mutableStateOf(false) }
    var showHifzStudio by remember { mutableStateOf(false) }
    var selectedAyahReflection by remember { mutableStateOf<Ayah?>(null) }
    LaunchedEffect(Unit) { HifzLoopAndReflectionRepository.init(context) }
    val playingSurah by AudioPlayerManager.currentSurah.collectAsState()
    val playingAyah by AudioPlayerManager.currentAyah.collectAsState()
    val isBookmarked = remember(surah.number) { QuranBookmarksRepository.bookmarks.value.any { it.surahNumber == surah.number } }
    val listState = rememberLazyListState()

    LaunchedEffect(playingSurah, playingAyah) {
        if (playingSurah == surah.number && playingAyah != null) {
            val idx = ayahs.indexOfFirst { it.numberInSurah == playingAyah }
            if (idx >= 0) listState.animateScrollToItem((idx + 1).coerceAtMost(ayahs.size))
        }
    }

    LaunchedEffect(isAutoScrolling) {
        while (isAutoScrolling) {
            delay(1200)
            listState.animateScrollToItem((listState.firstVisibleItemIndex + 1).coerceAtMost(ayahs.size - 1))
        }
    }

    if (showFullPlayer) {
        QuranPlayerFullScreen(surah = surah, onBack = { showFullPlayer = false })
        return
    }

    Scaffold(
        topBar = {
            QuranReaderTopBar(
                surah = surah,
                viewMode = viewMode,
                isBookmarked = isBookmarked,
                isAutoScrolling = isAutoScrolling,
                onBack = onBack,
                onToggleViewMode = {
                    val next = if (viewMode == QuranViewMode.VERSE_LIST) QuranViewMode.MUSHAF_PAGE else QuranViewMode.VERSE_LIST
                    viewMode = next
                    SmartAlertPreferences.quranViewMode.value = next
                },
                onToggleBookmark = {
                    QuranBookmarksRepository.toggleBookmark(surah.number, 1, surah.pageStart, surah.nameAr)
                },
                onOpenReciterSheet = { showReciterSheet = true },
                onOpenRiwayahSheet = { showRiwayahSheet = true },
                onOpenFullPlayer = { showFullPlayer = true },
                onOpenSurahInfo = { showSurahInfo = true },
                onToggleAutoScroll = { isAutoScrolling = !isAutoScrolling },
                onIncreaseFontSize = { if (fontSizeSp < 36f) fontSizeSp += 2f },
                onDecreaseFontSize = { if (fontSizeSp > 16f) fontSizeSp -= 2f },
                onOpenHifzStudio = { showHifzStudio = true }
            )
        },
        bottomBar = { AudioPlayerBottomBar(onOpenFullPlayer = { showFullPlayer = true }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (viewMode == QuranViewMode.MUSHAF_PAGE) {
                MushafPageViewer(
                    surah = surah,
                    ayahs = ayahs,
                    fontSizeSp = fontSizeSp,
                    playingAyahNumber = if (playingSurah == surah.number) playingAyah else null,
                    onSelectAyahForTafseer = { selectedAyahForTafseer = it }
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = (fontSizeSp + 2).sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(ayahs) { ayah ->
                        AyahCard(
                            ayah = ayah,
                            fontSizeSp = fontSizeSp,
                            isPlayingNow = (playingSurah == ayah.surahNumber && playingAyah == ayah.numberInSurah),
                            onPlayAudio = {
                                AudioPlayerManager.playAyah(context, ayah.surahNumber, ayah.numberInSurah, ayah.globalNumber)
                                StatsRepository.recordAyahsRead(1)
                            },
                            onOpenTafseer = { selectedAyahForTafseer = ayah },
                            onOpenReflection = { selectedAyahReflection = ayah }
                        )
                    }
                }
            }
        }
    }

    selectedAyahForTafseer?.let {
        TafseerModal(ayah = it, onDismiss = { selectedAyahForTafseer = null })
    }
    if (showReciterSheet) {
        ReciterSelectionSheet(onDismiss = { showReciterSheet = false })
    }
    if (showRiwayahSheet) {
        RiwayahSelectionSheet(onDismiss = { showRiwayahSheet = false })
    }
    if (showSurahInfo) SurahInfoDialog(surah = surah, onDismiss = { showSurahInfo = false })
    if (showHifzStudio) HifzPracticeDialog(ayahs = ayahs, onDismiss = { showHifzStudio = false })
    selectedAyahReflection?.let { AyahReflectionDialog(ayah = it, onDismiss = { selectedAyahReflection = null }) }
}
