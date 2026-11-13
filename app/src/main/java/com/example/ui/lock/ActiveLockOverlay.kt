package com.example.ui.lock

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.LockRepository
import com.example.data.StatsRepository
import com.example.model.LockSchedule
import com.example.model.LockType
import com.example.ui.theme.islamicGeometricPattern
import com.example.util.AppStrings

@Composable
fun ActiveLockOverlay(
    schedule: LockSchedule
) {
    var progressCount by remember { mutableStateOf(0) }
    var showEmergencyDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {}

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF206150E))
                .islamicGeometricPattern(
                    primary = MaterialTheme.colorScheme.primary,
                    secondary = MaterialTheme.colorScheme.secondary,
                    stepPx = 92f,
                    alpha = 0.14f
                )
                .padding(24.dp)
                .testTag("active_lock_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().widthIn(max = 500.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
                Text(
                    text = schedule.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = AppStrings.warningLockActive,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                if (schedule.lockType == LockType.QURAN_VERSES) {
                    ActiveLockContentCard(
                        schedule = schedule,
                        progressCount = progressCount
                    )

                    Button(
                        onClick = {
                            val next = progressCount + 1
                            progressCount = next
                            StatsRepository.recordAyahsRead(1)
                            if (next >= schedule.targetAmount) {
                                LockRepository.completeLock()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("progress_lock_btn"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Read")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (progressCount + 1 >= schedule.targetAmount) AppStrings.complete
                            else "قرأت وأتممت (${progressCount + 1}/${schedule.targetAmount})"
                        )
                    }
                } else {
                    val adhkar = remember(schedule.id, schedule.title) {
                        com.example.util.RoutineAdhkarResolver.getAdhkarForRoutine(schedule.id, schedule.title)
                    }
                    SequentialDhikrLockRunner(
                        routineTitle = schedule.title,
                        items = adhkar,
                        onAllCompleted = {
                            StatsRepository.recordDhikrCount(adhkar.sumOf { it.targetCount })
                            LockRepository.completeLock()
                        }
                    )
                }

                TextButton(
                    onClick = { showEmergencyDialog = true },
                    modifier = Modifier.testTag("emergency_bypass_btn")
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Emergency", tint = Color(0xFFFFB74D), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(AppStrings.emergencyBypass, color = Color(0xFFFFB74D))
                }
            }
        }
    }

    if (showEmergencyDialog) {
        PinProtectedBypassDialog(
            onSuccess = {
                showEmergencyDialog = false
                LockRepository.emergencyBypass()
            },
            onDismiss = { showEmergencyDialog = false }
        )
    }
}
