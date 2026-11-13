package com.example.ui.calendar.dashboard

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.data.local.entity.ScreenLockEntity
import com.example.ui.calendar.CreateEditLockDialog

@Composable
fun CentralizedLocksDashboard(
    modifier: Modifier = Modifier
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf<String?>(null) }
    var editingLock by remember { mutableStateOf<ScreenLockEntity?>(null) }

    Column(
        modifier = modifier.fillMaxWidth().testTag("centralized_locks_dashboard"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Dashboard, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isArabic) "لوحة التحكم المركزية بالأقفال" else "Centralized Locks Dashboard",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (isArabic) "بحث في الأقفال بالاسم أو النوع..." else "Search locks by name or type...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("locks_search_input"),
            singleLine = true
        )

        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val filters = listOf(
                null to if (isArabic) "الكل" else "All",
                "QURAN" to if (isArabic) "قرآن" else "Quran",
                "HIFZ" to if (isArabic) "تسميع" else "Hifz",
                "DHIKR" to if (isArabic) "أذكار" else "Dhikr",
                "TASBEEH" to if (isArabic) "تسبيح" else "Tasbeeh",
                "QUIZ" to if (isArabic) "سؤال" else "Quiz"
            )
            filters.forEach { (typeKey, label) ->
                FilterChip(selected = selectedTypeFilter == typeKey, onClick = { selectedTypeFilter = typeKey }, label = { Text(label, fontSize = 11.sp) })
            }
        }

        ActiveLockStatusCard()

        UpcomingLocksCard(
            searchQuery = searchQuery,
            selectedType = selectedTypeFilter,
            onEditLock = { editingLock = it }
        )

        CompletedLocksCard()
    }

    editingLock?.let { lock ->
        CreateEditLockDialog(
            initialLock = lock,
            onDismiss = { editingLock = null }
        )
    }
}
