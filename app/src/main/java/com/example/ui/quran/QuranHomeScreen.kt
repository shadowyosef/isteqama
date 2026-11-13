package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.QuranRepository
import com.example.model.SurahInfo
import com.example.util.AppStrings
import com.example.util.AudioPlayerManager

@Composable
fun QuranHomeScreen(
    onSelectSurah: (SurahInfo) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showKhatmaDialog by remember { mutableStateOf(false) }
    var showTafseerSearch by remember { mutableStateOf(false) }
    var showRadioDialog by remember { mutableStateOf(false) }
    var infoSurah by remember { mutableStateOf<SurahInfo?>(null) }
    val surahs = remember(searchQuery) { QuranRepository.searchSurahs(searchQuery) }

    Column(modifier = Modifier.fillMaxSize().testTag("quran_home_screen")) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(AppStrings.searchSurah) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.weight(1f).testTag("search_surah_input"),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )
            IconButton(onClick = { showRadioDialog = true }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.Radio, contentDescription = "Quran Radio", tint = MaterialTheme.colorScheme.tertiary)
            }
            IconButton(onClick = { showTafseerSearch = true }, modifier = Modifier.size(48.dp).testTag("tafseer_search_btn")) {
                Icon(Icons.Default.MenuBook, contentDescription = "Search Tafseer", tint = MaterialTheme.colorScheme.secondary)
            }
            IconButton(onClick = { showKhatmaDialog = true }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Khatma Planner", tint = MaterialTheme.colorScheme.primary)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(surahs, key = { it.number }) { surah ->
                    SurahCardItem(
                        surah = surah,
                        onInfoClick = { infoSurah = surah },
                        onClick = { onSelectSurah(surah) }
                    )
                }
            }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                AudioPlayerBottomBar(onOpenFullPlayer = {
                    val sNum = AudioPlayerManager.currentSurah.value ?: 1
                    val sObj = QuranRepository.getSurah(sNum) ?: surahs.firstOrNull()
                    if (sObj != null) onSelectSurah(sObj)
                })
            }
        }
    }

    if (showKhatmaDialog) KhatmaPlannerDialog(onDismiss = { showKhatmaDialog = false })
    if (showTafseerSearch) TafseerSearchDialog(onOpenSurah = onSelectSurah, onDismiss = { showTafseerSearch = false })
    if (showRadioDialog) QuranRadioDialog(onDismiss = { showRadioDialog = false })
    infoSurah?.let { s -> SurahInfoDialog(surah = s, onDismiss = { infoSurah = null }) }
}
