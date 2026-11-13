package com.example.ui.settings

import androidx.compose.foundation.layout.*
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
import com.example.data.SmartAlertPreferences
import com.example.model.AlertSoundStyle
import com.example.model.SmartAlertMode
import com.example.util.AppStrings
import com.example.util.FloatingAyahToastHelper
import com.example.util.WhisperTtsHelper

@Composable
fun SmartAlertSettingsSection() {
    val context = LocalContext.current
    var alertMode by remember { mutableStateOf(SmartAlertPreferences.smartAlertMode.value) }
    var soundStyle by remember { mutableStateOf(SmartAlertPreferences.alertSoundStyle.value) }
    var isPersistent by remember { mutableStateOf(SmartAlertPreferences.isPersistentAlert.value) }
    var hourlyAyah by remember { mutableStateOf(FloatingAyahToastHelper.isEnabled(context)) }
    var whisperAll by remember { mutableStateOf(WhisperTtsHelper.isGlobalEnabled(context)) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(AppStrings.get("سلوك التنبيه الذكي عند فتح التطبيقات غير المستثناة", "Smart Alert Style for Restricted Apps"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(
                selected = alertMode == SmartAlertMode.PERSISTENT_LOCK,
                onClick = { alertMode = SmartAlertMode.PERSISTENT_LOCK; SmartAlertPreferences.smartAlertMode.value = SmartAlertMode.PERSISTENT_LOCK },
                label = { Text(AppStrings.get("قفل كامل", "Full Lock"), fontSize = 11.sp, maxLines = 1) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = alertMode == SmartAlertMode.FLOATING_BANNER,
                onClick = { alertMode = SmartAlertMode.FLOATING_BANNER; SmartAlertPreferences.smartAlertMode.value = SmartAlertMode.FLOATING_BANNER },
                label = { Text(AppStrings.get("بانر عائم", "Banner"), fontSize = 11.sp, maxLines = 1) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = alertMode == SmartAlertMode.COUNTDOWN_GRACE,
                onClick = { alertMode = SmartAlertMode.COUNTDOWN_GRACE; SmartAlertPreferences.smartAlertMode.value = SmartAlertMode.COUNTDOWN_GRACE },
                label = { Text(AppStrings.get("مهلة 30ث", "30s Grace"), fontSize = 11.sp, maxLines = 1) },
                modifier = Modifier.weight(1f)
            )
        }

        Text(AppStrings.get("نغمة وتنبيه تذكير الورد", "Spiritual Reminder Sound Style"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(
                selected = soundStyle == AlertSoundStyle.GENTLE_ISLAMIC_TONE,
                onClick = { soundStyle = AlertSoundStyle.GENTLE_ISLAMIC_TONE; AppPreferencesManager.setNotificationStyle(AlertSoundStyle.GENTLE_ISLAMIC_TONE) },
                label = { Text(AppStrings.get("نغمة إيمانية", "Islamic"), fontSize = 11.sp, maxLines = 1) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = soundStyle == AlertSoundStyle.VIBRATION_ONLY,
                onClick = { soundStyle = AlertSoundStyle.VIBRATION_ONLY; AppPreferencesManager.setNotificationStyle(AlertSoundStyle.VIBRATION_ONLY) },
                label = { Text(AppStrings.get("اهتزاز فقط", "Vibrate"), fontSize = 11.sp, maxLines = 1) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = soundStyle == AlertSoundStyle.SILENT,
                onClick = { soundStyle = AlertSoundStyle.SILENT; AppPreferencesManager.setNotificationStyle(AlertSoundStyle.SILENT) },
                label = { Text(AppStrings.get("صامت", "Silent"), fontSize = 11.sp, maxLines = 1) },
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(AppStrings.get("إشعار دائم بعداد الصلاة القادمة", "Persistent next-prayer notification"), fontSize = 13.sp, modifier = Modifier.weight(1f))
            Switch(checked = isPersistent, onCheckedChange = { isPersistent = it; AppPreferencesManager.setPersistentNotification(it) })
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(AppStrings.get("توست آية الساعة العائم", "Hourly Floating Ayah Toast"), fontSize = 13.sp, modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(onClick = { FloatingAyahToastHelper.triggerNow(context) }) { Text(AppStrings.get("تجربة", "Test"), fontSize = 11.sp) }
                Switch(checked = hourlyAyah, onCheckedChange = { hourlyAyah = it; FloatingAyahToastHelper.setEnabled(context, it) }, modifier = Modifier.testTag("hourly_ayah_switch"))
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(AppStrings.get("التوست الصوتي الهادئ لجميع الأذكار", "Calm Whisper Voice (TTS) for toasts"), fontSize = 13.sp, modifier = Modifier.weight(1f))
            Switch(checked = whisperAll, onCheckedChange = { whisperAll = it; WhisperTtsHelper.setGlobalEnabled(context, it) }, modifier = Modifier.testTag("whisper_tts_switch"))
        }
    }
}
