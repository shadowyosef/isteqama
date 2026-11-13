package com.example.ui.prayer

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyProgressRepository
import com.example.model.SunnahType
import com.example.util.AppStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SunanTrackerSection() {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val prefs = remember { context.getSharedPreferences("sunan_prefs", Context.MODE_PRIVATE) }
    val todayKey = DailyProgressRepository.todayKey()

    var doneSet by remember {
        val saved = prefs.getStringSet("done_$todayKey", emptySet()) ?: emptySet()
        mutableStateOf(saved)
    }

    val totalRakahs = remember(doneSet) {
        SunnahType.values().filter { it.id in doneSet && it != SunnahType.DUHA && it != SunnahType.WITR && it != SunnahType.QIYAM }
            .sumOf { it.rakahs }
    }

    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isAr) "تتبع السنن الرواتب والنوافل" else "Sunan Rawatib Tracker",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Badge(containerColor = if (totalRakahs >= 12) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant) {
                    Text("$totalRakahs / 12 ${if (isAr) "ركعة" else "rak'ah"}", fontSize = 11.sp, modifier = Modifier.padding(2.dp))
                }
            }

            Text(
                text = if (isAr) "«من صلّى في يومٍ وليلةٍ ثنتي عشرةَ ركعةً بُنيَ لهُ بيتٌ في الجنةِ»" else "12 Sunan rak'ahs build a house in Jannah",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SunnahType.values().forEach { st ->
                    val isDone = st.id in doneSet
                    FilterChip(
                        selected = isDone,
                        onClick = {
                            val newSet = if (isDone) doneSet - st.id else doneSet + st.id
                            doneSet = newSet
                            prefs.edit().putStringSet("done_$todayKey", newSet).apply()
                        },
                        label = { Text("${if (isAr) st.nameAr else st.nameEn} (${st.rakahs})", fontSize = 10.sp) }
                    )
                }
            }
        }
    }
}
