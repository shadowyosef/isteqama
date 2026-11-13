package com.example.ui.prayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MissedPrayerRepository
import com.example.model.MissedPrayerType
import com.example.util.AppStrings

@Composable
fun MissedPrayersTrackerCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val missedMap by MissedPrayerRepository.missedPrayers.collectAsState()

    LaunchedEffect(Unit) {
        MissedPrayerRepository.init(context)
    }

    val total = missedMap.values.sum()

    Card(
        modifier = modifier.fillMaxWidth().testTag("missed_prayers_tracker_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (isAr) "قضاء الفوائت من الصلوات" else "Missed Prayers Makeup",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Badge(containerColor = if (total > 0) MaterialTheme.colorScheme.error.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
                    Text(
                        text = "${if (isAr) "المتبقي:" else "Remaining:"} $total",
                        color = if (total > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            MissedPrayerType.values().forEach { type ->
                val count = missedMap[type] ?: 0
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) type.labelAr else type.labelEn,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { MissedPrayerRepository.addMissed(context, type, 1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "$count",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (count > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FilledTonalButton(
                            onClick = { MissedPrayerRepository.markPrayed(context, type) },
                            enabled = count > 0,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(if (isAr) "قضيت" else "Made up", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
