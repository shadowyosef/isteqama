package com.example.ui.unified

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoutineSequenceStepRow(
    index: Int,
    text: String,
    countStr: String,
    canDelete: Boolean,
    onTextChange: (String) -> Unit,
    onCountChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text("الذكر #${index + 1}", fontSize = 11.sp) },
            modifier = Modifier.weight(2f).testTag("routine_step_text_$index")
        )

        OutlinedTextField(
            value = countStr,
            onValueChange = { onCountChange(it.filter { c -> c.isDigit() }) },
            label = { Text("العدد", fontSize = 11.sp) },
            modifier = Modifier.weight(1f).testTag("routine_step_count_$index")
        )

        if (canDelete) {
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            }
        }
    }
}
