package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.util.AppStrings

@Composable
fun QiyamPlannerDialog(onDismiss: () -> Unit) {
    val isAr = AppStrings.isArabic
    var targetAyahs by remember { mutableStateOf(100) }
    var rakats by remember { mutableStateOf(4) }

    val ayahsPerRakat = (targetAyahs / rakats.coerceAtLeast(1)).coerceAtLeast(1)
    val levelLabel = when {
        targetAyahs >= 1000 -> if (isAr) "المقنطرين (١٠٠٠+ آية)" else "Al-Muqantirin (1000+)"
        targetAyahs >= 100 -> if (isAr) "القانتين (١٠٠+ آية)" else "Al-Qanitin (100+)"
        else -> if (isAr) "المستقيمين (١٠+ آيات)" else "Al-Mustaqimin (10+)"
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(if (isAr) "مخطط قيام الليل بالآيات" else "Qiyam al-Layl Verse Planner", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                Text(if (isAr) "عدد الآيات المستهدفة الليلة:" else "Target Ayahs Tonight:", fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(20, 50, 100, 300, 1000).forEach { cnt ->
                        FilterChip(selected = targetAyahs == cnt, onClick = { targetAyahs = cnt }, label = { Text("$cnt", fontSize = 11.sp) })
                    }
                }
                Text(if (isAr) "عدد الركعات:" else "Number of Rak'ats:", fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(2, 4, 6, 8, 11).forEach { r ->
                        FilterChip(selected = rakats == r, onClick = { rakats = r }, label = { Text("$r", fontSize = 11.sp) })
                    }
                }
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(levelLabel, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = if (isAr) "اقرأ حوالي $ayahsPerRakat آية في كل ركعة (${targetAyahs / 15} صفحات تقريباً)" else "~$ayahsPerRakat Ayahs per Rak'ah (~${targetAyahs / 15} pages)",
                            fontSize = 12.sp
                        )
                    }
                }
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text(if (isAr) "إغلاق" else "Close") }
            }
        }
    }
}
