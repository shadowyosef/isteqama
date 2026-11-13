package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ReciterInfo
import com.example.model.ReciterListProvider
import com.example.util.AppStrings
import com.example.util.AudioPlayerManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciterSelectionSheet(onDismiss: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var currentReciter by remember { mutableStateOf(AudioPlayerManager.selectedReciter) }
    val allReciters = remember { ReciterListProvider.reciters }
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) allReciters
        else allReciters.filter {
            it.nameAr.contains(searchQuery, ignoreCase = true) ||
            it.nameEn.contains(searchQuery, ignoreCase = true) ||
            it.styleAr.contains(searchQuery, ignoreCase = true)
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = AppStrings.get("اختر القارئ (20 قارئاً معتمداً)", "Select Quran Reciter (20 Reciters)"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(AppStrings.get("ابحث عن قارئ...", "Search reciter..."), fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered, key = { it.id }) { reciter ->
                    val isSelected = currentReciter == reciter.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        onClick = {
                            currentReciter = reciter.id
                            AudioPlayerManager.selectedReciter = reciter.id
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (AppStrings.isArabic) reciter.nameAr else reciter.nameEn,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (AppStrings.isArabic) reciter.styleAr else reciter.styleEn,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
