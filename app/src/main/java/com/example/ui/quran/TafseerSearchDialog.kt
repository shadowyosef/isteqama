package com.example.ui.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.QuranRepository
import com.example.data.TafseerDataProvider
import com.example.model.SurahInfo
import com.example.util.AppStrings

@Composable
fun TafseerSearchDialog(
    onOpenSurah: (SurahInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val isAr = AppStrings.isArabic
    var query by remember { mutableStateOf("") }
    val results = remember(query) { TafseerDataProvider.searchEntries(query) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isAr) "بحث في القرآن والتفسير" else "Search Quran & Tafseer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text(if (isAr) "ابحث عن كلمة (مثال: الرحمة، العسر، الرزق)..." else "Search keyword...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("tafseer_search_input"),
                    singleLine = true
                )

                if (query.isBlank()) {
                    Text(
                        text = if (isAr) "أمثلة سريعة: العسر، الرزق، القيوم، الحمد" else "Try: الرحمة، الصمد، العسر",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else if (results.isEmpty()) {
                    Text(
                        text = if (isAr) "لا توجد نتائج مطابقة لهذا البحث" else "No matching Tafseer entries found",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(results) { (ref, surahName, bundle) ->
                            val (sNum, aNum) = ref
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth().clickable {
                                    QuranRepository.getSurah(sNum)?.let { s ->
                                        onDismiss()
                                        onOpenSurah(s)
                                    }
                                }
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("$surahName • ${if (isAr) "آية" else "Ayah"} $aNum", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    Text(bundle.muyassar, fontSize = 12.sp, maxLines = 2)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
