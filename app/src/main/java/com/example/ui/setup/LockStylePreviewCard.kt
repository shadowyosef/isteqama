package com.example.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
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
import com.example.data.AppPreferencesManager
import com.example.data.local.entity.ScreenLockEntity
import com.example.ui.lock.ActiveLockActivity

@Composable
fun LockStylePreviewCard(currentMode: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isArabic by AppPreferencesManager.isArabic.collectAsState()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "معاينة شكل وسلوك القفل" else "Lock Style Preview",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = {
                        val previewLock = ScreenLockEntity(
                            id = "preview_lock_${System.currentTimeMillis()}",
                            title = if (isArabic) "معاينة تجريبية لورد القفل" else "Lock Preview Demo",
                            lockType = "QURAN",
                            targetAmount = 2
                        )
                        ActiveLockActivity.launch(context, previewLock)
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.heightIn(min = 36.dp).testTag("live_preview_lock_btn")
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isArabic) "معاينة فورية" else "Live Preview", fontSize = 10.sp)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(116.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = if (currentMode == "SYSTEM_OVERLAY") 2.dp else 1.dp,
                        color = if (currentMode == "SYSTEM_OVERLAY") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                when (currentMode) {
                    "SYSTEM_OVERLAY" -> Surface(
                        shape = RoundedCornerShape(10.dp),
                        tonalElevation = 4.dp,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxSize().padding(2.dp)
                    ) {
                        MockPreviewBox(
                            title = if (isArabic) "نافذة نظام ثابتة الإطار فوق التطبيقات" else "Fixed-Frame System Overlay Window",
                            desc = if (isArabic) "تحافظ على أبعاد نافذة ثابتة ومقروءة فوق التطبيقات" else "Steady fixed window geometry over apps",
                            isAlert = false
                        )
                    }
                    "NATIVE_PINNING" -> MockPreviewBox(
                        title = if (isArabic) "تثبيت شاشة أندرويد (Screen Pinning)" else "Native Android Screen Pinning",
                        desc = if (isArabic) "يُظهر إشعار النظام 'Screen pinning' ويتطلب إلغاء التثبيت" else "Shows OS pinning dialog with gesture exit",
                        isAlert = true
                    )
                    else -> MockPreviewBox(
                        title = if (isArabic) "قفل كامل ذكي بدون إشعارات نظام" else "Seamless Fullscreen Lock (No Popups)",
                        desc = if (isArabic) "يحمي الشاشة ويمنع الخروج بزر Home حتى إتمام الورد" else "Prevents Home exit until ward is completed",
                        isAlert = false
                    )
                }
            }
        }
    }
}

@Composable
private fun MockPreviewBox(title: String, desc: String, isAlert: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, tint = if (isAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1)
        Text(text = desc, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, maxLines = 2)
    }
}
