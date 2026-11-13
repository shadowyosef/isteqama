package com.example.ui.unified

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.model.*
import com.example.ui.adhkar.DhikrItemCard
import com.example.ui.adhkar.EditDhikrDialog
import com.example.util.AppStrings
import com.example.util.RoutineAdhkarResolver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailViewerSheet(
    item: UnifiedDashboardItem,
    onDismiss: () -> Unit,
    onEdit: (UnifiedDashboardItem) -> Unit
) {
    val isAr = AppStrings.isArabic
    val adhkarState by AdhkarRepository.adhkarState.collectAsState()
    val customLists by CustomDhikrRepository.lists.collectAsState()
    var editingDhikr by remember { mutableStateOf<DhikrItem?>(null) }

    val resolvedAdhkar = remember(adhkarState, customLists, item.id) {
        RoutineAdhkarResolver.getAdhkarForRoutine(item.id, item.titleAr)
    }
    val completedCount = remember(resolvedAdhkar) { resolvedAdhkar.count { it.isCompleted } }
    val progressFraction = if (resolvedAdhkar.isNotEmpty()) completedCount.toFloat() / resolvedAdhkar.size else 0f

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    val icon = if (item.category == RoutineCategory.QURAN_WARD) Icons.Default.AutoStories else Icons.Default.Spa
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Column {
                        Text(text = if (isAr) item.titleAr else item.titleEn, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        Text(text = "${resolvedAdhkar.size} ${AppStrings.get("أذكار كاملة", "Adhkar")} • ${AppStrings.get("المكتمل:", "Done:")} $completedCount/${resolvedAdhkar.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = {
                        resolvedAdhkar.forEach { d ->
                            AdhkarRepository.adhkarState.value = AdhkarRepository.adhkarState.value.map { if (it.id == d.id) it.copy(currentCount = 0, isCompleted = false) else it }
                        }
                        if (item.id.startsWith("dhikr_")) CustomDhikrRepository.resetList(item.id.removePrefix("dhikr_"))
                        UnifiedRoutineRepository.resetCount(item.id)
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { onEdit(item) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier.fillMaxWidth().height(6.dp).padding(vertical = 4.dp),
                color = if (progressFraction >= 1f) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
            ) {
                items(resolvedAdhkar, key = { it.id }) { dhikr ->
                    DhikrItemCard(
                        item = dhikr,
                        onIncrement = {
                            AdhkarRepository.incrementDhikr(dhikr.id)
                            if (item.id.startsWith("dhikr_")) CustomDhikrRepository.incrementItem(item.id.removePrefix("dhikr_"), dhikr.id)
                            UnifiedRoutineRepository.incrementCount(item.id)
                        },
                        onEdit = { editingDhikr = dhikr }
                    )
                }
            }
        }
    }

    editingDhikr?.let { d ->
        EditDhikrDialog(item = d, onDismiss = { editingDhikr = null })
    }
}
