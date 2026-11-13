package com.example.ui.prayer

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.AutoSilentPrayerManager
import com.example.util.AppStrings

@Composable
fun AutoSilentMosqueCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var isEnabled by remember { mutableStateOf(AutoSilentPrayerManager.isAutoSilentEnabled(context)) }
    var durationMins by remember { mutableIntStateOf(AutoSilentPrayerManager.getDurationMinutes(context)) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.VolumeMute, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(text = if (isAr) "الوضع الصامت التلقائي وقت الصلاة" else "Auto-Silent During Prayers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { checked ->
                        isEnabled = checked
                        AutoSilentPrayerManager.setAutoSilentEnabled(context, checked)
                        if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                            if (nm?.isNotificationPolicyAccessGranted == false) {
                                try {
                                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                                } catch (_: Exception) {}
                            }
                        }
                    }
                )
            }

            Text(
                text = if (isAr) "يكتم صوت الهاتف تلقائياً أثناء الصلاة لمدة $durationMins دقيقة ويعيده طبيعياً بعد انتهاء وقت الجماعة."
                else "Mutes ringer during prayer for $durationMins mins and restores normal mode automatically.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isEnabled) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(15, 20, 25, 30).forEach { m ->
                        FilterChip(
                            selected = durationMins == m,
                            onClick = {
                                durationMins = m
                                AutoSilentPrayerManager.setDurationMinutes(context, m)
                            },
                            label = { Text("$m ${if (isAr) "د" else "m"}", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
