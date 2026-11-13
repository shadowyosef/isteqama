package com.example.ui.tasbeeh

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.ComprehensiveTasbeehCatalog
import com.example.model.TasbeehPhraseItem
import com.example.model.TasbeehPresetCategory

@Composable
fun ComprehensiveTasbeehSelectionDialog(
    initialSelectedPhrases: List<String> = emptyList(),
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onApply: (List<TasbeehPhraseItem>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TasbeehPresetCategory.ALL) }
    var selectedIds by remember {
        val initial = ComprehensiveTasbeehCatalog.items.filter { item ->
            initialSelectedPhrases.any { it == item.phraseAr || it == item.phraseEn }
        }.map { it.id }.toSet()
        mutableStateOf(initial)
    }

    val filteredItems = remember(searchQuery, selectedCategory) {
        ComprehensiveTasbeehCatalog.items.filter { item ->
            val matchCat = selectedCategory == TasbeehPresetCategory.ALL || item.category == selectedCategory
            val matchQuery = searchQuery.isBlank() ||
                item.phraseAr.contains(searchQuery, ignoreCase = true) ||
                item.phraseEn.contains(searchQuery, ignoreCase = true) ||
                item.virtueAr.contains(searchQuery, ignoreCase = true)
            matchCat && matchQuery
        }
    }

    val selectedCount = selectedIds.size

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(max = 620.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "بنك الأذكار والتسبيح الشامل" else "Comprehensive Azkar & Tasbeeh",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(if (isArabic) "بحث في الأذكار والأدعية..." else "Search azkar & duaa...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(TasbeehPresetCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(if (isArabic) cat.labelAr else cat.labelEn, fontSize = 11.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "المحدد: $selectedCount" else "Selected: $selectedCount",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { selectedIds = selectedIds + filteredItems.map { it.id } }) {
                            Text(if (isArabic) "تحديد الكل" else "Select All", fontSize = 11.sp)
                        }
                        TextButton(onClick = { selectedIds = emptySet() }) {
                            Text(if (isArabic) "إلغاء التحديد" else "Clear", fontSize = 11.sp)
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(items = filteredItems, key = { it.id }) { item ->
                        val isChecked = selectedIds.contains(item.id)
                        TasbeehPresetItemCard(
                            item = item,
                            isSelected = isChecked,
                            isArabic = isArabic,
                            onToggle = { checked ->
                                selectedIds = if (checked) selectedIds + item.id else selectedIds - item.id
                            }
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(if (isArabic) "إلغاء" else "Cancel") }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            val selectedItems = ComprehensiveTasbeehCatalog.items
                                .filter { selectedIds.contains(it.id) }
                                .map { TasbeehPhraseItem(it.phraseAr, it.phraseEn, it.defaultTarget) }
                            onApply(selectedItems)
                        },
                        enabled = selectedCount > 0
                    ) {
                        Text(if (isArabic) "إضافة المختار ($selectedCount)" else "Apply ($selectedCount)")
                    }
                }
            }
        }
    }
}
