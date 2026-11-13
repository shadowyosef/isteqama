package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ScreenLockRepository
import com.example.data.local.entity.ScreenLockEntity
import com.example.ui.lock.ActiveLockActivity
import com.example.ui.lock.MissingPermissionsDialog
import com.example.util.AppStrings
import com.example.util.RequestPermissionsManager
import kotlinx.coroutines.delay

@Composable
fun LiveCountdownCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locks by ScreenLockRepository.locks.collectAsState()
    var nearestLock by remember { mutableStateOf<ScreenLockEntity?>(null) }
    var remainingMs by remember { mutableStateOf(-1L) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(locks) {
        while (true) {
            val (lock, diff) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                ScreenLockRepository.getRemainingMillisToNextLock(context)
            }
            nearestLock = lock
            remainingMs = diff
            delay(1000L)
        }
    }

    val targetLock = nearestLock ?: locks.firstOrNull() ?: ScreenLockEntity(
        id = "test_now",
        title = "تجربة القفل الإلزامي",
        lockType = "QURAN",
        targetAmount = 7
    )

    Card(
        modifier = modifier.fillMaxWidth().testTag("live_countdown_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.countdownRemaining,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = {
                        val hasOverlay = RequestPermissionsManager.hasOverlayPermission(context)
                        val hasBattery = RequestPermissionsManager.hasBatteryOptimizationExemption(context)
                        val hasAlarm = RequestPermissionsManager.hasExactAlarmPermission(context)
                        if (!hasOverlay || !hasBattery || !hasAlarm) {
                            showPermissionDialog = true
                        } else {
                            ActiveLockActivity.launch(context, targetLock)
                        }
                    },
                    modifier = Modifier.height(36.dp).testTag("trigger_test_kiosk_btn"),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(AppStrings.testLockNow, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (nearestLock != null && remainingMs > 0) {
                val totalSeconds = remainingMs / 1000
                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60
                val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                val primarySlot = nearestLock!!.getScheduleSlots().firstOrNull()
                val scheduleLabel = if (primarySlot != null && primarySlot.intervalMinutes > 0) {
                    primarySlot.formattedTime()
                } else {
                    "الساعة " + String.format("%02d:%02d", nearestLock!!.triggerHour, nearestLock!!.triggerMinute)
                }

                Text(
                    text = "${nearestLock!!.title} • $scheduleLabel",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Text(
                    text = formattedTime,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = AppStrings.noUpcomingLock,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }

    if (showPermissionDialog) {
        MissingPermissionsDialog(
            onDismiss = { showPermissionDialog = false },
            onProceedWithLock = {
                showPermissionDialog = false
                ActiveLockActivity.launch(context, targetLock)
            }
        )
    }
}
