package com.example.ui.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HifzLoopAndReflectionRepository
import com.example.data.QuranRepository
import com.example.model.Ayah
import com.example.util.AppStrings
import com.example.util.TajweedHighlighter

@Composable
fun AyahCard(
    ayah: Ayah,
    fontSizeSp: Float,
    isPlayingNow: Boolean = false,
    onPlayAudio: () -> Unit,
    onOpenTafseer: () -> Unit,
    onOpenReflection: () -> Unit = {}
) {
    val context = LocalContext.current
    var showShareMenu by remember { mutableStateOf(false) }
    var showWordMeaning by remember { mutableStateOf(false) }
    val tajweedEnabled by HifzLoopAndReflectionRepository.tajweedEnabled.collectAsState()
    val surahName = remember(ayah.surahNumber) { QuranRepository.getSurah(ayah.surahNumber)?.nameAr ?: "القرآن الكريم" }
    val styledText = remember(ayah.textArabic, ayah.numberInSurah, tajweedEnabled) {
        TajweedHighlighter.highlight("${ayah.textArabic} ۝${ayah.numberInSurah}", tajweedEnabled)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlayingNow) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isPlayingNow) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlayingNow) 3.dp else 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("ayah_${ayah.numberInSurah}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = styledText,
                fontSize = fontSizeSp.sp,
                lineHeight = (fontSizeSp * 1.6f).sp,
                textAlign = TextAlign.Right,
                color = if (isPlayingNow) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${AppStrings.get("جزء", "Juz")} ${ayah.juz} • ${AppStrings.get("صفحة", "p.")} ${ayah.page}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Row {
                    IconButton(onClick = onPlayAudio, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.PlayCircle, contentDescription = AppStrings.listen, tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onOpenTafseer, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Info, contentDescription = AppStrings.tafseer, tint = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = { showWordMeaning = true }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Spellcheck, contentDescription = "Word Meanings", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onOpenReflection, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.EditNote, contentDescription = AppStrings.get("خاطرة", "Note"), tint = MaterialTheme.colorScheme.primary)
                    }
                    Box {
                        IconButton(onClick = { showShareMenu = true }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Share, contentDescription = "Share Ayah", tint = MaterialTheme.colorScheme.tertiary)
                        }
                        DropdownMenu(expanded = showShareMenu, onDismissRequest = { showShareMenu = false }) {
                            DropdownMenuItem(
                                text = { Text(AppStrings.get("مشاركة كبطاقة Story", "Share Ornate Story Card")) },
                                onClick = {
                                    showShareMenu = false
                                    QuranCardShareHelper.shareAyahAsImageCard(context, ayah.textArabic, surahName, ayah.numberInSurah)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(AppStrings.get("مشاركة كنص", "Share as Text")) },
                                onClick = {
                                    showShareMenu = false
                                    QuranCardShareHelper.shareAyahAsText(context, ayah.textArabic, surahName, ayah.numberInSurah)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showWordMeaning) WordMeaningDialog(ayah = ayah, onDismiss = { showWordMeaning = false })
}
