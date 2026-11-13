package com.example.ui.adhkar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager

@Composable
fun DhikrSequenceInputItem(
    index: Int,
    text: String,
    countStr: String,
    canDelete: Boolean,
    onTextChange: (String) -> Unit,
    onCountChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text("${if (isArabic) "الذكر" else "Dhikr"} #${index + 1}", fontSize = 11.sp) },
            modifier = Modifier.weight(2f).testTag("dhikr_seq_text_$index")
        )

        OutlinedTextField(
            value = countStr,
            onValueChange = { onCountChange(it.filter { c -> c.isDigit() }) },
            label = { Text(if (isArabic) "العدد" else "Count", fontSize = 11.sp) },
            modifier = Modifier.weight(1f).testTag("dhikr_seq_count_$index")
        )

        if (canDelete) {
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            }
        }
    }
}
