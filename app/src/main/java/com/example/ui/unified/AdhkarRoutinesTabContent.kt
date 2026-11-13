package com.example.ui.unified

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UnifiedDashboardItem
import com.example.util.AppStrings

@Composable
fun AdhkarRoutinesTabContent(
    items: List<UnifiedDashboardItem>,
    viewMode: String,
    onViewModeChange: (String) -> Unit,
    onOpen: (UnifiedDashboardItem) -> Unit = {},
    onIncrement: (String) -> Unit,
    onEdit: (UnifiedDashboardItem) -> Unit,
    onDuplicate: (UnifiedDashboardItem) -> Unit,
    onDelete: (String) -> Unit,
    onReset: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = AppStrings.get("سلاسل الأذكار والورد", "Adhkar & Ward Sequences"),
                style = MaterialTheme.typography.titleMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                var showPosDialog by remember { mutableStateOf(false) }
                AssistChip(
                    onClick = { showPosDialog = true },
                    leadingIcon = { Icon(Icons.Default.NearMe, null, modifier = Modifier.size(13.dp)) },
                    label = { Text(if (AppStrings.isArabic) "موقع التوست" else "Toast Pos", fontSize = 10.sp) }
                )
                if (showPosDialog) {
                    com.example.ui.tasbeeh.ToastPositionPickerDialog(isArabic = AppStrings.isArabic, onDismiss = { showPosDialog = false })
                }
                IconButton(onClick = { onViewModeChange("CARD") }) {
                    Icon(Icons.Default.ViewAgenda, contentDescription = "Card", tint = if (viewMode == "CARD") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
                IconButton(onClick = { onViewModeChange("LIST") }) {
                    Icon(Icons.Default.List, contentDescription = "List", tint = if (viewMode == "LIST") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
                IconButton(onClick = { onViewModeChange("GRID") }) {
                    Icon(Icons.Default.GridView, contentDescription = "Grid", tint = if (viewMode == "GRID") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            }
        }

        when (viewMode) {
            "LIST" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(items, key = { it.id }) { item ->
                        DashboardListItem(item = item, onOpen = { onOpen(item) }, onIncrement = { onIncrement(item.id) }, onEdit = { onEdit(item) }, onDelete = { onDelete(item.id) })
                    }
                }
            }
            "GRID" -> {
                LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 160.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(items, key = { it.id }) { item ->
                        DashboardCard(item = item, onOpen = { onOpen(item) }, onIncrement = { onIncrement(item.id) }, onEdit = { onEdit(item) }, onDuplicate = { onDuplicate(item) }, onDelete = { onDelete(item.id) }, onReset = { onReset(item.id) })
                    }
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(items, key = { it.id }) { item ->
                        DashboardCard(item = item, onOpen = { onOpen(item) }, onIncrement = { onIncrement(item.id) }, onEdit = { onEdit(item) }, onDuplicate = { onDuplicate(item) }, onDelete = { onDelete(item.id) }, onReset = { onReset(item.id) })
                    }
                }
            }
        }
    }
}
