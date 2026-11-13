package com.example.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
import com.example.data.ScreenLockRepository
import com.example.data.StatsRepository
import com.example.data.local.entity.ScreenLockEntity
import com.example.ui.theme.islamicGeometricPattern
import com.example.util.AppStrings
import com.example.util.RoutineAdhkarResolver

@Composable
fun ActiveLockContent(
    lock: ScreenLockEntity,
    queueSize: Int = 1,
    onComplete: () -> Unit
) {
    val config = remember(lock) { lock.getContentConfig() }
    var showEmergencyDialog by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(lock.id) { com.example.util.LockSoundManager.playLockAlert(context) }

    val finishLockAction: () -> Unit = {
        LockCompletionHelper.completeLock(context, lock, onComplete)
    }

    val lockMode by com.example.data.AppPreferencesManager.lockEnforcementMode.collectAsState()
    val overlayShape = if (lockMode == "SYSTEM_OVERLAY") androidx.compose.foundation.shape.RoundedCornerShape(20.dp) else androidx.compose.ui.graphics.RectangleShape

    if (lock.lockType.equals("QURAN", ignoreCase = true) && config.isPageMode) {
        ActiveQuranPageLockReader(
            config = config,
            onFinish = {
                StatsRepository.recordAyahsRead(config.pagesPerSession.coerceAtLeast(1) * 15)
                StatsRepository.recordLockCompleted()
                ScreenLockRepository.advanceProgress(lock.id)
                finishLockAction()
            },
            onResetToStart = { ScreenLockRepository.resetProgressToStart(lock.id) },
            onEmergencyBypass = { showEmergencyDialog = true },
            modifier = Modifier.fillMaxSize().systemBarsPadding().testTag("kiosk_active_lock_screen")
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (lockMode == "SYSTEM_OVERLAY") Modifier.systemBarsPadding().padding(10.dp) else Modifier)
                .background(Color(0xF206150E), overlayShape)
                .then(
                    if (lockMode == "SYSTEM_OVERLAY") {
                        Modifier.border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.85f), overlayShape)
                    } else Modifier
                )
                .islamicGeometricPattern(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary, 92f, 0.14f)
                .padding(16.dp).testTag("kiosk_active_lock_screen"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().widthIn(max = 500.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                Text(text = lock.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                Text(text = AppStrings.warningLockActive, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)

                if (lock.lockType.equals("QURAN", ignoreCase = true)) {
                    ActiveQuranLockReader(
                        config = config,
                        targetAmount = lock.targetAmount,
                        onFinish = {
                            StatsRepository.recordAyahsRead(config.ayahsPerSession.coerceAtLeast(1))
                            StatsRepository.recordLockCompleted()
                            ScreenLockRepository.advanceProgress(lock.id)
                            finishLockAction()
                        },
                        onResetToStart = { ScreenLockRepository.resetProgressToStart(lock.id) }
                    )
                } else if (lock.lockType.equals("HIFZ", ignoreCase = true)) {
                    HifzChallengeLockRunner(surahIds = config.selectedSurahIds) {
                        StatsRepository.recordAyahsRead(5); StatsRepository.recordLockCompleted(); finishLockAction()
                    }
                } else if (lock.lockType.equals("QUIZ", ignoreCase = true)) {
                    IslamicQuizLockRunner(onAnswerCorrect = {
                        StatsRepository.recordDhikrCount(10); StatsRepository.recordLockCompleted(); finishLockAction()
                    })
                } else if (lock.lockType.equals("MINDFUL_GATE", ignoreCase = true)) {
                    MindfulGatePromptCard(appPackage = lock.id.removePrefix("mindful_gate_"), appLabel = lock.title) {
                        ScreenLockRepository.dismissLock(context); onComplete()
                    }
                } else {
                    val adhkar = remember(lock.id, lock.title) { RoutineAdhkarResolver.getAdhkarForLock(lock) }
                    SequentialDhikrLockRunner(
                        routineTitle = lock.title,
                        items = adhkar,
                        queueInfo = if (queueSize > 1) "${AppStrings.get("الروتين", "Routine")} 1/${queueSize}" else "",
                        onAllCompleted = {
                            StatsRepository.recordDhikrCount(adhkar.sumOf { it.targetCount })
                            StatsRepository.recordLockCompleted()
                            finishLockAction()
                        }
                    )
                }

                if (lock.id.startsWith("prayer_lock_") || lock.lockType.equals("DHIKR", ignoreCase = true)) {
                    LockSnoozeRow { mins ->
                        com.example.util.LockScheduleTracker.scheduleSnoozeLock(context, lock.id, lock.title, lock.lockType, lock.contentConfigJson, mins)
                        ScreenLockRepository.dismissLock(context); onComplete()
                    }
                }

                TextButton(onClick = { showEmergencyDialog = true }, modifier = Modifier.testTag("kiosk_emergency_btn")) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFB74D), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(AppStrings.emergencyBypass, color = Color(0xFFFFB74D), fontSize = 12.sp)
                }
            }
        }
    }

    if (showEmergencyDialog) {
        PinProtectedBypassDialog(
            onSuccess = { showEmergencyDialog = false; StatsRepository.recordEmergencyBypass(); ScreenLockRepository.dismissLock(); onComplete() },
            onDismiss = { showEmergencyDialog = false }
        )
    }
}
