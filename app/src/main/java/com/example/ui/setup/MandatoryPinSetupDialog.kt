package com.example.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun MandatoryPinSetupDialog(
    onPinSet: () -> Unit
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var firstPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirmStep by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val currentInput = if (isConfirmStep) confirmPin else firstPin

    fun onDigit(d: String) {
        if (currentInput.length < 4) {
            errorMessage = ""
            TasbeehHapticsHelper.performCountFeedback(context, false)
            val updated = currentInput + d
            if (isConfirmStep) {
                confirmPin = updated
                if (updated.length == 4) {
                    if (updated == firstPin) {
                        TasbeehHapticsHelper.performCountFeedback(context, true)
                        AppPreferencesManager.setSecurityPin(context, updated)
                        onPinSet()
                    } else {
                        errorMessage = if (isAr) "الرمز غير متطابق، حاول مجدداً" else "PIN does not match, try again"
                        confirmPin = ""
                    }
                }
            } else {
                firstPin = updated
                if (updated.length == 4) {
                    isConfirmStep = true
                }
            }
        }
    }

    fun onBackspace() {
        if (currentInput.isNotEmpty()) {
            val updated = currentInput.dropLast(1)
            if (isConfirmStep) confirmPin = updated else firstPin = updated
        }
    }

    Dialog(onDismissRequest = {}, properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("mandatory_pin_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                Text(
                    text = if (isConfirmStep) (if (isAr) "تأكيد رمز PIN الأمني" else "Confirm Security PIN") else (if (isAr) "تعيين رمز PIN الأمني" else "Set Security PIN"),
                    fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isConfirmStep) (if (isAr) "أعد إدخال الرمز المكون من 4 أرقام" else "Re-enter the 4-digit PIN")
                    else (if (isAr) "حدد رمز PIN لحماية قفل الأذكار ومنع تخطيه دون إذنك" else "Set a 4-digit PIN required to bypass lock routines"),
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                    repeat(4) { idx ->
                        val filled = idx < currentInput.length
                        Box(
                            modifier = Modifier.size(16.dp).clip(CircleShape).background(if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
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
                                    color = if (key.all { it.isDigit() }) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    modifier = Modifier.size(54.dp).clickable {
                                        when (key) {
                                            "C" -> { if (isConfirmStep) confirmPin = "" else firstPin = "" }
                                            "⌫" -> onBackspace()
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
            }
        }
    }
}
