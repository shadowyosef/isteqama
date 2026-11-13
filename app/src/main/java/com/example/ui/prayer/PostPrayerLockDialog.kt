package com.example.ui.prayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.PrayerTimesRepository
import com.example.model.PrayerType
import com.example.ui.lock.SequentialDhikrLockRunner
import com.example.util.AppStrings

@Composable
fun PostPrayerLockDialog(
    prayerType: PrayerType = PrayerType.DHUHR,
    onComplete: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var isPrayerMarked by remember {
        mutableStateOf(PrayerTimesRepository.prayedPrayersToday.value.contains(prayerType.id))
    }
    val adhkarItems = remember { PostPrayerAdhkarRepository.items }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("post_prayer_lock_dialog")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Mosque, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Text(
                        text = if (isAr) "قفل وتأكيد صلاة ${prayerType.nameAr}" else "Confirm & Post-Prayer Lock: ${prayerType.nameEn}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isPrayerMarked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isPrayerMarked) (if (isAr) "تم تأكيد أداء الصلاة ✓" else "Prayer confirmed prayed ✓")
                            else (if (isAr) "هل أديت فريضة ${prayerType.nameAr}؟" else "Did you pray ${prayerType.nameEn}?"),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        if (!isPrayerMarked) {
                            Button(
                                onClick = {
                                    isPrayerMarked = true
                                    PrayerTimesRepository.togglePrayerPrayed(context, prayerType, true)
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(if (isAr) "نعم صليتها" else "Yes, prayed", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Text(
                    text = if (isAr) "أكمل أذكار ما بعد الصلاة لفك القفل:" else "Complete post-prayer adhkar to unlock:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                SequentialDhikrLockRunner(
                    routineTitle = if (isAr) "أذكار ما بعد صلاة ${prayerType.nameAr}" else "Post-Prayer Adhkar",
                    items = adhkarItems,
                    queueInfo = "",
                    onAllCompleted = onComplete
                )

                TextButton(onClick = onDismiss) {
                    Text(if (isAr) "إغلاق مؤقت" else "Dismiss", fontSize = 11.sp)
                }
            }
        }
    }
}
