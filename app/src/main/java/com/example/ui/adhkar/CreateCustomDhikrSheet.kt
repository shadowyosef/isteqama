package com.example.ui.adhkar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomDhikrRepository
import com.example.model.*
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomDhikrSheet(
    initialList: CustomDhikrList? = null,
    onDismiss: () -> Unit
) {
    val lKey = initialList?.id ?: "new"
    var titleAr by remember(lKey) { mutableStateOf(initialList?.titleAr ?: "") }
    var dhikrList by remember(lKey) {
        mutableStateOf(
            initialList?.items?.map { it.textAr to it.targetCount.toString() }
                ?: listOf("سُبْحَانَ اللَّهِ" to "33")
        )
    }
    var requireLock by remember(lKey) { mutableStateOf(initialList?.requireOnLockScreen ?: true) }

    ModalBottomSheet(onDismissRequest = onDismiss, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (initialList == null) "إنشاء روتين أذكار متسلسل" else "تعديل روتين الأذكار",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = titleAr,
                onValueChange = { titleAr = it },
                label = { Text("عنوان الروتين (مثال: أذكار ما بعد الصلاة)") },
                modifier = Modifier.fillMaxWidth().testTag("dhikr_list_title_ar")
            )

            Text("سلسلة الأذكار (ينتقل تلقائياً للذكر التالي بعد إتمامه):", fontSize = 12.sp, fontWeight = FontWeight.Bold)

            dhikrList.forEachIndexed { index, (text, countStr) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { newT ->
                            dhikrList = dhikrList.toMutableList().also { it[index] = newT to countStr }
                        },
                        label = { Text("الذكر #${index + 1}", fontSize = 10.sp) },
                        modifier = Modifier.weight(2f)
                    )
                    OutlinedTextField(
                        value = countStr,
                        onValueChange = { newC ->
                            dhikrList = dhikrList.toMutableList().also { it[index] = text to newC.filter { c -> c.isDigit() } }
                        },
                        label = { Text("العدد", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    if (dhikrList.size > 1) {
                        IconButton(onClick = { dhikrList = dhikrList.toMutableList().also { it.removeAt(index) } }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = { dhikrList = dhikrList + ("الْحَمْدُ لِلَّهِ" to "33") },
                modifier = Modifier.fillMaxWidth().testTag("add_chained_dhikr_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("+ أضف ذكر آخر للروتين المتسلسل", fontSize = 12.sp)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("إلزام الإتمام لفك شاشة القفل", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Switch(checked = requireLock, onCheckedChange = { requireLock = it })
            }

            Button(
                onClick = {
                    val items = dhikrList.mapIndexed { idx, (text, countStr) ->
                        val target = countStr.toIntOrNull()?.coerceAtLeast(1) ?: 33
                        DhikrItem(
                            id = "item_${System.currentTimeMillis()}_$idx",
                            category = DhikrCategory.CUSTOM,
                            textAr = text.ifBlank { "سُبْحَانَ اللَّهِ" },
                            textEn = text,
                            virtue = "ذكر في الروتين المتسلسل",
                            targetCount = target,
                            isCustom = true
                        )
                    }
                    val list = CustomDhikrList(
                        id = initialList?.id ?: "dl_${System.currentTimeMillis()}",
                        titleAr = titleAr.ifBlank { "روتين أذكار متسلسل" },
                        titleEn = titleAr,
                        items = items,
                        requireOnLockScreen = requireLock,
                        isCustom = true
                    )
                    if (initialList == null) CustomDhikrRepository.addList(list) else CustomDhikrRepository.updateList(list)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_custom_dhikr_btn")
            ) {
                Text(AppStrings.save)
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
