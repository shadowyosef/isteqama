package com.example.ui.setup

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
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
import com.example.data.AppPreferencesManager
import com.example.ui.theme.OrnateIslamicCard
import com.example.ui.theme.OrnateIslamicTonalButton
import com.example.util.RequestPermissionsManager

@Composable
fun SetupStepPermissions(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isArabic by AppPreferencesManager.isArabic.collectAsState()

    var hasOverlay by remember { mutableStateOf(RequestPermissionsManager.hasOverlayPermission(context)) }
    var hasBattery by remember { mutableStateOf(RequestPermissionsManager.hasBatteryOptimizationExemption(context)) }
    var hasAlarm by remember { mutableStateOf(RequestPermissionsManager.hasExactAlarmPermission(context)) }
    var hasNotif by remember { mutableStateOf(RequestPermissionsManager.hasNotificationPermission(context)) }
    var hasUsage by remember { mutableStateOf(RequestPermissionsManager.hasUsageStatsPermission(context)) }

    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasNotif = granted || RequestPermissionsManager.hasNotificationPermission(context)
    }

    fun refresh() {
        hasOverlay = RequestPermissionsManager.hasOverlayPermission(context)
        hasBattery = RequestPermissionsManager.hasBatteryOptimizationExemption(context)
        hasAlarm = RequestPermissionsManager.hasExactAlarmPermission(context)
        hasNotif = RequestPermissionsManager.hasNotificationPermission(context)
        hasUsage = RequestPermissionsManager.hasUsageStatsPermission(context)
    }

    com.example.util.OnLifecycleResumeEffect { refresh() }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = if (isArabic) "امنح الأذونات التالية ليظهر القفل والتنبيهات في موعدها دون توقف."
            else "Grant these permissions so locks, prayer windows, and reminders run reliably.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SetupPermissionCard(
            title = if (isArabic) "الظهور فوق التطبيقات (Overlay)" else "Display Over Apps",
            desc = if (isArabic) "لإظهار شاشة الورد وبوابة التمهل فوق التطبيقات" else "Shows lock & mindful gate overlays",
            isGranted = hasOverlay,
            isArabic = isArabic,
            onGrant = { RequestPermissionsManager.requestOverlayPermission(context) }
        )
        SetupPermissionCard(
            title = if (isArabic) "إذن الإشعارات والتنبيهات" else "Notifications Alert",
            desc = if (isArabic) "لتنبيهات الأذان والأذكار والتسابيح العائمة" else "Adhan alerts, Adhkar & Tasbeeh",
            isGranted = hasNotif,
            isArabic = isArabic,
            onGrant = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        )
        SetupPermissionCard(
            title = if (isArabic) "استثناء توفير البطارية" else "Ignore Battery Optimization",
            desc = if (isArabic) "يمنع النظام من إيقاف المنبه في الخلفية" else "Prevents OS from killing background service",
            isGranted = hasBattery,
            isArabic = isArabic,
            onGrant = { RequestPermissionsManager.requestBatteryOptimizationExemption(context) }
        )
        SetupPermissionCard(
            title = if (isArabic) "المنبهات الدقيقة (Exact Alarms)" else "Exact Alarms",
            desc = if (isArabic) "لدقة مواقيت الصلاة وأقفال الورد بالثانية" else "Exact timing for prayer & ward locks",
            isGranted = hasAlarm,
            isArabic = isArabic,
            onGrant = { RequestPermissionsManager.requestExactAlarmPermission(context) }
        )
        SetupPermissionCard(
            title = if (isArabic) "مراقبة استخدام التطبيقات" else "App Usage Access",
            desc = if (isArabic) "لبوابة التمهل عند فتح تطبيقات التواصل" else "Optional: enables Mindful App Gate",
            isGranted = hasUsage,
            isArabic = isArabic,
            onGrant = { RequestPermissionsManager.requestUsageStatsPermission(context) }
        )

        OrnateIslamicTonalButton(
            onClick = { refresh() },
            modifier = Modifier.fillMaxWidth().testTag("recheck_perms_btn")
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(if (isArabic) "تحديث فحص حالة الأذونات" else "Recheck Permissions Status", fontSize = 12.sp)
        }
    }
}

@Composable
private fun SetupPermissionCard(
    title: String,
    desc: String,
    isGranted: Boolean,
    isArabic: Boolean,
    onGrant: () -> Unit
) {
    OrnateIslamicCard(cornerRadius = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (isGranted) {
                Text(if (isArabic) "مفعّل" else "Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            } else {
                Button(onClick = onGrant, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(if (isArabic) "تفعيل" else "Grant", fontSize = 11.sp)
                }
            }
        }
    }
}
