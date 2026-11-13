package com.example.ui.adhkar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.OccasionAdhkarCatalog
import com.example.model.OccasionCategory
import com.example.util.AppStrings

@Composable
fun OccasionAdhkarDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<OccasionCategory?>(null) }

    val filteredItems = remember(searchQuery, selectedCategory) {
        OccasionAdhkarCatalog.items.filter { item ->
            val matchCat = selectedCategory == null || item.category == selectedCategory
            val matchQuery = searchQuery.isBlank() ||
                item.titleAr.contains(searchQuery, ignoreCase = true) ||
                item.textAr.contains(searchQuery, ignoreCase = true) ||
                item.titleEn.contains(searchQuery, ignoreCase = true)
            matchCat && matchQuery
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().heightIn(max = 620.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isAr) "أدعية المناسبات والأحداث اليومية" else "Occasional & Lifestyle Duaas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(if (isAr) "بحث في الأدعية (سفر، مطر، خروج...)" else "Search prayers...", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text(if (isAr) "الكل" else "All", fontSize = 10.sp) }
                        )
                    }
                    items(OccasionCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(if (isAr) cat.labelAr else cat.labelEn, fontSize = 10.sp) }
                        )
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredItems, key = { it.id }) { item ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = if (isAr) item.titleAr else item.titleEn, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                    IconButton(
                                        onClick = {
                                            val tItem = com.example.model.TasbeehPhraseItem(item.textAr, item.textEn, 1)
                                            DhikrToastScheduleHelper.triggerToastNow(context, item.titleAr, listOf(tItem))
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(Icons.Default.NearMe, contentDescription = "Toast", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Text(
                                    text = if (isAr) item.textAr else item.textEn,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    textAlign = if (isAr) TextAlign.Right else TextAlign.Left,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(text = "💡 ${item.virtue}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
