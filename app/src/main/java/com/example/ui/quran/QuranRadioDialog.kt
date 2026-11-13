package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.QuranRadioCatalog
import com.example.ui.theme.OrnateIslamicCard
import com.example.util.AppStrings
import com.example.util.QuranRadioPlayer

@Composable
fun QuranRadioDialog(onDismiss: () -> Unit) {
    val isAr = AppStrings.isArabic
    val active by QuranRadioPlayer.activeStation.collectAsState()
    val isPlaying by QuranRadioPlayer.isPlaying.collectAsState()
    val isBuffering by QuranRadioPlayer.isBuffering.collectAsState()
    val err by QuranRadioPlayer.errorMessage.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        OrnateIslamicCard(cornerRadius = 22.dp, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Radio, null, tint = MaterialTheme.colorScheme.primary)
                        Text(if (isAr) "إذاعات القرآن الكريم المباشرة" else "Live Quran Radio", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                }

                err?.let { Text(it, fontSize = 11.sp, color = MaterialTheme.colorScheme.error) }

                QuranRadioCatalog.stations.forEach { station ->
                    val isCurrent = active?.id == station.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(if (isAr) station.nameAr else station.nameEn, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(station.descAr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (isCurrent && isBuffering) {
                                CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                            } else {
                                IconButton(
                                    onClick = { if (isCurrent && isPlaying) QuranRadioPlayer.stop() else QuranRadioPlayer.playStation(station) },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        if (isCurrent && isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Stop",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
