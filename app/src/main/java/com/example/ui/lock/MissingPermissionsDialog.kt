package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.window.Dialog
import com.example.data.AppPreferencesManager
import com.example.util.RequestPermissionsManager

@Composable
fun MissingPermissionsDialog(
    onDismiss: () -> Unit,
    onProceedWithLock: () -> Unit
) {
    val context = LocalContext.current
    val isArabic by AppPreferencesManager.isArabic.collectAsState()

    var hasOverlay by remember { mutableStateOf(RequestPermissionsManager.hasOverlayPermission(context)) }
    var hasNotif by remember { mutableStateOf(RequestPermissionsManager.hasNotificationPermission(context)) }
    var hasBattery by remember { mutableStateOf(RequestPermissionsManager.hasBatteryOptimizationExemption(context)) }
    var hasAlarm by remember { mutableStateOf(RequestPermissionsManager.hasExactAlarmPermission(context)) }

    fun refresh() {
        hasOverlay = RequestPermissionsManager.hasOverlayPermission(context)
        hasNotif = RequestPermissionsManager.hasNotificationPermission(context)
        hasBattery = RequestPermissionsManager.hasBatteryOptimizationExemption(context)
        hasAlarm = RequestPermissionsManager.hasExactAlarmPermission(context)
    }

    com.example.util.OnLifecycleResumeEffect { refresh() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().testTag("missing_permissions_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isArabic) "صلاحيات القفل والتنبيهات" else "Permissions Required", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Text(
                    text = if (isArabic) "لضمان عمل قفل الشاشة والتوست العائم والإشعارات بشكل كامل:" else "To ensure full lock, floating toast, and alerts functionality:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                PermissionRowItem(if (isArabic) "الظهور فوق التطبيقات (Overlay)" else "Draw Over Apps", hasOverlay) {
                    RequestPermissionsManager.requestOverlayPermission(context)
                }
                PermissionRowItem(if (isArabic) "إرسال الإشعارات والتنبيهات" else "Notifications Alert", hasNotif) {
                    val intent = android.content.Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try { context.startActivity(intent) } catch (_: Exception) {}
                }
                PermissionRowItem(if (isArabic) "استثناء البطارية بالخلفية" else "Ignore Battery Optimization", hasBattery) {
                    RequestPermissionsManager.requestBatteryOptimizationExemption(context)
                }
                PermissionRowItem(if (isArabic) "المنبهات الدقيقة (Exact Alarms)" else "Exact Alarms Schedule", hasAlarm) {
                    RequestPermissionsManager.requestExactAlarmPermission(context)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { refresh() }) { Text(if (isArabic) "تحديث" else "Refresh") }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = { refresh(); onProceedWithLock() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("execute_real_lock_btn")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isArabic) "قفل الشاشة الآن" else "Lock Screen Now")
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionRowItem(title: String, isGranted: Boolean, onGrant: () -> Unit) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (isGranted) Icons.Default.CheckCircle else Icons.Default.Warning, null, tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
        if (!isGranted) {
            OutlinedButton(onClick = onGrant, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp), modifier = Modifier.height(28.dp)) {
                Text(if (isArabic) "تفعيل" else "Grant", fontSize = 10.sp)
            }
        }
    }
}
