package com.example.ui.quran

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.model.QuranViewMode
import com.example.model.SurahInfo
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderTopBar(
    surah: SurahInfo,
    viewMode: QuranViewMode,
    isBookmarked: Boolean,
    isAutoScrolling: Boolean,
    onBack: () -> Unit,
    onToggleViewMode: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenReciterSheet: () -> Unit,
    onOpenRiwayahSheet: () -> Unit,
    onOpenFullPlayer: () -> Unit,
    onOpenSurahInfo: () -> Unit,
    onToggleAutoScroll: () -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onOpenHifzStudio: () -> Unit = {}
) {
    val isAr = AppStrings.isArabic
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = if (isAr) surah.displayNameAr else surah.displayNameEn,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack, modifier = Modifier.testTag("reader_back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onOpenSurahInfo, modifier = Modifier.testTag("reader_surah_info_button")) {
                Icon(Icons.Default.Info, contentDescription = if (isAr) "مقاصد السورة" else "Surah Info", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onToggleBookmark, modifier = Modifier.testTag("reader_bookmark_button")) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onOpenFullPlayer, modifier = Modifier.testTag("reader_player_button")) {
                Icon(Icons.Default.Headphones, contentDescription = "Full Player", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { showMenu = true }, modifier = Modifier.testTag("reader_more_button")) {
                Icon(Icons.Default.MoreVert, contentDescription = if (isAr) "خيارات القراءة" else "Reader Options")
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text = { Text(if (isAr) "الحفظ والتجويد والمتشابهات" else "Hifz & Tajweed Studio") },
                    leadingIcon = { Icon(Icons.Default.Psychology, null) },
                    onClick = { showMenu = false; onOpenHifzStudio() }
                )
                DropdownMenuItem(
                    text = { Text(if (viewMode == QuranViewMode.MUSHAF_PAGE) (if (isAr) "عرض الآيات كقائمة" else "Verse List View") else (if (isAr) "عرض صفحة المصحف" else "Mushaf Page View")) },
                    leadingIcon = { Icon(if (viewMode == QuranViewMode.MUSHAF_PAGE) Icons.Default.FormatListNumbered else Icons.Default.MenuBook, null) },
                    onClick = { showMenu = false; onToggleViewMode() }
                )
                DropdownMenuItem(
                    text = { Text(if (isAr) "اختيار القارئ" else "Select Reciter") },
                    leadingIcon = { Icon(Icons.Default.RecordVoiceOver, null) },
                    onClick = { showMenu = false; onOpenReciterSheet() }
                )
                DropdownMenuItem(
                    text = { Text(if (isAr) "الرواية والترجمة" else "Riwayah & Translation") },
                    leadingIcon = { Icon(Icons.Default.Translate, null) },
                    onClick = { showMenu = false; onOpenRiwayahSheet() }
                )
                DropdownMenuItem(
                    text = { Text(if (isAutoScrolling) (if (isAr) "إيقاف التمرير التلقائي" else "Pause Auto-Scroll") else (if (isAr) "تشغيل التمرير التلقائي" else "Start Auto-Scroll")) },
                    leadingIcon = { Icon(if (isAutoScrolling) Icons.Default.Pause else Icons.Default.PlayArrow, null) },
                    onClick = { showMenu = false; onToggleAutoScroll() }
                )
                DropdownMenuItem(
                    text = {
                        Row {
                            TextButton(onClick = onIncreaseFontSize) { Text("A+", fontWeight = FontWeight.Bold) }
                            TextButton(onClick = onDecreaseFontSize) { Text("A-", fontWeight = FontWeight.Bold) }
                        }
                    },
                    onClick = {}
                )
            }
        }
    )
}
