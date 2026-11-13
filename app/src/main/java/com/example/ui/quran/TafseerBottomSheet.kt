package com.example.ui.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookmarkCategory
import com.example.data.QuranBookmarksRepository
import com.example.data.TafseerDataProvider
import com.example.util.AppStrings
import com.example.util.AudioPlayerManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TafseerBottomSheet(
    surahNumber: Int,
    ayahNumber: Int,
    ayahText: String,
    surahName: String,
    globalAyahNumber: Int = 1,
    pageNumber: Int = 1,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val bundle = remember(surahNumber, ayahNumber) { TafseerDataProvider.getRichTafseer(surahNumber, ayahNumber) }
    var selectedSource by remember { mutableIntStateOf(0) }
    var selectedBmCategory by remember { mutableStateOf(BookmarkCategory.TADABBUR) }

    val sourceTitles = if (isAr) listOf("الميسر", "السعدي", "ابن كثير", "الإعراب والمفردات")
    else listOf("Muyassar", "Saadi", "Ibn Kathir", "Grammar & Words")
    val bodyText = when (selectedSource) {
        0 -> bundle.muyassar
        1 -> bundle.saadi
        2 -> bundle.ibnKathir
        else -> bundle.irabAndWords
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("$surahName • ${if (isAr) "آية" else "Ayah"} $ayahNumber", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp)) }
            }

            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
                Text(ayahText, modifier = Modifier.padding(12.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                sourceTitles.forEachIndexed { idx, label ->
                    FilterChip(selected = selectedSource == idx, onClick = { selectedSource = idx }, label = { Text(label, fontSize = 11.sp) })
                }
            }

            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth()) {
                Text(bodyText, modifier = Modifier.padding(12.dp), fontSize = 13.sp, lineHeight = 22.sp)
            }

            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(if (isAr) "نوع العلامة:" else "Tag:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                BookmarkCategory.values().forEach { cat ->
                    FilterChip(selected = selectedBmCategory == cat, onClick = { selectedBmCategory = cat }, label = { Text(if (isAr) cat.labelAr else cat.labelEn, fontSize = 10.sp) })
                }
            }

            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(onClick = { AudioPlayerManager.playAyah(context, surahNumber, ayahNumber, globalAyahNumber) }) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(15.dp))
                    Text(if (isAr) "استماع" else "Listen", fontSize = 11.sp)
                }
                OutlinedButton(onClick = {
                    QuranCardShareHelper.shareAyahAsImageCard(context, ayahText, surahName, ayahNumber)
                }) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(15.dp))
                    Text(if (isAr) "بطاقة صورة" else "Card", fontSize = 11.sp)
                }
                OutlinedButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                    clipboard?.setPrimaryClip(ClipData.newPlainText("Ayah", "$ayahText ($surahName: $ayahNumber)"))
                    Toast.makeText(context, if (isAr) "تم النسخ" else "Copied", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                    Text(if (isAr) "نسخ" else "Copy", fontSize = 11.sp)
                }
                Button(onClick = {
                    QuranBookmarksRepository.toggleBookmark(surahNumber, ayahNumber, pageNumber, surahName, ayahText, selectedBmCategory)
                    Toast.makeText(context, if (isAr) "تم الحفظ في ${selectedBmCategory.labelAr}" else "Saved", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }) {
                    Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(15.dp))
                    Text(if (isAr) "حفظ" else "Save", fontSize = 11.sp)
                }
            }
        }
    }
}
