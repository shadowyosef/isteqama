package com.example.ui.calendar.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
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
import com.example.data.ScreenLockRepository
import com.example.ui.lock.ActiveLockActivity

@Composable
fun ActiveLockStatusCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val activeLock by ScreenLockRepository.currentActiveLock.collectAsState()

    Card(
        modifier = modifier.fillMaxWidth().testTag("active_lock_status_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (activeLock != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (activeLock != null) Icons.Default.Lock else Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (activeLock != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (activeLock != null) (if (isArabic) "قفل نشط الآن" else "Active Lock in Progress")
                        else (if (isArabic) "حالة القفل: غير مقيد" else "Status: No Active Lock"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = activeLock?.title ?: (if (isArabic) "الهاتف متاح بحرية لحين حلول موعد الورد القادم" else "Phone is unlocked until next scheduled ward"),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (activeLock != null) {
                Button(
                    onClick = { ActiveLockActivity.launch(context, activeLock) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp).testTag("resume_active_lock_btn")
                ) {
                    Text(if (isArabic) "استئناف" else "Resume", fontSize = 11.sp)
                }
            }
        }
    }
}
