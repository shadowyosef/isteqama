package com.example.ui.adhkar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdhkarRepository
import com.example.model.DhikrCategory
import com.example.model.DhikrItem
import com.example.util.AppStrings

@Composable
fun AdhkarHomeScreen() {
    var selectedCategory by remember { mutableStateOf(DhikrCategory.MORNING) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<DhikrItem?>(null) }
    val adhkarList by AdhkarRepository.adhkarState.collectAsState()
    val filteredItems = remember(adhkarList, selectedCategory) {
        adhkarList.filter { it.category == selectedCategory }
    }
    val completedCount = remember(filteredItems) { filteredItems.count { it.isCompleted } }

    var showCreateSheet by remember { mutableStateOf(false) }
    var editingList by remember { mutableStateOf<com.example.model.CustomDhikrList?>(null) }
    val customLists by com.example.data.CustomDhikrRepository.lists.collectAsState()

    val categoryTitle = when (selectedCategory) {
        DhikrCategory.MORNING -> AppStrings.get("أذكار الصباح النبوية الكاملة", "Complete Morning Prophetic Adhkar")
        DhikrCategory.EVENING -> AppStrings.get("أذكار المساء وحفظ الحرز", "Complete Evening Adhkar")
        DhikrCategory.SLEEP -> AppStrings.get("أذكار النوم وسنن الاضطجاع", "Complete Sleep Adhkar")
        DhikrCategory.PRAYER -> AppStrings.get("أذكار ما بعد الصلوات المكتوبة", "Complete After-Prayer Adhkar")
        DhikrCategory.CUSTOM -> AppStrings.get("قوائم الأذكار المخصصة", "Custom Dhikr Lists")
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (selectedCategory == DhikrCategory.CUSTOM) showCreateSheet = true else showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_custom_dhikr_fab")
            ) { Icon(Icons.Default.Add, contentDescription = "Add Dhikr") }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).testTag("adhkar_home_screen")) {
            ScrollableTabRow(selectedTabIndex = selectedCategory.ordinal, edgePadding = 12.dp, divider = {}) {
                DhikrCategory.values().forEach { category ->
                    Tab(
                        selected = selectedCategory == category, onClick = { selectedCategory = category },
                        text = {
                            Text(when (category) {
                                DhikrCategory.MORNING -> AppStrings.get("أذكار الصباح", "Morning")
                                DhikrCategory.EVENING -> AppStrings.get("أذكار المساء", "Evening")
                                DhikrCategory.SLEEP -> AppStrings.get("أذكار النوم", "Sleep")
                                DhikrCategory.PRAYER -> AppStrings.get("بعد الصلاة", "Prayer")
                                DhikrCategory.CUSTOM -> AppStrings.get("قوائم مخصصة", "Custom")
                            })
                        }
                    )
                }
            }

            if (selectedCategory == DhikrCategory.CUSTOM) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(customLists, key = { it.id }) { list -> CustomDhikrListCard(list = list, onEdit = { editingList = list }) }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = categoryTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            Text(text = "${filteredItems.size} ${AppStrings.get("أذكار نبوية كاملة", "Adhkar")} • ${AppStrings.get("المكتمل:", "Done:")} $completedCount/${filteredItems.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { AdhkarRepository.resetCategory(selectedCategory) }, modifier = Modifier.testTag("reset_category_btn")) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(filteredItems, key = { it.id }) { item ->
                        DhikrItemCard(item = item, onIncrement = { AdhkarRepository.incrementDhikr(item.id) }, onEdit = { editingItem = item })
                    }
                }
            }
        }
    }

    if (showAddDialog) AddCustomDhikrDialog(onDismiss = { showAddDialog = false })
    if (showCreateSheet) CreateCustomDhikrSheet(onDismiss = { showCreateSheet = false })
    editingList?.let { list -> CreateCustomDhikrSheet(initialList = list, onDismiss = { editingList = null }) }
    editingItem?.let { item -> EditDhikrDialog(item = item, onDismiss = { editingItem = null }) }
}
