package com.example.ui.tasbeeh

import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.TasbeehScheduleModel
import com.example.model.ToastPositionPreset
import com.example.service.FloatingTasbeehToastManager
import com.example.util.FloatingToastPositionManager
import com.example.util.RequestPermissionsManager

@Composable
fun ToastPositionPickerDialog(
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedPreset by remember {
        mutableStateOf(FloatingToastPositionManager.getSelectedPreset(context))
    }
    val lastX = remember { FloatingToastPositionManager.getLastDraggedX(context) }
    val lastY = remember { FloatingToastPositionManager.getLastDraggedY(context) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "موقع التوست العائم" else "Floating Toast Position",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
                    }
                }

                Text(
                    text = if (isArabic) "يمكنك سحب التوست بإصبعك لأي مكان وسيتذكره تلقائياً، أو اختر مكاناً ثابتاً من القائمة:"
                    else "You can freely drag the toast anywhere and it will remember it, or choose a preset position:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(ToastPositionPreset.values()) { preset ->
                        val isSelected = preset == selectedPreset
                        val subtitle = if (preset == ToastPositionPreset.LAST_DRAGGED) {
                            if (isArabic) "الإحداثيات: X: $lastX, Y: $lastY" else "Coordinates: X: $lastX, Y: $lastY"
                        } else null

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth().clickable { selectedPreset = preset }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = isSelected, onClick = { selectedPreset = preset })
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isArabic) preset.labelAr else preset.labelEn,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    subtitle?.let {
                                        Text(text = it, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                if (preset == ToastPositionPreset.LAST_DRAGGED) {
                                    Icon(Icons.Default.NearMe, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = {
                            FloatingToastPositionManager.setPreset(context, selectedPreset)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                                RequestPermissionsManager.requestOverlayPermission(context)
                            } else {
                                val testModel = TasbeehScheduleModel(
                                    phraseAr = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
                                    phraseEn = "Subhan Allahi wa bihamdihi",
                                    targetCount = 3,
                                    durationSeconds = 8
                                )
                                FloatingTasbeehToastManager.triggerTasbeehAlert(context, testModel)
                            }
                        }
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isArabic) "تجربة الموقع" else "Test", fontSize = 11.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(onClick = onDismiss) { Text(if (isArabic) "إلغاء" else "Cancel") }
                        Button(onClick = {
                            FloatingToastPositionManager.setPreset(context, selectedPreset)
                            onDismiss()
                        }) {
                            Text(if (isArabic) "حفظ" else "Save")
                        }
                    }
                }
            }
        }
    }
}
