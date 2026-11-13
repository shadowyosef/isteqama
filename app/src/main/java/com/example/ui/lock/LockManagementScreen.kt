package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Warning
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
import com.example.model.LockSchedule
import com.example.util.AppStrings
import com.example.util.RequestPermissionsManager

@Composable
fun LockManagementScreen() {
    val context = LocalContext.current
    val schedules by LockRepository.schedules.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showAppBlockerSheet by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<LockSchedule?>(null) }
    var allPermissions by remember { mutableStateOf(RequestPermissionsManager.allCorePermissionsGranted(context)) }
    com.example.util.OnLifecycleResumeEffect {
        allPermissions = RequestPermissionsManager.allCorePermissionsGranted(context)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_lock_schedule_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).testTag("lock_management_screen")
        ) {
            if (!allPermissions) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("يرجى تفعيل صلاحيات الظهور وحماية البطارية", fontSize = 12.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                        Button(
                            onClick = {
                                RequestPermissionsManager.requestOverlayPermission(context)
                                RequestPermissionsManager.requestBatteryOptimizationExemption(context)
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("تفعيل", fontSize = 11.sp)
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        val active = schedules.firstOrNull() ?: LockSchedule("demo", "ورد الفاتحة", com.example.model.LockType.QURAN_VERSES, targetAmount = 7)
                        LockRepository.triggerLock(active)
                    },
                    modifier = Modifier.weight(1f).testTag("trigger_test_lock_btn")
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تجربة القفل الآن", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { showAppBlockerSheet = true },
                    modifier = Modifier.weight(1f).testTag("app_blocker_whitelist_btn")
                ) {
                    Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("التطبيقات والوايت ليست", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(AppStrings.lockRules, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(schedules, key = { it.id }) { schedule ->
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("schedule_card_${schedule.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(schedule.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("الوقت: ${com.example.util.TimeFormatUtils.format12Hour(schedule.triggerHour, schedule.triggerMinute, true)} • الهدف: ${schedule.targetAmount}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                                Text("${schedule.blockerStyle.name} • ${schedule.lockType.name}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { editingSchedule = schedule }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                                Switch(checked = schedule.isEnabled, onCheckedChange = { LockRepository.toggleSchedule(schedule.id) })
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) CreateLockDialog(onDismiss = { showCreateDialog = false })
    if (showAppBlockerSheet) AppBlockerConfigSheet(onDismiss = { showAppBlockerSheet = false })
    editingSchedule?.let { EditLockScheduleDialog(schedule = it, onDismiss = { editingSchedule = null }) }
}
