package com.example.ui.prayer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FastingTrackerRepository
import com.example.util.AppStrings

@Composable
fun VoluntaryFastingCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val fasts by FastingTrackerRepository.completedFasts.collectAsState()

    LaunchedEffect(Unit) {
        FastingTrackerRepository.init(context)
    }

    val isFastedToday = FastingTrackerRepository.isFastedToday()
    val (recommendation, note) = remember { FastingTrackerRepository.getTodayFastingRecommendation() }

    Card(
        modifier = modifier.fillMaxWidth().testTag("voluntary_fasting_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFastedToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        if (isFastedToday) Icons.Default.DarkMode else Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isAr) "صيام التطوع والأيام الفاضلة" else "Voluntary Fasting Tracker",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Badge(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
                    Text(
                        text = "${fasts.size} ${if (isAr) "أيام صيام" else "Fasted Days"}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            if (recommendation != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isAr) "🌙 ${recommendation.labelAr}: $note" else "🌙 ${recommendation.labelEn}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp),
                        lineHeight = 16.sp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isFastedToday) (if (isAr) "صائم اليوم بإذن الله 🤲" else "Fasting Today ✓")
                        else (if (isAr) "هل أنت صائم اليوم؟" else "Are you fasting today?"),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAr) "سجل صيامك ليُحسب في سجل العبادات" else "Log fast to your worship record",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(
                    onClick = { FastingTrackerRepository.toggleFastedToday(context) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFastedToday) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isFastedToday) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isAr) "مُسجل صائم ✓" else "Fasted ✓", fontSize = 11.sp)
                    } else {
                        Text(if (isAr) "أنا صائم اليوم" else "Mark Fasted", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
