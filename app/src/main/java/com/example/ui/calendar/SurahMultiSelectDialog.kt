package com.example.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.AppPreferencesManager
import com.example.data.QuranMetadata

@Composable
fun SurahMultiSelectDialog(
    initialSelected: List<Int>,
    onDismiss: () -> Unit,
    onConfirm: (List<Int>) -> Unit
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    var selectedIds by remember { mutableStateOf(initialSelected.toSet()) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) QuranMetadata.surahs
        else QuranMetadata.surahs.filter {
            it.nameAr.contains(searchQuery.trim()) ||
            it.nameEn.contains(searchQuery.trim(), ignoreCase = true) ||
            it.number.toString() == searchQuery.trim()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().height(480.dp).testTag("surah_multi_select_dialog"),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isArabic) "اختر السور لقفل الورد (${selectedIds.size} مختارة)" else "Select Surahs (${selectedIds.size} selected)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(if (isArabic) "بحث برقم أو اسم السورة..." else "Search surah...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                )

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(filteredSurahs, key = { it.number }) { surah ->
                        val isChecked = selectedIds.contains(surah.number)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedIds = if (isChecked) selectedIds - surah.number else selectedIds + surah.number
                                }
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    selectedIds = if (isChecked) selectedIds - surah.number else selectedIds + surah.number
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${surah.number}. ${if (isArabic) surah.nameAr else surah.nameEn}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${surah.versesCount} ${if (isArabic) "آية" else "ayahs"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(if (isArabic) "إلغاء" else "Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val res = selectedIds.toList().ifEmpty { listOf(1) }
                            onConfirm(res)
                            onDismiss()
                        }
                    ) {
                        Text(if (isArabic) "تأكيد الاختيار" else "Confirm")
                    }
                }
            }
        }
    }
}
