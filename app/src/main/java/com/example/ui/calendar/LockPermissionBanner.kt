package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AppStrings
import com.example.util.OnLifecycleResumeEffect
import com.example.util.RequestPermissionsManager

@Composable
fun LockPermissionBanner(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasOverlay by remember { mutableStateOf(RequestPermissionsManager.hasOverlayPermission(context)) }
    var hasUsage by remember { mutableStateOf(RequestPermissionsManager.hasUsageStatsPermission(context)) }
    var hasAdmin by remember { mutableStateOf(RequestPermissionsManager.hasDeviceAdminPermission(context)) }
    var hasBattery by remember { mutableStateOf(RequestPermissionsManager.hasBatteryOptimizationExemption(context)) }

    fun refreshPermissions() {
        hasOverlay = RequestPermissionsManager.hasOverlayPermission(context)
        hasUsage = RequestPermissionsManager.hasUsageStatsPermission(context)
        hasAdmin = RequestPermissionsManager.hasDeviceAdminPermission(context)
        hasBattery = RequestPermissionsManager.hasBatteryOptimizationExemption(context)
    }

    OnLifecycleResumeEffect { refreshPermissions() }

    val allGranted = hasOverlay && hasUsage && hasAdmin && hasBattery

    Card(
        modifier = modifier.fillMaxWidth().testTag("lock_permission_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (allGranted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (allGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (allGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (allGranted) AppStrings.get("جميع الصلاحيات نشطة وتعمل بالخلفية", "All background lock permissions active")
                    else AppStrings.get("صلاحيات مطلوبة لمنع الإلغاء والعمل بالخلفية", "Permissions needed for unbypassable background lock"),
                    fontSize = 12.sp,
                    color = if (allGranted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                )
            }

            if (!allGranted) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!hasOverlay) {
                        OutlinedButton(
                            onClick = { RequestPermissionsManager.requestOverlayPermission(context) },
                            modifier = Modifier.weight(1f).height(36.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text("الظهور", fontSize = 11.sp)
                        }
                    }
                    if (!hasUsage) {
                        OutlinedButton(
                            onClick = { RequestPermissionsManager.requestUsageStatsPermission(context) },
                            modifier = Modifier.weight(1f).height(36.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text("الاستخدام", fontSize = 11.sp)
                        }
                    }
                    if (!hasAdmin) {
                        OutlinedButton(
                            onClick = { RequestPermissionsManager.requestDeviceAdminPermission(context) },
                            modifier = Modifier.weight(1f).height(36.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text("الحماية", fontSize = 11.sp)
                        }
                    }
                    if (!hasBattery) {
                        OutlinedButton(
                            onClick = { RequestPermissionsManager.requestBatteryOptimizationExemption(context) },
                            modifier = Modifier.weight(1f).height(36.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text("البطارية", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
