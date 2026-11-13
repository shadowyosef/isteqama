package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.model.LockContentConfig
import com.example.ui.adhkar.QuickAddDhikrOrTasbeehDialog
import com.example.ui.adhkar.UnifiedDhikrSelectionDialog

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DhikrSelectSection(
    config: LockContentConfig,
    lockType: String,
    onConfigChange: (LockContentConfig) -> Unit,
    onTargetAmountUpdate: ((Int) -> Unit)? = null
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    var showBankDialog by remember { mutableStateOf(false) }
    var showQuickAddDialog by remember { mutableStateOf(false) }

    val quickPresets = if (lockType == "TASBEEH") {
        listOf(
            "سُبْحَانَ اللَّهِ" to "Subhanallah",
            "الْحَمْدُ لِلَّهِ" to "Alhamdulillah",
            "لا إِلَهَ إِلا اللَّهُ" to "La ilaha illallah",
            "اللَّهُ أَكْبَرُ" to "Allahu Akbar",
            "أَسْتَغْفِرُ اللَّهَ" to "Astaghfirullah",
            "لا حَوْلَ وَلا قُوَّةَ إِلا بِاللَّهِ" to "La hawla wa la quwwata illa billah"
        )
    } else {
        listOf(
            "أذكار الصباح" to "Morning Adhkar",
            "أذكار المساء" to "Evening Adhkar",
            "أذكار الاستيقاظ" to "Waking Up Adhkar",
            "أذكار النوم" to "Sleep Adhkar",
            "آية الكرسي والمعوذات" to "Ayat Al-Kursi & Mu'awwidhat",
            "سيد الاستغفار" to "Master Supplication"
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) "اختر الأذكار أو الروتين المطلوب للقفل:" else "Select Dhikr / Routine for Lock:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            FilledTonalButton(
                onClick = { showQuickAddDialog = true },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(30.dp).testTag("quick_add_dhikr_or_tasbeeh_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(if (isArabic) "+ إضافة جديد" else "+ Add New", fontSize = 10.sp)
            }
        }

        OutlinedButton(
            onClick = { showBankDialog = true },
            modifier = Modifier.fillMaxWidth().testTag("open_unified_dhikr_bank_btn"),
            shape = MaterialTheme.shapes.small
        ) {
            Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            val selCount = config.selectedDhikrIds.size
            Text(
                text = if (isArabic) "📚 فتح بنك الأذكار والتسبيحات الموحد (تشيكبوكس لكل ذكر: $selCount محدد)"
                else "📚 Open Unified Dhikr & Tasbeeh Bank (Checklist: $selCount selected)",
                fontSize = 11.sp
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            quickPresets.forEach { (ar, en) ->
                val label = if (isArabic) ar else en
                val isSelected = config.selectedDhikrIds.contains(ar)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val updated = if (isSelected) config.selectedDhikrIds - ar else config.selectedDhikrIds + ar
                        onConfigChange(config.copy(selectedDhikrIds = updated.ifEmpty { listOf(ar) }))
                    },
                    label = { Text(label, fontSize = 10.sp) },
                    modifier = Modifier.height(28.dp)
                )
            }
        }
    }

    if (showBankDialog) {
        UnifiedDhikrSelectionDialog(
            initialSelected = config.selectedDhikrIds,
            isArabic = isArabic,
            onDismiss = { showBankDialog = false },
            onApply = { selected, totalTarget ->
                onConfigChange(config.copy(selectedDhikrIds = selected))
                onTargetAmountUpdate?.invoke(totalTarget)
            },
            onAddNewRequested = {
                showBankDialog = false
                showQuickAddDialog = true
            }
        )
    }

    if (showQuickAddDialog) {
        QuickAddDhikrOrTasbeehDialog(
            initialIsTasbeeh = lockType == "TASBEEH",
            onDismiss = { showQuickAddDialog = false },
            onCreated = { phrase, target, _ ->
                val updated = (config.selectedDhikrIds + phrase).distinct()
                onConfigChange(config.copy(selectedDhikrIds = updated))
                onTargetAmountUpdate?.invoke(target)
            }
        )
    }
}
