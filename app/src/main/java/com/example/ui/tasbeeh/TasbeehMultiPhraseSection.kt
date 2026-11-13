package com.example.ui.tasbeeh

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TasbeehPhraseItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TasbeehMultiPhraseSection(
    phrases: List<TasbeehPhraseItem>,
    isRandom: Boolean,
    onPhrasesChange: (List<TasbeehPhraseItem>) -> Unit,
    onRandomChange: (Boolean) -> Unit,
    isArabic: Boolean
) {
    var newText by remember { mutableStateOf("") }
    var newCount by remember { mutableIntStateOf(33) }
    var showPresetsDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) "أذكار التسبيح (${phrases.size})" else "Tasbeeh Phrases (${phrases.size})",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FilterChip(
                    selected = !isRandom,
                    onClick = { onRandomChange(false) },
                    leadingIcon = { Icon(Icons.Default.SyncAlt, null, modifier = Modifier.size(14.dp)) },
                    label = { Text(if (isArabic) "بالتتالي" else "Sequential", fontSize = 10.sp) }
                )
                FilterChip(
                    selected = isRandom,
                    onClick = { onRandomChange(true) },
                    leadingIcon = { Icon(Icons.Default.Shuffle, null, modifier = Modifier.size(14.dp)) },
                    label = { Text(if (isArabic) "عشوائي" else "Random", fontSize = 10.sp) }
                )
            }
        }

        OutlinedButton(
            onClick = { showPresetsDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isArabic) "بنك الأذكار الشامل (اختيار وتشيكبوكس)" else "Comprehensive Azkar Presets (Checkboxes)", fontSize = 12.sp)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newText,
                onValueChange = { newText = it },
                placeholder = { Text(if (isArabic) "أضف ذكراً جديداً..." else "Add new phrase...", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = newCount.toString(),
                onValueChange = { newCount = it.toIntOrNull()?.coerceIn(1, 1000) ?: newCount },
                label = { Text(if (isArabic) "العدد" else "Cnt", fontSize = 9.sp) },
                modifier = Modifier.width(60.dp),
                singleLine = true
            )
            IconButton(
                onClick = {
                    if (newText.isNotBlank()) {
                        onPhrasesChange(phrases + TasbeehPhraseItem(newText.trim(), newText.trim(), newCount))
                        newText = ""
                    }
                },
                modifier = Modifier.size(42.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            phrases.forEachIndexed { index, item ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}. ${item.phraseAr} (${item.targetCount}x)",
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (phrases.size > 1) {
                            IconButton(
                                onClick = { onPhrasesChange(phrases.filterIndexed { i, _ -> i != index }) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPresetsDialog) {
        ComprehensiveTasbeehSelectionDialog(
            initialSelectedPhrases = phrases.map { it.phraseAr },
            isArabic = isArabic,
            onDismiss = { showPresetsDialog = false },
            onApply = { selectedList ->
                val existing = phrases.toMutableList()
                selectedList.forEach { sel ->
                    if (existing.none { it.phraseAr == sel.phraseAr }) {
                        existing.add(sel)
                    }
                }
                onPhrasesChange(existing)
                showPresetsDialog = false
            }
        )
    }
}
