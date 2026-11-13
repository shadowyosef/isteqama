package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.lock.ActiveLockActivity
import com.example.util.AppStrings
import com.example.util.SmartAutoLockHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarQuickControlsRow(
    onOpenAppBlocker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var smartGradientEnabled by remember { mutableStateOf(SmartAutoLockHelper.isEnabled(context)) }
    val currentPhase = remember(isAr) { SmartAutoLockHelper.getPhaseLabel(isAr = isAr) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Column {
                        Text(
                            text = if (isAr) "التدرج اليومي الذكي للورد" else "Smart Time-Gradient Lock",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(currentPhase, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Switch(
                    checked = smartGradientEnabled,
                    onCheckedChange = {
                        smartGradientEnabled = it
                        SmartAutoLockHelper.setEnabled(context, it)
                    },
                    modifier = Modifier.testTag("smart_gradient_lock_switch")
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilledTonalButton(
                    onClick = { ActiveLockActivity.launch(context, SmartAutoLockHelper.buildSmartLockForHour()) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("launch_smart_lock_now_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isAr) "تجربة الورد الذكي" else "Try Smart Lock", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = onOpenAppBlocker,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("calendar_app_blocker_btn")
                ) {
                    Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isAr) "قفل التطبيقات المشتتة" else "Blocked Apps", fontSize = 11.sp)
                }
                var showFocusSheet by remember { mutableStateOf(false) }
                FilledTonalButton(
                    onClick = { showFocusSheet = true },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("calendar_focus_profiles_btn")
                ) {
                    Text(if (isAr) "أنماط التركيز" else "Focus Modes", fontSize = 11.sp)
                }
                if (showFocusSheet) {
                    com.example.ui.lock.FocusProfilesSheet(onDismiss = { showFocusSheet = false })
                }
            }
        }
    }
}
