package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.OrnateIslamicCard
import com.example.util.AmbientNatureType
import com.example.util.AmbientSoundPlayer
import com.example.util.AppStrings

@Composable
fun AmbientSoundSheet(onDismiss: () -> Unit) {
    val isAr = AppStrings.isArabic
    val current by AmbientSoundPlayer.currentSound.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        OrnateIslamicCard(cornerRadius = 22.dp, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.GraphicEq, null, tint = MaterialTheme.colorScheme.primary)
                        Text(if (isAr) "أصوات الخشوع والطبيعة" else "Ambient Nature Sounds", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                }
                Text(if (isAr) "أصوات هادئة أثناء تلاوة الورد أو قيام الليل:" else "Gentle natural soundscape for night Qiyam:", fontSize = 11.sp)
                AmbientNatureType.values().forEach { item ->
                    val selected = current == item
                    FilledTonalButton(
                        onClick = { AmbientSoundPlayer.play(item) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    ) {
                        Text("${if (selected) "✓ " else ""}${if (isAr) item.labelAr else item.labelEn}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
