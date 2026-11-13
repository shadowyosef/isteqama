package com.example.ui.security

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SecurityRepository
import com.example.util.AppStrings

@Composable
fun SecurityPinDialog(
    title: String = "التحقق الأمني لمنع الخروج",
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "أدخل رمز الحماية (الافتراضي 1234) لتأكيد العملية ومنع الإيقاف غير المصرح به",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        if (it.length <= 8) {
                            pinInput = it
                            errorMessage = null
                        }
                    },
                    label = { Text("رمز الحماية") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("security_pin_input")
                )
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                val context = androidx.compose.ui.platform.LocalContext.current
                if (BiometricAuthHelper.isBiometricAvailable(context)) {
                    TextButton(
                        onClick = {
                            val activity = context as? androidx.fragment.app.FragmentActivity
                            if (activity != null) {
                                BiometricAuthHelper.authenticate(
                                    activity = activity,
                                    onSuccess = onSuccess,
                                    onError = { errorMessage = it }
                                )
                            }
                        }
                    ) {
                        Text(AppStrings.get("المصادقة بالبصمة 🔐", "Authenticate with Biometric 🔐"))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (SecurityRepository.verifyPin(pinInput)) {
                        onSuccess()
                    } else {
                        errorMessage = "الرمز غير صحيح، حاول مرة أخرى"
                    }
                },
                modifier = Modifier.testTag("confirm_security_pin_btn")
            ) {
                Text("تأكيد وفك القفل")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(AppStrings.cancel) }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
