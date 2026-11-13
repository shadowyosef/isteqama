package com.example.ui.quran

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Ayah
import com.example.util.AppStrings
import com.example.util.AudioPlayerManager

@Composable
fun PlayerAyahCard(
    ayah: Ayah?,
    totalAyahs: Int,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by AudioPlayerManager.playbackProgress.collectAsState()
    val isPlaying by AudioPlayerManager.isPlaying.collectAsState()
    val rawText = ayah?.textArabic ?: "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
    val primaryColor = MaterialTheme.colorScheme.primary
    val normalColor = MaterialTheme.colorScheme.onSurface

    val annotated = buildAnnotatedString {
        val words = rawText.split(" ")
        val activeIdx = if (isPlaying && words.isNotEmpty()) (progress * words.size).toInt().coerceIn(0, words.lastIndex) else -1
        words.forEachIndexed { idx, w ->
            if (idx == activeIdx) {
                withStyle(SpanStyle(color = primaryColor, fontWeight = FontWeight.ExtraBold)) { append(w) }
            } else {
                withStyle(SpanStyle(color = normalColor)) { append(w) }
            }
            if (idx < words.lastIndex) append(" ")
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp, max = 260.dp)
            .border(1.dp, primaryColor.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Text(
                            text = "${AppStrings.get("الآية", "Ayah")} ${ayah?.numberInSurah ?: 1} / $totalAyahs",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(3.dp)
                        )
                    }
                    IconButton(onClick = onToggleBookmark, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark Verse",
                            tint = if (isBookmarked) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = annotated,
                    fontSize = 20.sp,
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
