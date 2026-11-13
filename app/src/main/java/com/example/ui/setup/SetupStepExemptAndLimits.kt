package com.example.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.data.FocusProfilesRepository
import com.example.data.LockRepository
import com.example.model.BlockerStyle
import com.example.ui.lock.AppDailyLimitRow
import com.example.ui.theme.OrnateIslamicCard

@Composable
fun SetupStepExemptAndLimits(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val rules by LockRepository.appRules.collectAsState()
    val limits by FocusProfilesRepository.appDailyLimitMins.collectAsState()
    val usageSec by FocusProfilesRepository.appUsageSecondsToday.collectAsState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        FocusProfilesRepository.init(context)
        LockRepository.refreshInstalledApps(context)
    }

    val filtered = remember(rules, query) {
        val q = query.trim().lowercase()
        val list = if (q.isBlank()) rules else rules.filter {
            it.appName.lowercase().contains(q) || it.packageName.lowercase().contains(q)
        }
        list.take(25)
    }

    Column(
        modifier = modifier.fillMaxWidth().testTag("setup_step_exempt_limits"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OrnateIslamicCard {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Apps, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Text(
                        text = if (isArabic) "التطبيقات المستثناة والحدود اليومية (${rules.size})"
                        else "Exempt Apps & Daily Limits (${rules.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (isArabic) "الخطوة ٦: اختر التطبيقات الحقيقية على جهازك للاستثناء (وايت ليست) أو التقييد، وحدد الحد اليومي بالدقائق يدوياً."
                    else "Step 6: Pick real device apps to Exempt or Restrict, and set custom manual daily minute budgets.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    placeholder = { Text(if (isArabic) "ابحث في تطبيقات الجهاز..." else "Search device apps...", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("setup_apps_search_input")
                )
            }
        }

        filtered.forEach { app ->
            OrnateIslamicCard {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(app.appName, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                            Text(app.packageName, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(
                                selected = app.blockerStyle == BlockerStyle.ALLOWED,
                                onClick = { LockRepository.updateAppRule(app.packageName, BlockerStyle.ALLOWED, false, context) },
                                label = { Text(if (isArabic) "مستثنى" else "Exempt", fontSize = 10.sp) }
                            )
                            FilterChip(
                                selected = app.blockerStyle == BlockerStyle.STRICT,
                                onClick = { LockRepository.updateAppRule(app.packageName, BlockerStyle.STRICT, true, context) },
                                label = { Text(if (isArabic) "مقيّد" else "Locked", fontSize = 10.sp) }
                            )
                        }
                    }
                    AppDailyLimitRow(
                        rule = app,
                        currentLimitMins = limits[app.packageName] ?: 0,
                        usedSeconds = usageSec[app.packageName] ?: 0,
                        isArabic = isArabic
                    )
                }
            }
        }
    }
}
