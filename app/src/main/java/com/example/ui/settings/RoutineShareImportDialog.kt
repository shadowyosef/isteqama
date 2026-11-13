package com.example.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.util.AppStrings
import com.example.util.RoutineShareCodeHelper

@Composable
fun RoutineShareImportDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var activeTab by remember { mutableIntStateOf(0) }
    val packageCode = remember { RoutineShareCodeHelper.exportPackageCode() }
    val qrMatrix = remember(packageCode) { RoutineShareCodeHelper.buildQrMatrix(packageCode) }
    var pasteCode by remember { mutableStateOf("") }
    var importResultMsg by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().widthIn(max = 500.dp)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.QrCode2, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(if (isAr) "مشاركة واستيراد باقة الأذكار والجدول" else "Share & Import Routine Code", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(selected = activeTab == 0, onClick = { activeTab = 0 }, label = { Text(if (isAr) "مشاركة QR / كود" else "Export QR", fontSize = 11.sp) }, modifier = Modifier.weight(1f))
                    FilterChip(selected = activeTab == 1, onClick = { activeTab = 1 }, label = { Text(if (isAr) "استيراد كود" else "Import Code", fontSize = 11.sp) }, modifier = Modifier.weight(1f))
                }

                if (activeTab == 0) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(168.dp).background(Color.White, RoundedCornerShape(12.dp)).padding(12.dp)) {
                            val n = qrMatrix.size
                            val cell = size.width / n
                            for (r in 0 until n) for (c in 0 until n) {
                                if (qrMatrix[r][c]) drawRect(Color.Black, topLeft = Offset(c * cell, r * cell), size = Size(cell, cell))
                            }
                        }
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
                        Text(packageCode.take(64) + "...", modifier = Modifier.padding(8.dp), fontSize = 10.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                cm?.setPrimaryClip(ClipData.newPlainText("IstaqamaPackage", packageCode))
                                Toast.makeText(context, if (isAr) "تم نسخ الكود" else "Copied", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isAr) "نسخ الكود" else "Copy", fontSize = 11.sp)
                        }
                        Button(onClick = { RoutineShareCodeHelper.shareCode(context, packageCode) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isAr) "مشاركة" else "Share", fontSize = 11.sp)
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = pasteCode,
                        onValueChange = { pasteCode = it },
                        label = { Text(if (isAr) "ألصق كود الباقة (ISTAQAMA1:...)" else "Paste Package Code") },
                        modifier = Modifier.fillMaxWidth().testTag("import_routine_code_input"),
                        maxLines = 4
                    )
                    Button(
                        onClick = {
                            val res = RoutineShareCodeHelper.importPackageCode(pasteCode)
                            importResultMsg = if (res.isSuccess) {
                                if (isAr) "تم استيراد ${res.getOrDefault(0)} جدول وورد بنجاح!" else "Imported ${res.getOrDefault(0)} items!"
                            } else {
                                if (isAr) "الكود غير صالح، تأكد من لصق النص كاملاً" else "Invalid package code"
                            }
                        },
                        enabled = pasteCode.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().testTag("apply_imported_code_btn")
                    ) { Text(if (isAr) "تطبيق واستيراد الباقة" else "Apply Package") }
                    if (importResultMsg.isNotBlank()) {
                        Text(importResultMsg, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
