package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppBackupMetadata

@Composable
fun BackupItemRow(
    item: AppBackupMetadata,
    onRestore: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.formattedDate, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("${item.sizeBytes / 1024} KB • ${item.fileName}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                IconButton(onClick = onRestore, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Restore, contentDescription = "Restore", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
