package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.SurahVirtuesCatalog
import com.example.model.SurahInfo
import com.example.util.AppStrings

@Composable
fun SurahInfoDialog(
    surah: SurahInfo,
    onDismiss: () -> Unit
) {
    val isAr = AppStrings.isArabic
    val info = SurahVirtuesCatalog.getInfo(surah)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().widthIn(max = 520.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AutoStories, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = if (isAr) "${surah.displayNameAr} • بطاقة السورة" else "${surah.displayNameEn} Info",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${if (isAr) info.revelationTypeAr else surah.revelationType} • ${surah.versesCount} ${if (isAr) "آية" else "Ayahs"} • ${if (isAr) "تبدأ ص" else "Starts p."} ${surah.pageStart}",
                        modifier = Modifier.padding(10.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                SurahDetailBlock(if (isAr) "مقاصد السورة" else "Core Purpose", if (isAr) info.purposeAr else info.purposeEn)
                SurahDetailBlock(if (isAr) "فضل السورة" else "Virtues", if (isAr) info.virtueAr else info.virtueEn)
                SurahDetailBlock(if (isAr) "أسباب النزول والسياق" else "Revelation Context", info.reasonAr)

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text(AppStrings.close)
                }
            }
        }
    }
}

@Composable
private fun SurahDetailBlock(title: String, body: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(body, fontSize = 13.sp, lineHeight = 20.sp)
        }
    }
}
