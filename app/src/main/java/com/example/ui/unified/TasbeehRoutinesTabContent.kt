package com.example.ui.unified

import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.data.TasbeehScheduleRepository
import com.example.model.TasbeehScheduleModel
import com.example.service.FloatingTasbeehToastManager
import com.example.ui.tasbeeh.TasbeehScheduleCard
import com.example.ui.tasbeeh.ToastPositionPickerDialog
import com.example.util.RequestPermissionsManager

@Composable
fun TasbeehRoutinesTabContent(
    onEdit: (TasbeehScheduleModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val schedules by TasbeehScheduleRepository.schedules.collectAsState()
    var showPositionDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) "جداول التسبيح والتوست العائم" else "Scheduled Tasbeeh & Floating Toasts",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            AssistChip(
                onClick = { showPositionDialog = true },
                leadingIcon = { Icon(Icons.Default.NearMe, null, modifier = Modifier.size(14.dp)) },
                label = { Text(if (isArabic) "موقع التوست" else "Toast Pos", fontSize = 11.sp) }
            )
        }

        if (schedules.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isArabic) "لا توجد جداول تسبيح مضافة. اضغط + بالأسفل لإضافة تذكير عائم" else "No scheduled tasbeeh. Tap + below to add a floating reminder.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp, top = 4.dp)
            ) {
                items(schedules, key = { it.id }) { item ->
                    TasbeehScheduleCard(
                        item = item,
                        isArabic = isArabic,
                        onToggle = { TasbeehScheduleRepository.toggleSchedule(item.id) },
                        onTest = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                                RequestPermissionsManager.requestOverlayPermission(context)
                            }
                            FloatingTasbeehToastManager.triggerTasbeehAlert(context, item)
                        },
                        onEdit = { onEdit(item) },
                        onDelete = { TasbeehScheduleRepository.deleteSchedule(item.id) }
                    )
                }
            }
        }
    }

    if (showPositionDialog) {
        ToastPositionPickerDialog(isArabic = isArabic, onDismiss = { showPositionDialog = false })
    }
}
