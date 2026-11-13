package com.example.ui.quran

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookmarkCategory
import com.example.data.QuranBookmarksRepository
import com.example.data.QuranRepository
import com.example.util.AppStrings
import com.example.util.AudioPlayerManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranBookmarksSheet(
    onSelectAyah: (surah: Int, ayah: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val bookmarks by QuranBookmarksRepository.bookmarks.collectAsState()
    var filterCat by remember { mutableStateOf<BookmarkCategory?>(null) }
    val filtered = remember(bookmarks, filterCat) {
        if (filterCat == null) bookmarks else bookmarks.filter { it.category == filterCat }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Bookmark, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = if (isAr) "العلامات المرجعية المصنفة" else "Categorized Bookmarks",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(selected = filterCat == null, onClick = { filterCat = null }, label = { Text(if (isAr) "الكل" else "All", fontSize = 11.sp) })
                BookmarkCategory.values().forEach { cat ->
                    FilterChip(
                        selected = filterCat == cat,
                        onClick = { filterCat = cat },
                        label = { Text(if (isAr) cat.labelAr else cat.labelEn, fontSize = 11.sp) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    Text(if (isAr) "لا توجد علامات في هذا التصنيف" else "No bookmarks in this category", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtered, key = { it.id }) { bm ->
                        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${bm.surahNameAr} - ${if (isAr) "آية" else "Ayah"} ${bm.ayahNumber} • [${if (isAr) bm.category.labelAr else bm.category.labelEn}]",
                                        fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary
                                    )
                                    if (bm.ayahText.isNotBlank()) {
                                        Text(bm.ayahText, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        val ayahs = QuranRepository.getAyahsForSurah(bm.surahNumber)
                                        val a = ayahs.find { it.numberInSurah == bm.ayahNumber } ?: ayahs.firstOrNull()
                                        if (a != null) AudioPlayerManager.playAyah(context, bm.surahNumber, bm.ayahNumber, a.globalNumber)
                                        onSelectAyah(bm.surahNumber, bm.ayahNumber)
                                        onDismiss()
                                    }) { Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = MaterialTheme.colorScheme.primary) }
                                    IconButton(onClick = {
                                        QuranBookmarksRepository.toggleBookmark(bm.surahNumber, bm.ayahNumber, bm.pageNumber, bm.surahNameAr, bm.ayahText, bm.category)
                                    }) { Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error) }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
