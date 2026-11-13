package com.example.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ScreenLockRepository
import com.example.ui.lock.LockSoundPickerDialog
import com.example.ui.theme.OrnateIslamicButton
import com.example.ui.theme.OrnateIslamicCard
import com.example.ui.theme.OrnateIslamicTonalButton
import com.example.util.AppStrings

@Composable
fun HomeLockStatusCard(
    onOpenLocker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val locks by ScreenLockRepository.locks.collectAsState()
    val activeCount = locks.count { it.isEnabled }
    var showSoundDialog by remember { mutableStateOf(false) }

    OrnateIslamicCard(
        modifier = modifier.testTag("home_lock_status_card"),
        cornerRadius = 20.dp
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Text(AppStrings.get("قفل الشاشة وحصن الطاعات", "Screen Lock & Guard"), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Text("$activeCount " + AppStrings.get("أقفال نشطة", "active locks"), fontSize = 11.sp, modifier = Modifier.padding(2.dp))
                }
            }

            Text(
                text = AppStrings.get("حماية وقتك وإلزام النفس بالورد القرآني والأذكار عند مواعيدها.", "Guarding your time with compulsory Quran and Adhkar."),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OrnateIslamicTonalButton(
                    onClick = { showSoundDialog = true },
                    modifier = Modifier.weight(1f).testTag("home_lock_sound_btn")
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(AppStrings.get("صوت القفل", "Lock Sound"), fontSize = 11.sp)
                }

                OrnateIslamicButton(
                    onClick = onOpenLocker,
                    modifier = Modifier.weight(1f).testTag("home_manage_locks_btn")
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(AppStrings.get("إدارة الأقفال", "Manage"), fontSize = 11.sp)
                }
            }
        }
    }

    if (showSoundDialog) {
        LockSoundPickerDialog(onDismiss = { showSoundDialog = false })
    }
}
