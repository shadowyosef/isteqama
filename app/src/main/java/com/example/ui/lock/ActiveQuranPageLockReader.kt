package com.example.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.QuranRepository
import com.example.model.LockContentConfig
import com.example.util.QuranLockPreferences
import com.example.util.QuranPageAudioPlayer

@Composable
fun ActiveQuranPageLockReader(
    config: LockContentConfig,
    onFinish: () -> Unit,
    onResetToStart: (() -> Unit)? = null,
    onEmergencyBypass: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalPages = config.pagesPerSession.coerceAtLeast(1)
    var pageOffset by remember { mutableIntStateOf(0) }
    var basePage by remember(config.currentProgressPage) { mutableIntStateOf(config.currentProgressPage.coerceIn(1, 604)) }
    val currentPage = (basePage + pageOffset).coerceIn(1, 604)

    var isDarkTheme by remember { mutableStateOf(QuranLockPreferences.isDarkTheme(context)) }
    var userFontSize by remember { mutableFloatStateOf(QuranLockPreferences.getFontSize(context)) }
    var selectedAyah by remember(currentPage) { mutableIntStateOf(QuranLockPreferences.getLastSelectedAyah(context, currentPage)) }

    val ayahs = remember(currentPage) { QuranRepository.getAyahsForPage(currentPage) }
    val surahs = remember(ayahs) { ayahs.map { it.surahNumber }.distinct().mapNotNull { QuranRepository.getSurah(it) } }
    val surahTitle = surahs.joinToString(" • ") { it.nameAr }
    val juzNum = QuranRepository.getJuzNumber(currentPage)

    val audioPlayer = remember { QuranPageAudioPlayer() }
    var isPlayingAudio by remember { mutableStateOf(false) }
    var isLoadingAudio by remember { mutableStateOf(false) }

    DisposableEffect(currentPage) {
        audioPlayer.stop()
        isPlayingAudio = false
        isLoadingAudio = false
        onDispose { audioPlayer.release() }
    }

    val pageBgColor = if (isDarkTheme) Color(0xFF0D1410) else Color(0xFFFBF8EF)

    Column(modifier = modifier.fillMaxSize().background(pageBgColor)) {
        QuranPageLockToolbar(
            surahTitle = surahTitle, juzNumber = juzNum, pageNumber = currentPage,
            fontSize = userFontSize, isDarkTheme = isDarkTheme,
            isPlayingAudio = isPlayingAudio, isLoadingAudio = isLoadingAudio,
            onZoomIn = {
                val next = (userFontSize + 1.5f).coerceAtMost(30f)
                userFontSize = next
                QuranLockPreferences.setFontSize(context, next)
            },
            onZoomOut = {
                val prev = (userFontSize - 1.5f).coerceAtLeast(13f)
                userFontSize = prev
                QuranLockPreferences.setFontSize(context, prev)
            },
            onToggleTheme = {
                val nextMode = !isDarkTheme
                isDarkTheme = nextMode
                QuranLockPreferences.setDarkTheme(context, nextMode)
            },
            onToggleAudio = {
                audioPlayer.playPage(currentPage) { playing, loading ->
                    isPlayingAudio = playing
                    isLoadingAudio = loading
                }
            },
            onResetToStart = {
                pageOffset = 0
                basePage = config.startPage.coerceIn(1, 604)
                onResetToStart?.invoke()
            },
            onEmergencyBypass = { onEmergencyBypass?.invoke() }
        )

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            QuranPageContentBody(
                ayahs = ayahs, selectedAyah = selectedAyah, fontSize = userFontSize,
                isDarkTheme = isDarkTheme,
                onSelectAyah = { aNum ->
                    selectedAyah = aNum
                    QuranLockPreferences.setLastSelectedAyah(context, currentPage, aNum)
                }
            )
        }

        QuranPageBottomBar(
            pageOffset = pageOffset,
            totalPages = totalPages,
            selectedAyah = selectedAyah,
            isDarkTheme = isDarkTheme,
            onPreviousPage = { pageOffset -= 1 },
            onNextOrFinish = {
                if (pageOffset + 1 < totalPages) pageOffset += 1
                else onFinish()
            }
        )
    }
}
