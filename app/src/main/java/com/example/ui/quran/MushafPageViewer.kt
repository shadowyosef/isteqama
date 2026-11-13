package com.example.ui.quran

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranBookmarksRepository
import com.example.data.SmartAlertPreferences
import com.example.model.Ayah
import com.example.model.SurahInfo
import com.example.util.AppStrings

@Composable
fun MushafPageViewer(
    surah: SurahInfo,
    ayahs: List<Ayah>,
    fontSizeSp: Float,
    playingAyahNumber: Int? = null,
    onSelectAyahForTafseer: (Ayah) -> Unit
) {
    var currentPage by remember(surah.pageStart) { mutableStateOf(surah.pageStart.coerceAtLeast(1)) }
    val isBookmarked = remember(currentPage) { QuranBookmarksRepository.bookmarks.value.any { it.pageNumber == currentPage } }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "الجزء ${((currentPage - 1) / 20) + 1}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Text(text = if (AppStrings.isArabic) surah.displayNameAr else surah.displayNameEn, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = if (isBookmarked) "🔖 محفوظ" else "صفحة $currentPage", style = MaterialTheme.typography.labelMedium)
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(2.dp, Color(0xFFD4AF37), RoundedCornerShape(16.dp))
                .padding(4.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (currentPage == surah.pageStart) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        fontSize = (fontSizeSp + 2).sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                }

                ayahs.forEach { ayah ->
                    val isActive = playingAyahNumber == ayah.numberInSurah
                    Text(
                        text = "${ayah.textArabic} ﴿${ayah.numberInSurah}﴾",
                        fontSize = fontSizeSp.sp,
                        lineHeight = (fontSizeSp * 1.8f).sp,
                        textAlign = TextAlign.Center,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSelectAyahForTafseer(ayah) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { if (currentPage > 1) currentPage-- }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev Page")
            }
            Text(
                text = "${AppStrings.get("صفحة", "Page")} $currentPage / 604",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = { if (currentPage < 604) currentPage++ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Page")
            }
        }
    }
}
