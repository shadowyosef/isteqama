package com.example.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.OrnateIslamicCard

@Composable
fun SetupStepLocksDefaults(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val lockMode by AppPreferencesManager.lockEnforcementMode.collectAsState()
    val mindfulGate by MindfulGateRepository.mindfulGateEnabled.collectAsState()
    val prayerFreeze by MindfulGateRepository.prayerFreezeEnabled.collectAsState()
    val prayerLock by PrayerTimesRepository.isPrayerLockEnabled.collectAsState()
    val locks by ScreenLockRepository.locks.collectAsState()
    val tasbeehs by TasbeehScheduleRepository.schedules.collectAsState()

    val khatmaOn = locks.find { it.id == "lock_monthly_khatma" }?.isEnabled ?: true
    val morningOn = locks.find { it.id == "lock_morning_adhkar" }?.isEnabled ?: true
    val eveningOn = locks.find { it.id == "lock_evening_adhkar" }?.isEnabled ?: true
    val tasbeehOn = tasbeehs.find { it.id == "tasbeeh_default_all_random" }?.isEnabled ?: true

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OrnateIslamicCard {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.SelfImprovement, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(if (isArabic) "تفعيل أو إيقاف الأوراد الافتراضية:" else "Toggle Default Worship Locks:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                SwitchRow(
                    label = if (isArabic) "ختمة الشهر (~٢١ صفحة/يوم • هدوء ٢٣–٠٨)" else "Monthly Khatma (~21p/day • DND 23–08)",
                    checked = khatmaOn, tag = "toggle_default_khatma",
                    onCheckedChange = { ScreenLockRepository.setLockEnabled("lock_monthly_khatma", it) }
                )
                SwitchRow(
                    label = if (isArabic) "أذكار الصباح (بعد الفجر بساعة)" else "Morning Adhkar (+1h after Fajr)",
                    checked = morningOn, tag = "toggle_default_morning",
                    onCheckedChange = { ScreenLockRepository.setLockEnabled("lock_morning_adhkar", it) }
                )
                SwitchRow(
                    label = if (isArabic) "أذكار المساء (بعد العصر بساعة)" else "Evening Adhkar (+1h after Asr)",
                    checked = eveningOn, tag = "toggle_default_evening",
                    onCheckedChange = { ScreenLockRepository.setLockEnabled("lock_evening_adhkar", it) }
                )
                SwitchRow(
                    label = if (isArabic) "أذكار بعد الصلاة (+١٥ د مع خيارات التأجيل)" else "Post-Prayer Adhkar (+15m w/ snooze)",
                    checked = prayerLock, tag = "toggle_default_prayer_lock",
                    onCheckedChange = { PrayerTimesRepository.togglePrayerLock(context, it) }
                )
                SwitchRow(
                    label = if (isArabic) "تسابيح عشوائية كل ٣٠ دقيقة" else "Random Tasbeeh Toast (Every 30m)",
                    checked = tasbeehOn, tag = "toggle_default_tasbeeh",
                    onCheckedChange = { TasbeehScheduleRepository.setScheduleEnabled("tasbeeh_default_all_random", it) }
                )
            }
        }

        OrnateIslamicCard {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(if (isArabic) "حماية التركيز وأسلوب القفل:" else "Focus Guards & Lock Mode:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                SwitchRow(
                    label = if (isArabic) "بوابة التمهل قبل التطبيقات المشتتة" else "Mindful Pause Gate",
                    checked = mindfulGate,
                    onCheckedChange = { MindfulGateRepository.setMindfulGate(context, it) }
                )
                SwitchRow(
                    label = if (isArabic) "تجميد المشتتات وقت الصلاة (٢٠ د)" else "Prayer Focus Freeze (20m)",
                    checked = prayerFreeze,
                    onCheckedChange = { MindfulGateRepository.setPrayerFreeze(context, it) }
                )
                val modes = listOf(
                    "SEAMLESS_OVERLAY" to (if (isArabic) "انسيابي ذكي" else "Seamless"),
                    "SYSTEM_OVERLAY" to (if (isArabic) "نافذة نظام" else "System Overlay"),
                    "NATIVE_PINNING" to (if (isArabic) "تثبيت النظام" else "Pinning")
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    modes.forEach { (k, lbl) ->
                        FilterChip(
                            selected = lockMode == k,
                            onClick = { AppPreferencesManager.setLockEnforcementMode(k) },
                            label = { Text(lbl, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f).testTag("lock_mode_chip_$k")
                        )
                    }
                }
                LockStylePreviewCard(currentMode = lockMode)
            }
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, tag: String = "", onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 11.sp, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange, modifier = if (tag.isNotBlank()) Modifier.testTag(tag) else Modifier)
    }
}
