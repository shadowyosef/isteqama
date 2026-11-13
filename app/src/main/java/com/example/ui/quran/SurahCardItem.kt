package com.example.ui.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SurahInfo
import com.example.util.AppStrings

@Composable
fun SurahCardItem(
    surah: SurahInfo,
    onInfoClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    com.example.ui.theme.OrnateIslamicCard(
        cornerRadius = 16.dp,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .testTag("surah_item_${surah.number}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), modifier = Modifier.size(44.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("${surah.number}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 15.sp)
                    }
                }
                Column {
                    Text(if (AppStrings.isArabic) surah.displayNameAr else surah.displayNameEn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${surah.versesCount} ${AppStrings.get("آية", "verses")} • ${surah.revelationType} • ${AppStrings.get("ص", "p.")} ${surah.pageStart}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(surah.nameAr, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                if (onInfoClick != null) {
                    IconButton(onClick = onInfoClick, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Info, contentDescription = "Surah Info", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
