package com.example.ui.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppBackupMetadata
import com.example.util.AppBackupManager
import com.example.util.AppStrings
import java.io.File

@Composable
fun BackupRestoreSettingsSection() {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var autoBackupEnabled by remember { mutableStateOf(AppBackupManager.isAutoBackupEnabled(context)) }
    var backupsList by remember { mutableStateOf(AppBackupManager.listBackups(context)) }
    var confirmingRestoreItem by remember { mutableStateOf<AppBackupMetadata?>(null) }
    var showShareQrDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    val fileImportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            try {
                val json = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                if (json != null && AppBackupManager.restoreBackup(context, json).isSuccess) {
                    statusMessage = if (isAr) "تمت استعادة البيانات بنجاح!" else "Backup restored successfully!"
                    backupsList = AppBackupManager.listBackups(context)
                } else statusMessage = if (isAr) "فشل استيراد الملف" else "Failed to parse backup"
            } catch (e: Exception) {
                statusMessage = if (isAr) "خطأ: ${e.message}" else "Error: ${e.message}"
            }
        }
    }
    val fileExportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
        if (uri != null) {
            val res = AppBackupManager.createBackup(context)
            res.onSuccess { file ->
                context.contentResolver.openOutputStream(uri)?.use { out -> file.inputStream().copyTo(out) }
                statusMessage = if (isAr) "تم تصدير النسخة الاحتياطية بنجاح!" else "Exported backup JSON!"
            }.onFailure { statusMessage = if (isAr) "فشل التصدير" else "Export failed" }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isAr) "النسخ الاحتياطي ومشاركة الباقات (QR / كود)" else "Backup & Share Routine Code (QR)",
            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (isAr) "احفظ جميع إعداداتك وأذكارك في ملف أو شارك كود/QR مع أهلك وأصدقائك." else "Backup all settings or share a QR/Text routine package with family.",
            fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(if (isAr) "النسخ الاحتياطي التلقائي اليومي" else "Daily Auto-Backup", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Switch(checked = autoBackupEnabled, onCheckedChange = { autoBackupEnabled = it; AppBackupManager.setAutoBackupEnabled(context, it) })
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(
                onClick = {
                    val res = AppBackupManager.createBackup(context)
                    if (res.isSuccess) {
                        statusMessage = if (isAr) "تم إنشاء نسخة احتياطية جديدة بنجاح!" else "New backup created successfully!"
                        backupsList = AppBackupManager.listBackups(context)
                    } else statusMessage = if (isAr) "فشل إنشاء النسخة" else "Backup failed"
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isAr) "نسخ" else "Backup", fontSize = 11.sp)
            }
            OutlinedButton(onClick = { fileExportLauncher.launch("istaqama_backup.json") }, modifier = Modifier.weight(1f)) {
                Text(if (isAr) "تصدير ملف" else "Export", fontSize = 11.sp)
            }
            OutlinedButton(onClick = { fileImportLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isAr) "استيراد" else "Import", fontSize = 11.sp)
            }
        }
        FilledTonalButton(onClick = { showShareQrDialog = true }, modifier = Modifier.fillMaxWidth().testTag("open_qr_routine_share_btn")) {
            Text(if (isAr) "📱 مشاركة أو استيراد باقة أذكار / ختمة (QR أو كود)" else "📱 Share / Import Routine Code or QR", fontSize = 11.sp)
        }
        if (statusMessage.isNotBlank()) {
            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
                Text(statusMessage, modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
        if (backupsList.isNotEmpty()) {
            Text(if (isAr) "النسخ المحفوظة (${backupsList.size}):" else "Saved Backups (${backupsList.size}):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                backupsList.forEach { b ->
                    BackupItemRow(
                        item = b,
                        onRestore = { confirmingRestoreItem = b },
                        onShare = {
                            val intent = AppBackupManager.exportBackup(context, File(b.filePath))
                            context.startActivity(Intent.createChooser(intent, if (isAr) "مشاركة النسخة" else "Share Backup"))
                        },
                        onDelete = {
                            AppBackupManager.deleteBackup(b.filePath)
                            backupsList = AppBackupManager.listBackups(context)
                        }
                    )
                }
            }
        }
    }

    confirmingRestoreItem?.let { item ->
        AlertDialog(
            onDismissRequest = { confirmingRestoreItem = null },
            title = { Text(if (isAr) "تأكيد الاستعادة" else "Confirm Restore") },
            text = { Text(if (isAr) "استعادة بيانات النسخة (${item.formattedDate})؟" else "Restore data from ${item.formattedDate}?") },
            confirmButton = {
                Button(onClick = {
                    if (AppBackupManager.restoreBackup(context, File(item.filePath).readText()).isSuccess) {
                        statusMessage = if (isAr) "تمت الاستعادة بنجاح!" else "Restored!"
                        Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show()
                    }
                    confirmingRestoreItem = null
                }) { Text(if (isAr) "استعادة" else "Restore") }
            },
            dismissButton = { TextButton(onClick = { confirmingRestoreItem = null }) { Text(if (isAr) "إلغاء" else "Cancel") } }
        )
    }

    if (showShareQrDialog) RoutineShareImportDialog(onDismiss = { showShareQrDialog = false })
}
