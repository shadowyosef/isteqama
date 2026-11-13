package com.example.ui.tasbeeh

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
import com.example.data.TasbeehRepository
import com.example.model.TasbeehItem
import com.example.util.AppStrings

@Composable
fun EditTasbeehDialog(
    item: TasbeehItem,
    onDismiss: () -> Unit
) {
    var phraseAr by remember(item.id) { mutableStateOf(item.phraseAr) }
    var phraseEn by remember(item.id) { mutableStateOf(item.phraseEn) }
    var targetStr by remember(item.id) { mutableStateOf(item.target.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.get("تعديل التسبيح", "Edit Tasbeeh"), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = phraseAr,
                    onValueChange = { phraseAr = it },
                    label = { Text("صيغة الذكر / التسبيح") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phraseEn,
                    onValueChange = { phraseEn = it },
                    label = { Text("English Translation") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it.filter { c -> c.isDigit() } },
                    label = { Text("الهدف المطلوب (مثال: 33، 100)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetStr.toIntOrNull() ?: item.target
                    TasbeehRepository.updateTasbeeh(item.id, phraseAr, phraseEn, target)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_edit_tasbeeh_btn")
            ) {
                Text(AppStrings.save)
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = {
                        TasbeehRepository.deleteTasbeeh(item.id)
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
