package com.example.ui.unified

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UnifiedRoutineRepository
import com.example.model.RoutineCategory
import com.example.model.UnifiedRoutine
import com.example.ui.lock.ActiveLockActivity
import com.example.util.AppStrings
import com.example.util.CountdownUtils

@Composable
fun UnifiedRoutineCard(
    routine: UnifiedRoutine,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    com.example.ui.theme.OrnateIslamicCard(
        cornerRadius = 18.dp,
        modifier = modifier.testTag("routine_card_${routine.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = when (routine.category) {
                            RoutineCategory.QURAN_WARD -> "ورد قرآني"
                            RoutineCategory.ADHKAR_SET -> "سيت أذكار"
                            RoutineCategory.TASBEEH_CHAIN -> "سلسلة تسبيح"
                            RoutineCategory.CUSTOM_HYBRID -> "روتين مخصص"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CountdownBadge(
                        remainingMillisProvider = { CountdownUtils.getRemainingMillisForRoutine(routine) },
                        isArabic = AppStrings.isArabic
                    )
                    if (routine.isLockActive) {
                        IconButton(
                            onClick = { ActiveLockActivity.launch(context, routine.toScreenLockEntity()) },
                            modifier = Modifier.size(32.dp).testTag("test_lock_icon_${routine.id}")
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = "Test Lock", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        RoutineActionMenu(
                            expanded = showMenu,
                            routine = routine,
                            context = context,
                            onDismiss = { showMenu = false },
                            onEdit = onEdit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (AppStrings.isArabic) routine.titleAr else routine.titleEn,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            if (routine.detailsSummary.isNotEmpty()) {
                Text(
                    text = routine.detailsSummary,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LinearProgressIndicator(
                    progress = { routine.progressFraction },
                    modifier = Modifier.weight(1f).height(8.dp).padding(end = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
                com.example.ui.theme.OrnateIslamicButton(
                    onClick = {
                        val isGoal = routine.currentCount + 1 >= routine.targetCount
                        com.example.util.TasbeehHapticsHelper.performCountFeedback(context, isGoal)
                        UnifiedRoutineRepository.incrementCount(routine.id)
                    }
                ) {
                    if (routine.isCompleted) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    } else {
                        Text("${routine.currentCount} / ${routine.targetCount}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
