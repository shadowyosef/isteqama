package com.example.ui.adhkar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdhkarRepository
import com.example.data.AppPreferencesManager
import com.example.data.CustomDhikrRepository
import com.example.data.TasbeehRepository
import com.example.model.CustomDhikrList
import com.example.model.DhikrCategory
import com.example.model.DhikrItem
import com.example.model.TasbeehItem
import com.example.util.AppStrings

@Composable
fun QuickAddDhikrOrTasbeehDialog(
    initialIsTasbeeh: Boolean = false,
    onDismiss: () -> Unit,
    onCreated: (phrase: String, target: Int, isTasbeeh: Boolean) -> Unit
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    var isTasbeeh by remember { mutableStateOf(initialIsTasbeeh) }
    var textAr by remember { mutableStateOf("") }
    var targetStr by remember { mutableStateOf("33") }
    var virtue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isArabic) "إضافة ذكر أو تسبيح جديد فورياً" else "Quick Add New Dhikr or Tasbeeh",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = !isTasbeeh,
                        onClick = { isTasbeeh = false },
                        label = { Text(if (isArabic) "ذكر ودعاء" else "Dhikr", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = isTasbeeh,
                        onClick = { isTasbeeh = true },
                        label = { Text(if (isArabic) "تسبيح واستغفار" else "Tasbeeh", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = textAr,
                    onValueChange = { textAr = it },
                    label = { Text(if (isArabic) "نص الذكر / التسبيح" else "Dhikr / Tasbeeh Text") },
                    modifier = Modifier.fillMaxWidth().testTag("quick_dhikr_text_input")
                )

                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it.filter { c -> c.isDigit() } },
                    label = { Text(if (isArabic) "العدد المستهدف (مثلاً 33 أو 100)" else "Target Count") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = virtue,
                    onValueChange = { virtue = it },
                    label = { Text(if (isArabic) "الفضل أو الملاحظة (اختياري)" else "Virtue / Note (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = if (isArabic) "💡 سينضاف الذكر تلقائياً لبنك الأذكار العام وقائمة التسبيح والتطبيق."
                    else "💡 Will automatically be saved to both Dhikr and Tasbeeh catalogs.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textAr.isNotBlank()) {
                        val count = targetStr.toIntOrNull()?.coerceAtLeast(1) ?: 33
                        // 1. Add to Adhkar Repository
                        AdhkarRepository.addCustomDhikr(
                            textAr = textAr,
                            textEn = textAr,
                            target = count,
                            virtue = virtue.ifBlank { if (isTasbeeh) "تسبيح مضاف" else "ذكر مضاف" },
                            category = if (isTasbeeh) DhikrCategory.CUSTOM else DhikrCategory.CUSTOM,
                            titleAr = textAr.take(25)
                        )
                        // 2. Add to Tasbeeh Repository
                        TasbeehRepository.addCustomTasbeeh(textAr, textAr, count)
                        // 3. Add to Custom Dhikr Lists as a single routine list so it can be scheduled
                        val singleItem = DhikrItem("item_${System.currentTimeMillis()}", DhikrCategory.CUSTOM, textAr, textAr, virtue, count, isCustom = true)
                        CustomDhikrRepository.addList(
                            CustomDhikrList("dl_${System.currentTimeMillis()}", textAr.take(30), textAr.take(30), listOf(singleItem), isCustom = true)
                        )

                        onCreated(textAr, count, isTasbeeh)
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("confirm_quick_add_dhikr_btn")
            ) {
                Text(AppStrings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.cancel) }
        },
        shape = RoundedCornerShape(18.dp)
    )
}
