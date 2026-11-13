package com.example.ui.tasbeeh

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.TasbeehRepository
import com.example.model.TasbeehItem
import com.example.util.AppStrings

@Composable
fun CreateTasbeehSetDialog(
    onDismiss: () -> Unit
) {
    var titleAr by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var phrase1 by remember { mutableStateOf("سُبْحَانَ اللَّهِ") }
    var target1 by remember { mutableStateOf("33") }
    var phrase2 by remember { mutableStateOf("الْحَمْدُ لِلَّهِ") }
    var target2 by remember { mutableStateOf("33") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.get("إنشاء سيت تسبيح جديد", "Create Tasbeeh Set"), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = titleAr,
                    onValueChange = { titleAr = it },
                    label = { Text("اسم السيت / الروتين (عربي)") },
                    modifier = Modifier.fillMaxWidth().testTag("set_title_ar_input")
                )
                OutlinedTextField(
                    value = titleEn,
                    onValueChange = { titleEn = it },
                    label = { Text("Set Title (English)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phrase1,
                    onValueChange = { phrase1 = it },
                    label = { Text("الذكر الأول والعدد") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = target1,
                    onValueChange = { target1 = it.filter { c -> c.isDigit() } },
                    label = { Text("الهدف الأول") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phrase2,
                    onValueChange = { phrase2 = it },
                    label = { Text("الذكر الثاني والعدد") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = target2,
                    onValueChange = { target2 = it.filter { c -> c.isDigit() } },
                    label = { Text("الهدف الثاني") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titleAr.isNotBlank()) {
                        val items = listOf(
                            TasbeehItem("t_set_1_${System.currentTimeMillis()}", phrase1, phrase1, target1.toIntOrNull() ?: 33),
                            TasbeehItem("t_set_2_${System.currentTimeMillis()}", phrase2, phrase2, target2.toIntOrNull() ?: 33)
                        )
                        TasbeehRepository.addCustomSet(titleAr, titleEn.ifEmpty { titleAr }, items)
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_tasbeeh_set_btn")
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
