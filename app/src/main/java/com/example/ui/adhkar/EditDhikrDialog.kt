package com.example.ui.adhkar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AdhkarRepository
import com.example.model.DhikrItem
import com.example.util.AppStrings

@Composable
fun EditDhikrDialog(
    item: DhikrItem,
    onDismiss: () -> Unit
) {
    var titleAr by remember(item.id) { mutableStateOf(item.titleAr) }
    var textAr by remember(item.id) { mutableStateOf(item.textAr) }
    var textEn by remember(item.id) { mutableStateOf(item.textEn) }
    var targetStr by remember(item.id) { mutableStateOf(item.targetCount.toString()) }
    var virtue by remember(item.id) { mutableStateOf(item.virtue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.get("تعديل الذكر", "Edit Dhikr"), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titleAr,
                    onValueChange = { titleAr = it },
                    label = { Text("عنوان الذكر (مثال: آية الكرسي)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = textAr,
                    onValueChange = { textAr = it },
                    label = { Text("نص الذكر الكامل") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = textEn,
                    onValueChange = { textEn = it },
                    label = { Text("English Translation") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it.filter { c -> c.isDigit() } },
                    label = { Text("عدد التكرار المطلوب (1، 3، 7، 10، 100)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = virtue,
                    onValueChange = { virtue = it },
                    label = { Text("فضل الذكر والحديث") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetStr.toIntOrNull() ?: item.targetCount
                    AdhkarRepository.updateDhikr(item.id, textAr, textEn, target, virtue, titleAr)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_edit_dhikr_btn")
            ) {
                Text(AppStrings.save)
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = {
                        AdhkarRepository.deleteDhikr(item.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                    Text("حذف")
                }
                TextButton(onClick = onDismiss) { Text(AppStrings.cancel) }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
