package com.example.ui.lock

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AppStrings
import com.example.util.LockSoundManager

@Composable
fun LockSoundPickerDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(LockSoundManager.isSoundEnabled(context)) }
    var selectedSound by remember { mutableStateOf(LockSoundManager.getSelectedSound(context)) }
    var customUri by remember { mutableStateOf(LockSoundManager.getCustomSoundUri(context)) }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            val uriStr = uri.toString()
            customUri = uriStr
            selectedSound = LockSoundManager.SOUND_CUSTOM
            LockSoundManager.setCustomSoundUri(context, uriStr)
            LockSoundManager.setSelectedSound(context, LockSoundManager.SOUND_CUSTOM)
            Toast.makeText(context, AppStrings.get("تم اختيار الصوت بنجاح", "Audio selected"), Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose { LockSoundManager.stopSound() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(AppStrings.get("صوت تنبيه قفل الشاشة", "Lock Screen Alert Sound"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(AppStrings.get("تفعيل صوت القفل", "Enable Lock Sound"), fontSize = 14.sp)
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = {
                            isEnabled = it
                            LockSoundManager.setSoundEnabled(context, it)
                        }
                    )
                }

                if (isEnabled) {
                    HorizontalDivider()

                    val soundOptions = listOf(
                        LockSoundManager.SOUND_ADHAN to AppStrings.get("أذان كامل (أوفلاين)", "Full Adhan (Offline)"),
                        LockSoundManager.SOUND_TAKBEER to AppStrings.get("تكبيرات الأذان", "Takbeerat Alert"),
                        LockSoundManager.SOUND_CHIME to AppStrings.get("نغمة تسبيح هادئة", "Spiritual Chime"),
                        LockSoundManager.SOUND_RINGTONE to AppStrings.get("نغمة الهاتف الافتراضية", "Device Default Ringtone"),
                        LockSoundManager.SOUND_CUSTOM to AppStrings.get("صوت مخصص من الهاتف", "Custom Phone Audio")
                    )

                    soundOptions.forEach { (type, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                selectedSound = type
                                LockSoundManager.setSelectedSound(context, type)
                            }.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSound == type,
                                onClick = {
                                    selectedSound = type
                                    LockSoundManager.setSelectedSound(context, type)
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(label, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        }
                    }

                    if (selectedSound == LockSoundManager.SOUND_CUSTOM) {
                        OutlinedButton(
                            onClick = { audioPickerLauncher.launch(arrayOf("audio/*")) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AudioFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (customUri != null) AppStrings.get("تغيير الملف الصوتي", "Change Audio File")
                                else AppStrings.get("اختيار صوت من الهاتف (زي أذان)", "Pick Audio from Phone"),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = { LockSoundManager.playLockAlert(context) },
                        modifier = Modifier.fillMaxWidth().testTag("preview_lock_sound_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(AppStrings.get("تجربة واستماع الصوت", "Preview Alert Sound"), fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                LockSoundManager.stopSound()
                onDismiss()
            }) {
                Text(AppStrings.get("حفظ وإغلاق", "Save & Close"))
            }
        }
    )
}
