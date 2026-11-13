package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FocusProfilesRepository
import com.example.data.LockRepository
import com.example.data.SecurityRepository
import com.example.ui.security.SecurityPinDialog
import com.example.util.AppStrings

@Composable
fun EmergencyBypassDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isAr = AppStrings.isArabic
    var showPinDialog by remember { mutableStateOf(false) }
    var duaTaps by remember { mutableStateOf(0) }

    if (showPinDialog) {
        SecurityPinDialog(
            title = if (isAr) "تأكيد تجاوز الطوارئ برمز الحماية" else "Confirm Emergency Bypass PIN",
            onSuccess = {
                showPinDialog = false
                LockRepository.appRules.value.forEach { FocusProfilesRepository.grantTemporaryBypass(it.packageName, 2) }
                onConfirm()
            },
            onDismiss = onDismiss
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(AppStrings.emergencyBypass) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(AppStrings.emergencyDesc)
                    if (!SecurityRepository.isSecurityEnabled.value) {
                        Text(
                            text = if (isAr) "استغفر ٣ مرات لفتح مهلة دقيقتين ($duaTaps/3):" else "Tap 3x Istighfar for 2m allowance ($duaTaps/3):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        FilledTonalButton(onClick = { if (duaTaps < 3) duaTaps++ }, modifier = Modifier.fillMaxWidth()) {
                            Text(if (isAr) "أستغفر الله العظيم ($duaTaps/3)" else "Astaghfirullah ($duaTaps/3)")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (SecurityRepository.isSecurityEnabled.value) {
                            showPinDialog = true
                        } else if (duaTaps >= 3) {
                            LockRepository.appRules.value.forEach { FocusProfilesRepository.grantTemporaryBypass(it.packageName, 2) }
                            onConfirm()
                        }
                    },
                    enabled = SecurityRepository.isSecurityEnabled.value || duaTaps >= 3,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(AppStrings.confirmBypass)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(AppStrings.cancel) }
            }
        )
    }
}
