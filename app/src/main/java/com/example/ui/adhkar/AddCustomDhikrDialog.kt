package com.example.ui.adhkar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.model.CustomDhikrList
import com.example.model.DhikrCategory
import com.example.model.DhikrItem
import com.example.util.AppStrings

@Composable
fun AddCustomDhikrDialog(
    onDismiss: () -> Unit
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    var title by remember { mutableStateOf("") }
    var dhikrItems by remember {
        mutableStateOf(listOf("سُبْحَانَ اللَّهِ" to "33"))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isArabic) "إضافة ذكر أو روتين متسلسل" else "Add Dhikr / Sequence Routine",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isArabic) "عنوان الذكر أو الروتين" else "Routine Title") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_dhikr_title_input")
                )

                Text(
                    text = if (isArabic) "الأذكار المتسلسلة (تنتقل تلقائياً وراء بعضها):"
                    else "Sequential Dhikrs (auto-advances):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                dhikrItems.forEachIndexed { index, (text, count) ->
                    DhikrSequenceInputItem(
                        index = index,
                        text = text,
                        countStr = count,
                        canDelete = dhikrItems.size > 1,
                        onTextChange = { newT ->
                            dhikrItems = dhikrItems.toMutableList().also { it[index] = newT to count }
                        },
                        onCountChange = { newC ->
                            dhikrItems = dhikrItems.toMutableList().also { it[index] = text to newC }
                        },
                        onDelete = {
                            if (dhikrItems.size > 1) {
                                dhikrItems = dhikrItems.toMutableList().also { it.removeAt(index) }
                            }
                        }
                    )
                }

                OutlinedButton(
                    onClick = {
                        val defaultNext = if (dhikrItems.size == 1) "الْحَمْدُ لِلَّهِ" else "اللَّهُ أَكْبَرُ"
                        dhikrItems = dhikrItems + (defaultNext to "33")
                    },
                    modifier = Modifier.fillMaxWidth().testTag("add_another_dhikr_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "+ أضف ذكراً آخر للروتين المتسلسل" else "+ Add Another Dhikr to Routine",
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val validItems = dhikrItems.filter { it.first.isNotBlank() }
                    if (validItems.isNotEmpty()) {
                        val routineTitle = title.ifBlank { validItems.first().first }
                        validItems.forEach { (text, countStr) ->
                            val target = countStr.toIntOrNull()?.coerceAtLeast(1) ?: 33
                            AdhkarRepository.addCustomDhikr(text, text, target, routineTitle)
                        }
                        val items = validItems.mapIndexed { idx, (text, countStr) ->
                            val target = countStr.toIntOrNull()?.coerceAtLeast(1) ?: 33
                            DhikrItem("item_${System.currentTimeMillis()}_$idx", DhikrCategory.CUSTOM, text, text, "ذكر متسلسل", target, isCustom = true)
                        }
                        CustomDhikrRepository.addList(
                            CustomDhikrList("dl_${System.currentTimeMillis()}", routineTitle, routineTitle, items, isCustom = true)
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_custom_dhikr_btn")
            ) {
                Text(AppStrings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.cancel) }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
