package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.FocusProfileMode
import com.example.data.FocusProfilesRepository
import com.example.data.LockRepository
import com.example.util.AppStrings

@Composable
fun FocusProfilesSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val active by FocusProfilesRepository.activeProfile.collectAsState()
    val autoFri by FocusProfilesRepository.autoFridayMode.collectAsState()
    val autoPrayer by FocusProfilesRepository.autoPrayerFocus.collectAsState()
    val limits by FocusProfilesRepository.appDailyLimitMins.collectAsState()
    val usageSec by FocusProfilesRepository.appUsageSecondsToday.collectAsState()
    val rules by LockRepository.appRules.collectAsState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { LockRepository.refreshInstalledApps(context) }

    val filteredRules = remember(rules, query) {
        val q = query.trim().lowercase()
        val list = if (q.isBlank()) rules else rules.filter {
            it.appName.lowercase().contains(q) || it.packageName.lowercase().contains(q)
        }
        list.take(20)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "أنماط التركيز وحدود التطبيقات" else "Focus Profiles & App Budgets", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                FocusProfileMode.entries.forEach { mode ->
                    FilterChip(
                        selected = active == mode,
                        onClick = { FocusProfilesRepository.setProfile(context, mode) },
                        label = {
                            Column(Modifier.padding(vertical = 4.dp)) {
                                Text(if (isAr) mode.labelAr else mode.labelEn, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(if (isAr) mode.descAr else mode.descEn, fontSize = 11.sp)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "تفعيل وضع الجمعة والكهف تلقائياً" else "Auto Friday Kahf Mode", fontSize = 12.sp)
                    Switch(checked = autoFri, onCheckedChange = { FocusProfilesRepository.setAutoFridayMode(context, it) })
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "حجب تلقائي للتطبيقات وقت الصلاة" else "Auto App Focus at Prayer", fontSize = 12.sp)
                    Switch(checked = autoPrayer, onCheckedChange = { FocusProfilesRepository.setAutoPrayerFocus(context, it) })
                }

                HorizontalDivider()
                Text(if (isAr) "الميزانية اليومية للتطبيقات (يدوياً أو سريعاً):" else "Per-App Daily Limits (Manual or Quick):", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    placeholder = { Text(if (isAr) "بحث عن تطبيق..." else "Search installed app...", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                filteredRules.forEach { rule ->
                    AppDailyLimitRow(
                        rule = rule,
                        currentLimitMins = limits[rule.packageName] ?: 0,
                        usedSeconds = usageSec[rule.packageName] ?: 0,
                        isArabic = isAr
                    )
                }
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(if (isAr) "تم" else "Done")
                }
            }
        }
    }
}
