package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import com.example.util.AppStrings
import com.example.util.OnLifecycleResumeEffect
import com.example.util.RequestPermissionsManager

@Composable
fun PermissionsSection() {
    val context = LocalContext.current
    val isAr by AppPreferencesManager.isArabic.collectAsState()
    var hasOverlay by remember { mutableStateOf(RequestPermissionsManager.hasOverlayPermission(context)) }
    var hasUsage by remember { mutableStateOf(RequestPermissionsManager.hasUsageStatsPermission(context)) }
    var hasAdmin by remember { mutableStateOf(RequestPermissionsManager.hasDeviceAdminPermission(context)) }
    var hasBattery by remember { mutableStateOf(RequestPermissionsManager.hasBatteryOptimizationExemption(context)) }
    var hasAlarm by remember { mutableStateOf(RequestPermissionsManager.hasExactAlarmPermission(context)) }

    fun refresh() {
        hasOverlay = RequestPermissionsManager.hasOverlayPermission(context)
        hasUsage = RequestPermissionsManager.hasUsageStatsPermission(context)
        hasAdmin = RequestPermissionsManager.hasDeviceAdminPermission(context)
        hasBattery = RequestPermissionsManager.hasBatteryOptimizationExemption(context)
        hasAlarm = RequestPermissionsManager.hasExactAlarmPermission(context)
    }

    OnLifecycleResumeEffect { refresh() }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(AppStrings.permissions, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        PermissionRow(
            title = if (isAr) "الظهور فوق التطبيقات (Overlay)" else "Display Over Apps",
            subtitle = if (isAr) "لعرض شاشة القفل والورد عند فتح التطبيقات المحظورة" else "Display lock overlay over apps",
            isGranted = hasOverlay, isAr = isAr, tag = "grant_overlay_btn",
            onAction = { RequestPermissionsManager.requestOverlayPermission(context) }
        )
        PermissionRow(
            title = if (isAr) "مراقبة التطبيقات (Usage Stats)" else "App Usage Stats",
            subtitle = if (isAr) "لاكتشاف فتح التطبيقات المحظورة وتفعيل الحظر فوراً" else "Detect blocked app launches",
            isGranted = hasUsage, isAr = isAr, tag = "grant_usage_btn",
            onAction = { RequestPermissionsManager.requestUsageStatsPermission(context) }
        )
        PermissionRow(
            title = if (isAr) "استثناء توفير البطارية" else "Battery Optimization Exemption",
            subtitle = if (isAr) "لضمان استمرار المنبهات والأوراد بالخلفية" else "Keep schedules active in background",
            isGranted = hasBattery, isAr = isAr, tag = "grant_battery_btn",
            onAction = { RequestPermissionsManager.requestBatteryOptimizationExemption(context) }
        )
        PermissionRow(
            title = if (isAr) "المنبهات الدقيقة (Exact Alarms)" else "Exact Alarms",
            subtitle = if (isAr) "لتوقيت مواقيت الصلاة والأوراد بالثانية" else "Trigger prayer & lock alarms accurately",
            isGranted = hasAlarm, isAr = isAr, tag = "grant_alarm_btn",
            onAction = { RequestPermissionsManager.requestExactAlarmPermission(context) }
        )
        PermissionRow(
            title = if (isAr) "مسؤول الجهاز (Device Admin)" else "Device Admin Guard",
            subtitle = if (isAr) "لمنع إلغاء تثبيت التطبيق أو إيقافه إجبارياً أثناء القفل" else "Prevent force-stop during lock",
            isGranted = hasAdmin, isAr = isAr, tag = "grant_admin_btn",
            onAction = { RequestPermissionsManager.requestDeviceAdminPermission(context) }
        )
    }
}

@Composable
private fun PermissionRow(title: String, subtitle: String, isGranted: Boolean, isAr: Boolean, tag: String, onAction: () -> Unit) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            if (isGranted) {
                AssistChip(
                    onClick = {},
                    label = { Text(if (isAr) "مفعّلة" else "Granted", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                )
            } else {
                Button(onClick = onAction, shape = RoundedCornerShape(8.dp), modifier = Modifier.heightIn(min = 48.dp).testTag(tag)) {
                    Text(if (isAr) "تفعيل" else "Grant", fontSize = 11.sp)
                }
            }
        }
    }
}
