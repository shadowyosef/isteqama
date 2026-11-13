package com.example.ui.tasbeeh

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Refresh
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
import com.example.data.TasbeehRepository
import com.example.model.TasbeehItem
import com.example.service.FloatingTasbeehService
import com.example.util.AppStrings

@Composable
fun TasbeehHomeScreen() {
    val context = LocalContext.current
    val phrases by TasbeehRepository.phrases.collectAsState()
    val selectedIndex by TasbeehRepository.selectedIndex.collectAsState()
    val current = remember(phrases, selectedIndex) { TasbeehRepository.getCurrentItem() }
    val lifetimeTotal by TasbeehRepository.totalLifetimeCount.collectAsState()
    var editingItem by remember { mutableStateOf<TasbeehItem?>(null) }
    var showCreateSetDialog by remember { mutableStateOf(false) }
    var showPresetsDialog by remember { mutableStateOf(false) }
    var showPositionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("tasbeeh_home_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    itemsIndexed(phrases) { index, item ->
                        FilterChip(
                            selected = index == selectedIndex,
                            onClick = { TasbeehRepository.selectedIndex.value = index },
                            label = { Text(if (AppStrings.isArabic) item.phraseAr else item.phraseEn, fontSize = 12.sp) }
                        )
                    }
                }
                Row {
                    IconButton(onClick = { showPresetsDialog = true }) {
                        Icon(Icons.Default.Checklist, contentDescription = "Presets", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { showCreateSetDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Set", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (AppStrings.isArabic) current.phraseAr else current.phraseEn,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${AppStrings.get("الهدف", "Target")}: ${current.target} • ${AppStrings.get("إجمالي", "Total")}: $lifetimeTotal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    IconButton(onClick = { editingItem = current }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Phrase", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            TasbeehBeadsRing(count = current.count, onTap = {
                val nextCount = current.count + 1
                val isGoalReached = nextCount >= current.target
                com.example.util.TasbeehHapticsHelper.performCountFeedback(context, isGoalReached)
                TasbeehRepository.increment()
            })
        }

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 70.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            OutlinedButton(onClick = { TasbeehRepository.resetCurrent() }, modifier = Modifier.testTag("reset_tasbeeh_btn")) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(AppStrings.get("تصفير", "Reset"))
            }
            OutlinedButton(onClick = { showPositionDialog = true }) {
                Icon(Icons.Default.NearMe, contentDescription = "Pos", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(AppStrings.get("موقع التوست", "Toast Pos"))
            }
            Button(onClick = { FloatingTasbeehService.start(context) }, modifier = Modifier.testTag("floating_tasbeeh_btn")) {
                Icon(Icons.Default.Layers, contentDescription = "Float", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(AppStrings.get("تذكير عائم", "Float Widget"))
            }
        }
    }

    editingItem?.let { EditTasbeehDialog(item = it, onDismiss = { editingItem = null }) }
    if (showCreateSetDialog) CreateTasbeehSetDialog(onDismiss = { showCreateSetDialog = false })
    if (showPositionDialog) ToastPositionPickerDialog(isArabic = AppStrings.isArabic, onDismiss = { showPositionDialog = false })
    if (showPresetsDialog) {
        ComprehensiveTasbeehSelectionDialog(
            initialSelectedPhrases = phrases.map { it.phraseAr },
            isArabic = AppStrings.isArabic,
            onDismiss = { showPresetsDialog = false },
            onApply = { selectedList ->
                val newItems = selectedList.map {
                    TasbeehItem("preset_${System.currentTimeMillis()}_${it.phraseAr.hashCode()}", it.phraseAr, it.phraseEn, it.targetCount)
                }
                TasbeehRepository.addMultipleTasbeeh(newItems)
                showPresetsDialog = false
            }
        )
    }
}
