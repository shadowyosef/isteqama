package com.example.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppPreferencesManager
import com.example.util.AppStrings
import com.example.util.TasbeehHapticsHelper

@Composable
fun PinProtectedBypassDialog(
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var inputPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    fun onDigit(d: String) {
        if (inputPin.length < 4) {
            errorMessage = ""
            TasbeehHapticsHelper.performCountFeedback(context, false)
            val updated = inputPin + d
            inputPin = updated
            if (updated.length == 4) {
                if (AppPreferencesManager.verifySecurityPin(context, updated)) {
                    TasbeehHapticsHelper.performCountFeedback(context, true)
                    onSuccess()
                } else {
                    errorMessage = if (isAr) "رمز PIN غير صحيح! القفل مستمر" else "Incorrect PIN! Lock continues"
                    inputPin = ""
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("pin_bypass_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.LockOpen, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(36.dp))
                Text(
                    text = if (isAr) "فك القفل برمز PIN" else "Unlock via Security PIN",
                    fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = if (isAr) "أدخل رمز PIN الأمني لتخطي القفل وإنهاء الجلسة اضطرارياً" else "Enter your security PIN to bypass the lock screen",
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                    repeat(4) { idx ->
                        val filled = idx < inputPin.length
                        Box(
                            modifier = Modifier.size(16.dp).clip(CircleShape).background(if (filled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant)
                        )
                    }
                }

                if (errorMessage.isNotBlank()) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    val digits = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("C", "0", "⌫"))
                    digits.forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            row.forEach { key ->
                                Surface(
                                    shape = CircleShape,
                                    color = if (key.all { it.isDigit() }) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                                    modifier = Modifier.size(52.dp).clickable {
                                        when (key) {
                                            "C" -> inputPin = ""
                                            "⌫" -> if (inputPin.isNotEmpty()) inputPin = inputPin.dropLast(1)
                                            else -> onDigit(key)
                                        }
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (key == "⌫") Icon(Icons.Default.Backspace, contentDescription = null, modifier = Modifier.size(20.dp))
                                        else Text(text = key, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Text(if (isAr) "إلغاء والعودة للأذكار" else "Cancel and Return to Adhkar", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
