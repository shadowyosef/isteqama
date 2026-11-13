package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.QuranWordMeaningCatalog
import com.example.model.Ayah
import com.example.ui.theme.OrnateIslamicCard
import com.example.util.AppStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordMeaningDialog(ayah: Ayah, onDismiss: () -> Unit) {
    val isAr = AppStrings.isArabic
    val words = remember(ayah.textArabic) { ayah.textArabic.split(Regex("\\s+")).filter { it.isNotBlank() } }
    var selectedToken by remember(ayah.numberInSurah) { mutableStateOf(words.firstOrNull().orEmpty()) }
    val entry = remember(selectedToken) { QuranWordMeaningCatalog.lookupWord(selectedToken) }

    Dialog(onDismissRequest = onDismiss) {
        OrnateIslamicCard(cornerRadius = 22.dp, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "معاني مفردات الآية (${ayah.numberInSurah})" else "Word-by-Word Meanings", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                }

                Text(if (isAr) "اضغط على أي كلمة لمعرفة جذرها ومعناها:" else "Tap any word to inspect its root & meaning:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    words.forEach { w ->
                        FilterChip(
                            selected = selectedToken == w,
                            onClick = { selectedToken = w },
                            label = { Text(w, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }

                Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${entry.wordAr}  •  ${if (isAr) "الجذر:" else "Root:"} (${entry.rootAr})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                        Text(if (isAr) entry.meaningAr else entry.meaningEn, fontSize = 13.sp, lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}
