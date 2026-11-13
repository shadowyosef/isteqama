package com.example.ui.quran

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.StatsRepository
import com.example.util.AppStrings
import com.example.util.KhatmaCatchUpHelper
import kotlin.math.ceil

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KhatmaPlannerDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val stats by StatsRepository.stats.collectAsState()
    val prefs = remember { context.getSharedPreferences("khatma_planner_prefs", Context.MODE_PRIVATE) }
    var targetDaysText by remember { mutableStateOf(prefs.getInt("target_days", 30).toString()) }
    val targetDays = (targetDaysText.toIntOrNull() ?: 30).coerceIn(1, 365)

    val pagesPerDay = remember(targetDays) { ceil(604.0 / targetDays).toInt() }
    val pagesPerPrayer = remember(pagesPerDay) { ceil(pagesPerDay / 5.0).toInt() }
    val actualPagesRead = remember(stats.khatmaProgressAyahs) { (stats.khatmaProgressAyahs / 10).coerceIn(0, 604) }
    val catchUp = remember(targetDays, actualPagesRead) { KhatmaCatchUpHelper.computeStatus(targetDays, 5, actualPagesRead) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "مخطط واستدراك الختمات" else "Khatma & Catch-Up Planner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Close, "Close", modifier = Modifier.size(18.dp)) }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = targetDaysText, onValueChange = { targetDaysText = it.filter { c -> c.isDigit() }.take(3) },
                        label = { Text(if (isAr) "أيام" else "Days", fontSize = 10.sp) }, singleLine = true,
                        modifier = Modifier.width(86.dp).testTag("quran_khatma_manual_days_input")
                    )
                    FlowRow(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(7, 15, 30, 60, 90).forEach { days ->
                            FilterChip(selected = targetDays == days, onClick = { targetDaysText = days.toString() }, label = { Text("$days", fontSize = 10.sp) })
                        }
                    }
                }

                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${if (isAr) "الورد المطلوب:" else "Daily Quota:"} $pagesPerDay ${if (isAr) "صفحة/يوم" else "pages/day"} ($pagesPerPrayer/${if (isAr) "صلاة" else "prayer"})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Text("${if (isAr) "الإنجاز الحالي:" else "Progress:"} ${stats.khatmaProgressAyahs} / 6236 (${String.format("%.1f%%", stats.khatmaPercentage)})", fontSize = 11.sp)
                    }
                }

                if (catchUp.deficitPages > 0) {
                    Text("${if (isAr) "خطة استدراك المتأخر (${catchUp.deficitPages} ص):" else "Catch-Up Options (${catchUp.deficitPages}p):"}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    catchUp.options.forEach { opt ->
                        OutlinedButton(onClick = { prefs.edit().putInt("catchup_pages_per_day", opt.newDailyPages).apply() }, modifier = Modifier.fillMaxWidth()) {
                            Text("${if (isAr) opt.titleAr else opt.titleEn} • ${if (isAr) opt.subtitleAr else opt.subtitleEn}", fontSize = 10.sp)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = { prefs.edit().putInt("target_days", targetDays).apply(); onDismiss() }, modifier = Modifier.testTag("save_khatma_planner_btn")) {
                        Text(if (isAr) "حفظ الخطة" else "Save Plan")
                    }
                }
            }
        }
    }
}
