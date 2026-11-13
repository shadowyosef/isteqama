package com.example.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager

@Composable
fun SetupStepPreferences(modifier: Modifier = Modifier) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val lockMode by AppPreferencesManager.lockEnforcementMode.collectAsState()
    val isDark by AppPreferencesManager.isDarkMode.collectAsState()

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = if (isArabic) "٢. أسلوب القفل والتخصيص" else "2. Lock Mode & Customization",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = if (isArabic) "اختر أسلوب حماية الشاشة وقت الورد:" else "Choose screen lock enforcement mode:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        val modes = listOf(
            Triple("SEAMLESS_OVERLAY", if (isArabic) "القفل الانسيابي (موصى به - بدون إشعارات)" else "Seamless Kiosk (Recommended)", if (isArabic) "قفل كامل ذكي بدون نوافذ نظام تثبيت الشاشة المزعجة" else "Full lock without annoying system pinning dialogs"),
            Triple("SYSTEM_OVERLAY", if (isArabic) "نافذة النظام (System Overlay)" else "System Overlay Window", if (isArabic) "طبقة فوق جميع النوافذ وشاشة القفل" else "Topmost floating window covering home screen"),
            Triple("NATIVE_PINNING", if (isArabic) "تثبيت شاشة أندرويد (Screen Pinning)" else "Native Android Pinning", if (isArabic) "يستخدم ميزة تثبيت الشاشة التابعة للنظام" else "Uses system Screen Pinning (shows OS dialog)")
        )

        modes.forEach { (modeKey, title, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (lockMode == modeKey) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                onClick = { AppPreferencesManager.setLockEnforcementMode(modeKey) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = lockMode == modeKey, onClick = { AppPreferencesManager.setLockEnforcementMode(modeKey) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        LockStylePreviewCard(currentMode = lockMode)

        Spacer(modifier = Modifier.height(2.dp))
        Text(if (isArabic) "تفضيلات العرض والمظهر:" else "Display & Theme:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = isArabic,
                onClick = { AppPreferencesManager.setLanguage(true) },
                label = { Text("العربية", fontSize = 11.sp) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = !isArabic,
                onClick = { AppPreferencesManager.setLanguage(false) },
                label = { Text("English", fontSize = 11.sp) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = isDark,
                onClick = { AppPreferencesManager.setDarkMode(!isDark) },
                label = { Text(if (isDark) "داكن" else "فاتح", fontSize = 11.sp) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
