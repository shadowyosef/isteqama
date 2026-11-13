package com.example.ui.quran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.data.StatsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReadingStatsDashboard(
    surahNumber: Int,
    currentAyah: Int,
    totalAyahsInSurah: Int,
    onDismiss: () -> Unit
) {
    val isAr by AppPreferencesManager.isArabic.collectAsState()
    val stats by StatsRepository.stats.collectAsState()
    var showMonthly by remember { mutableStateOf(false) }

    val daysLabels = if (isAr) listOf("سبت", "أحد", "إثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة")
    else listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
    val weeksLabels = if (isAr) listOf("أسبوع 1", "أسبوع 2", "أسبوع 3", "أسبوع 4") else listOf("W1", "W2", "W3", "W4")

    val surahCompletion = (currentAyah.toFloat() / totalAyahsInSurah.coerceAtLeast(1) * 100f).coerceIn(0f, 100f)
    val estimatedHasanaat = (stats.totalAyahsRead.coerceAtLeast(stats.ayahsReadToday) * 45L) * 10L
    val chartData = if (!showMonthly) stats.weeklyMinutes else listOf(
        stats.weeklyMinutes.sum().coerceAtLeast(25),
        (stats.weeklyMinutes.sum() * 1.15f).toInt(),
        (stats.weeklyMinutes.sum() * 0.9f).toInt(),
        (stats.totalQuranMinutes).coerceAtLeast(stats.weeklyMinutes.sum())
    )
    val chartLabels = if (!showMonthly) daysLabels else weeksLabels

    ModalBottomSheet(onDismissRequest = onDismiss, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(if (isAr) "لوحة إنجاز التلاوة والحسنات" else "Quran & Hasanaat Dashboard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (isAr) "آيات اليوم" else "Today Ayahs", fontSize = 10.sp)
                        Text("${stats.ayahsReadToday}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (isAr) "الحسنات التقديرية" else "Est. Hasanaat", fontSize = 10.sp)
                        Text("+$estimatedHasanaat", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (isAr) "الختمة" else "Khatmah", fontSize = 10.sp)
                        Text("%.1f%%".format(stats.khatmaPercentage), fontWeight = FontWeight.Bold, fontSize = 17.sp, color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${if (isAr) "السورة الحالية" else "Surah"} #$surahNumber", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("$currentAyah/$totalAyahsInSurah (%.0f%%)".format(surahCompletion), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    LinearProgressIndicator(progress = { (surahCompletion / 100f).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(if (isAr) "مخطط القراءة:" else "Reading Chart:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(selected = !showMonthly, onClick = { showMonthly = false }, label = { Text(if (isAr) "أسبوعي" else "Weekly", fontSize = 10.sp) })
                    FilterChip(selected = showMonthly, onClick = { showMonthly = true }, label = { Text(if (isAr) "شهري" else "Monthly", fontSize = 10.sp) })
                }
            }

            Row(modifier = Modifier.fillMaxWidth().height(110.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                val maxMins = (chartData.maxOrNull() ?: 60).coerceAtLeast(30).toFloat()
                chartData.forEachIndexed { idx, mins ->
                    val frac = (mins / maxMins).coerceIn(0.1f, 1f)
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Text("$mins", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Box(modifier = Modifier.width(22.dp).height((72 * frac).dp).clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)).background(MaterialTheme.colorScheme.primary))
                        Text(chartLabels.getOrElse(idx) { "$idx" }, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
