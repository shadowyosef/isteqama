package com.example.ui.adhkar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UnifiedBankDhikrItem

@Composable
fun UnifiedDhikrItemCard(
    item: UnifiedBankDhikrItem,
    isSelected: Boolean,
    isArabic: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = Modifier.fillMaxWidth().clickable { onToggle(!isSelected) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onToggle,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isArabic) item.titleAr else item.titleEn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text("${item.targetCount}x", fontSize = 9.sp) },
                        modifier = Modifier.height(20.dp)
                    )
                }
                Text(
                    text = if (isArabic) item.textAr else item.textEn,
                    fontSize = 12.sp,
                    maxLines = 2,
                    textAlign = if (isArabic) TextAlign.Right else TextAlign.Left,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.virtue.isNotBlank()) {
                    Text(
                        text = item.virtue,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
