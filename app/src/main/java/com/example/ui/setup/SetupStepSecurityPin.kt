package com.example.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.ui.theme.OrnateIslamicCard

@Composable
fun SetupStepSecurityPin(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val isPinSaved by AppPreferencesManager.isPinConfigured.collectAsState()
    var pinInput by remember { mutableStateOf("") }
    var confirmInput by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth().testTag("setup_step_security_pin"), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OrnateIslamicCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                    Text(
                        text = if (isArabic) "تعيين الرقم السري للحماية" else "Set Security Protection PIN",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (isArabic) "الخطوة ٥: يمنع هذا الرمز السريع تجاوز أوراد القفل أو تعطيل الحماية أثناء لحظات الفتور."
                    else "Step 5: Protects your lock routines and app restrictions from accidental bypass.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isPinSaved) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            if (isPinSaved) Icons.Default.CheckCircle else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isPinSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isPinSaved) (if (isArabic) "تم تعيين الرقم السري بنجاح ✓" else "Security PIN is Active ✓")
                            else (if (isArabic) "أدخل ٤ أرقام لحفظ الرقم السري" else "Enter 4 digits to configure your PIN"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { raw ->
                        pinInput = raw.filter { it.isDigit() }.take(4)
                        feedback = ""
                    },
                    label = { Text(if (isArabic) "الرقم السري (٤ أرقام)" else "4-Digit PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("setup_pin_input")
                )

                OutlinedTextField(
                    value = confirmInput,
                    onValueChange = { raw ->
                        confirmInput = raw.filter { it.isDigit() }.take(4)
                        feedback = ""
                    },
                    label = { Text(if (isArabic) "تأكيد الرقم السري" else "Confirm PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("setup_pin_confirm_input")
                )

                Button(
                    onClick = {
                        when {
                            pinInput.length != 4 -> feedback = if (isArabic) "يرجى إدخال ٤ أرقام" else "Enter 4 digits"
                            confirmInput.isNotEmpty() && confirmInput != pinInput -> feedback = if (isArabic) "الرقمان غير متطابقين" else "PINs do not match"
                            else -> {
                                AppPreferencesManager.setSecurityPin(context, pinInput)
                                feedback = if (isArabic) "تم حفظ الرقم السري بنجاح ✓" else "PIN Saved Successfully ✓"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("save_setup_pin_btn")
                ) {
                    Text(if (isArabic) "حفظ وتفعيل الرقم السري" else "Save & Activate PIN")
                }

                if (feedback.isNotBlank()) {
                    Text(feedback, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
