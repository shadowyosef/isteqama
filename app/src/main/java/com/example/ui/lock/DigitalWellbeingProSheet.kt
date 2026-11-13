package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.MindfulGateRepository
import com.example.ui.theme.OrnateIslamicCard
import com.example.util.AppStrings

@Composable
fun DigitalWellbeingProSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    LaunchedEffect(Unit) { MindfulGateRepository.init(context) }

    val gateEnabled by MindfulGateRepository.mindfulGateEnabled.collectAsState()
    val freezeEnabled by MindfulGateRepository.prayerFreezeEnabled.collectAsState()
    val freezeMins by MindfulGateRepository.prayerFreezeMins.collectAsState()
    val bedtimeEnabled by MindfulGateRepository.bedtimeEnabled.collectAsState()
    val savedMins by MindfulGateRepository.savedMinutes.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        OrnateIslamicCard(cornerRadius = 22.dp, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Spa, null, tint = MaterialTheme.colorScheme.primary)
                        Text(if (isAr) "الانضباط الرقمي والخشوع" else "Mindful Digital Shield", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                }

                Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(if (isAr) "الوقت المحفوظ من التصفح المُلهي" else "Distraction Time Saved", fontSize = 11.sp)
                        Text("$savedMins ${if (isAr) "دقيقة محفوظة بفضل الله" else "minutes saved"}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (isAr) "بوابة «ثانية قبل الإدمان»" else "Mindful Pause Gate", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(if (isAr) "مهلة ٥ ثوانٍ + استغفار قبل فتح التطبيقات المقيدة" else "5s mindful pause & Istighfar before restricted apps", fontSize = 10.sp)
                    }
                    Switch(checked = gateEnabled, onCheckedChange = { MindfulGateRepository.setMindfulGate(context, it) })
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (isAr) "التجميد الإلزامي وقت الصلاة" else "Prayer Time App Freeze", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(if (isAr) "حظر التطبيقات الترفيهية فور رفع الأذان" else "Lock distracting apps automatically at Adhan", fontSize = 10.sp)
                    }
                    Switch(checked = freezeEnabled, onCheckedChange = { MindfulGateRepository.setPrayerFreeze(context, it, freezeMins) })
                }
                if (freezeEnabled) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(15, 20, 30).forEach { m ->
                            FilterChip(selected = freezeMins == m, onClick = { MindfulGateRepository.setPrayerFreeze(context, true, m) }, label = { Text("${m}م", fontSize = 11.sp) })
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (isAr) "قفل النوم + أذكار الاستيقاظ" else "Bedtime Auto-Lock & Wake Duaa", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(if (isAr) "من ١١ ليلاً حتى ٥ فجراً لحماية صلاة الفجر" else "Locks nightlife scroll 23:00 - 05:00", fontSize = 10.sp)
                    }
                    Switch(checked = bedtimeEnabled, onCheckedChange = { MindfulGateRepository.setBedtimeConfig(context, it) })
                }
            }
        }
    }
}
