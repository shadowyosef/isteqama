package com.example.ui.circles

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloud.CloudSyncRepository
import com.example.cloud.GoogleAuthHelper
import com.example.cloud.GoogleSignInButton
import com.example.cloud.UserBackupSnapshot
import com.example.data.ScreenLockRepository
import com.example.data.StatsRepository
import com.example.data.TasbeehRepository
import com.example.util.AppStrings
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@Composable
fun CloudBackupSyncCard(onOpenCircles: () -> Unit = {}) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val scope = rememberCoroutineScope()
    val user by GoogleAuthHelper.authStateFlow().collectAsState(initial = null)
    val repo = remember { CloudSyncRepository(context) }

    val backupsFlow = remember(user?.uid) {
        if (user != null) repo.observeMyBackups() else emptyFlow()
    }
    val backups by backupsFlow.collectAsState(initial = emptyList())
    var statusMsg by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        GoogleAuthHelper.attemptAutoSignIn(context)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(if (user != null) Icons.Default.CloudDone else Icons.Default.CloudSync, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(if (isAr) "المزامنة السحابية وحلقات الختمة" else "Cloud Sync & Khatma Circles", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            if (user == null) {
                Text(
                    if (isAr) "سجّل دخولك بحساب Google لحفظ إحصائياتك ومشاركتها عبر أجهزتك والانضمام لحلقات الختمة." else "Sign in with Google to sync worship progress across devices & join Khatma circles.",
                    fontSize = 12.sp
                )
                GoogleSignInButton(onError = { statusMsg = it })
            } else {
                Text("${user?.displayName ?: user?.email ?: ""} ✓", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                val latest = backups.firstOrNull()
                if (latest != null) {
                    Text(
                        if (isAr) "آخر نسخة: ${latest.totalTasbeeh} تسبيحة • ${latest.totalPagesRead} صفحة" else "Last cloud sync: ${latest.totalTasbeeh} dhikr • ${latest.totalPagesRead} pages",
                        fontSize = 11.sp
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            scope.launch {
                                val st = StatsRepository.stats.value
                                val totalDhikr = st.totalDhikrCount.toLong() + TasbeehRepository.totalLifetimeCount.value
                                val res = repo.uploadBackupSnapshot(
                                    UserBackupSnapshot(
                                        ownerId = user?.uid ?: "",
                                        label = Build.MODEL.orEmpty().ifBlank { "Android Device" },
                                        totalTasbeeh = totalDhikr,
                                        totalPagesRead = st.totalAyahsRead / 15,
                                        currentStreak = st.currentStreak,
                                        locksCount = ScreenLockRepository.locks.value.size
                                    )
                                )
                                statusMsg = if (res.isSuccess) AppStrings.get("تم الحفظ في السحابة", "Synced!") else (res.exceptionOrNull()?.message ?: "Error")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text(if (isAr) "مزامنة الآن" else "Sync Now", fontSize = 12.sp) }

                    FilledTonalButton(onClick = onOpenCircles, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (isAr) "الحلقات" else "Circles", fontSize = 12.sp)
                    }
                }
                TextButton(onClick = { scope.launch { GoogleAuthHelper.signOut(context) } }) {
                    Text(if (isAr) "تسجيل خروج" else "Sign out", fontSize = 11.sp)
                }
            }
            if (statusMsg.isNotEmpty()) Text(statusMsg, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
