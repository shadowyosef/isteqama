package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.LockRepository
import com.example.model.BlockerStyle
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBlockerConfigSheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val appRules by LockRepository.appRules.collectAsState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { LockRepository.refreshInstalledApps(context) }

    val filtered = remember(appRules, query) {
        val q = query.trim().lowercase()
        if (q.isBlank()) appRules
        else appRules.filter { it.appName.lowercase().contains(q) || it.packageName.lowercase().contains(q) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("app_blocker_sheet"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = AppStrings.whitelistApps,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (isAr) "التطبيقات الحقيقية المثبتة على جهازك (${appRules.size}): حدد المستثناة أو المقيدة"
                else "Installed apps on device (${appRules.size}): choose Whitelist or Strict lock",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                placeholder = { Text(if (isAr) "ابحث باسم التطبيق..." else "Search installed apps...", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("app_blocker_search")
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered, key = { it.packageName }) { app ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(app.appName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1)
                                Text(app.packageName, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), maxLines = 1)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = app.blockerStyle == BlockerStyle.ALLOWED,
                                    onClick = { LockRepository.updateAppRule(app.packageName, BlockerStyle.ALLOWED, false, context) },
                                    label = { Text(if (isAr) "وايت ليست" else "Exempt", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = app.blockerStyle == BlockerStyle.STRICT,
                                    onClick = { LockRepository.updateAppRule(app.packageName, BlockerStyle.STRICT, true, context) },
                                    label = { Text(if (isAr) "صارم" else "Strict", fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("close_app_blocker_btn")
            ) {
                Text(AppStrings.close)
            }
        }
    }
}
