package com.example.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.data.SecurityRepository
import com.example.data.SecurityType

@Composable
fun SecuritySettingsSection() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val lockMode by AppPreferencesManager.lockEnforcementMode.collectAsState()
    val verifyMode by AppPreferencesManager.quranVerificationMode.collectAsState()
    var isEnabled by remember { mutableStateOf(SecurityRepository.isSecurityEnabled.value) }
    var securityType by remember { mutableStateOf(SecurityRepository.securityType.value) }
    var currentPin by remember { mutableStateOf(SecurityRepository.securityPin.value) }
    var showPinEdit by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = if (isArabic) "حماية الخروج وقفل الأمان" else "Security & Lock Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Text(text = if (isArabic) "أسلوب قفل الشاشة الإلزامي:" else "Lock Mode:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            FilterChip(selected = lockMode == "SEAMLESS_OVERLAY", onClick = { AppPreferencesManager.setLockEnforcementMode("SEAMLESS_OVERLAY") }, label = { Text(if (isArabic) "انسيابي" else "Seamless", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
            FilterChip(selected = lockMode == "SYSTEM_OVERLAY", onClick = { AppPreferencesManager.setLockEnforcementMode("SYSTEM_OVERLAY") }, label = { Text(if (isArabic) "نافذة نظام" else "Overlay", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
            FilterChip(selected = lockMode == "NATIVE_PINNING", onClick = { AppPreferencesManager.setLockEnforcementMode("NATIVE_PINNING") }, label = { Text(if (isArabic) "تثبيت OS" else "Pinning", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
        }

        Text(text = if (isArabic) "التحقق من قراءة القرآن المؤقتة:" else "Timed Quran Reading Check:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            FilterChip(selected = verifyMode == "INTERACTION_TRACK", onClick = { AppPreferencesManager.setQuranVerificationMode("INTERACTION_TRACK") }, label = { Text(if (isArabic) "تفاعل القراءة" else "Touch/Scroll", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
            FilterChip(selected = verifyMode == "AUDIO_RECITE", onClick = { AppPreferencesManager.setQuranVerificationMode("AUDIO_RECITE") }, label = { Text(if (isArabic) "تلاوة صوتية" else "Audio", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
            FilterChip(selected = verifyMode == "PERIODIC_CONFIRM", onClick = { AppPreferencesManager.setQuranVerificationMode("PERIODIC_CONFIRM") }, label = { Text(if (isArabic) "تأكيد دوري" else "Prompt", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(if (isArabic) "منع إيقاف التطبيق أو تجاوزه" else "Require Passcode to Exit", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text(if (isArabic) "يطلب الرمز أو البصمة لمنع تخطي الورد" else "Requires PIN to dismiss", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Switch(checked = isEnabled, onCheckedChange = { isEnabled = it; SecurityRepository.toggleSecurity(it) }, modifier = Modifier.testTag("security_toggle_switch"))
        }

        if (isEnabled) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(selected = securityType == SecurityType.PIN_4_DIGIT, onClick = { securityType = SecurityType.PIN_4_DIGIT; SecurityRepository.setSecurityType(SecurityType.PIN_4_DIGIT) }, label = { Text(if (isArabic) "رمز PIN" else "PIN", fontSize = 11.sp) }, modifier = Modifier.weight(1f))
                FilterChip(selected = securityType == SecurityType.BIOMETRIC, onClick = { securityType = SecurityType.BIOMETRIC; SecurityRepository.setSecurityType(SecurityType.BIOMETRIC) }, label = { Text(if (isArabic) "البصمة" else "Biometrics", fontSize = 11.sp) }, modifier = Modifier.weight(1f))
            }

            val savedPin by AppPreferencesManager.securityPin.collectAsState()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${if (isArabic) "رمز PIN الحالي:" else "Current PIN:"} ${savedPin.ifBlank { "••••" }}", fontSize = 12.sp)
                TextButton(onClick = { showPinEdit = true }) { Text(if (isArabic) "تغيير" else "Change") }
            }

            if (showPinEdit) {
                var newPinText by remember { mutableStateOf("") }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newPinText,
                        onValueChange = { if (it.length <= 4) newPinText = it.filter { c -> c.isDigit() } },
                        label = { Text(if (isArabic) "الرمز الجديد (4 أرقام)" else "New 4-digit PIN") },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            if (newPinText.length == 4) {
                                AppPreferencesManager.setSecurityPin(context, newPinText)
                                SecurityRepository.updatePin(newPinText)
                                showPinEdit = false
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) { Text(if (isArabic) "حفظ" else "Save") }
                }
            }
        }
    }
}
