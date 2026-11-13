package com.example.ui.adhkar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.UnifiedDhikrBankCatalog
import com.example.model.UnifiedBankDhikrItem
import com.example.model.UnifiedBankItemType

@Composable
fun UnifiedDhikrSelectionDialog(
    initialSelected: List<String> = emptyList(),
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onApply: (selectedTexts: List<String>, totalTarget: Int) -> Unit,
    onAddNewRequested: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(UnifiedBankItemType.ALL) }
    var allItems by remember { mutableStateOf(UnifiedDhikrBankCatalog.getAllBankItems()) }
    var selectedTexts by remember { mutableStateOf(initialSelected.toSet()) }

    val filteredItems = remember(allItems, searchQuery, selectedType) {
        allItems.filter { item ->
            val matchType = selectedType == UnifiedBankItemType.ALL || item.itemType == selectedType
            val matchQuery = searchQuery.isBlank() ||
                item.textAr.contains(searchQuery, ignoreCase = true) ||
                item.titleAr.contains(searchQuery, ignoreCase = true) ||
                item.textEn.contains(searchQuery, ignoreCase = true)
            matchType && matchQuery
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(max = 620.dp).testTag("unified_dhikr_selection_dialog"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) "بنك الأذكار والتسبيحات الموحد" else "Unified Dhikr & Tasbeeh Bank",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(if (isArabic) "بحث في جميع الأذكار والتسابيح..." else "Search all dhikr & tasbeeh...", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(16.dp)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    FilledTonalButton(
                        onClick = onAddNewRequested,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(38.dp).testTag("quick_add_new_dhikr_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(if (isArabic) "+ جديد" else "+ New", fontSize = 11.sp)
                    }
                }

                val types = listOf(
                    UnifiedBankItemType.ALL to if (isArabic) "الكل" else "All",
                    UnifiedBankItemType.TASBEEH to if (isArabic) "تسبيح" else "Tasbeeh",
                    UnifiedBankItemType.ROUTINE to if (isArabic) "روتينات" else "Routines",
                    UnifiedBankItemType.MORNING to if (isArabic) "صباح" else "Morning",
                    UnifiedBankItemType.EVENING to if (isArabic) "مساء" else "Evening",
                    UnifiedBankItemType.PRAYER to if (isArabic) "صلاة" else "Prayer",
                    UnifiedBankItemType.SLEEP to if (isArabic) "نوم" else "Sleep",
                    UnifiedBankItemType.CUSTOM to if (isArabic) "مخصص" else "Custom"
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(types) { (type, label) ->
                        FilterChip(selected = selectedType == type, onClick = { selectedType = type }, label = { Text(label, fontSize = 10.sp) })
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isArabic) "المحدد: ${selectedTexts.size}" else "Selected: ${selectedTexts.size}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { selectedTexts = selectedTexts + filteredItems.map { it.textAr } }) { Text(if (isArabic) "تحديد الكل" else "Select All", fontSize = 11.sp) }
                        TextButton(onClick = { selectedTexts = emptySet() }) { Text(if (isArabic) "إلغاء" else "Clear", fontSize = 11.sp) }
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filteredItems, key = { it.id }) { item ->
                        val isChecked = selectedTexts.contains(item.textAr) || selectedTexts.contains(item.titleAr)
                        UnifiedDhikrItemCard(
                            item = item,
                            isSelected = isChecked,
                            isArabic = isArabic,
                            onToggle = { checked ->
                                val key = item.textAr
                                selectedTexts = if (checked) selectedTexts + key else selectedTexts - key - item.titleAr
                            }
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(if (isArabic) "إلغاء" else "Cancel") }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            val chosenItems = allItems.filter { selectedTexts.contains(it.textAr) || selectedTexts.contains(it.titleAr) }
                            val totalTarget = chosenItems.sumOf { it.targetCount }.coerceAtLeast(33)
                            onApply(selectedTexts.toList(), totalTarget)
                            onDismiss()
                        },
                        enabled = selectedTexts.isNotEmpty()
                    ) {
                        Text(if (isArabic) "تطبيق المختار (${selectedTexts.size})" else "Apply (${selectedTexts.size})")
                    }
                }
            }
        }
    }
}
