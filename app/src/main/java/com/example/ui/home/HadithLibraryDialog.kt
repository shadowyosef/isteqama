package com.example.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.HadithLibraryCatalog
import com.example.data.MindfulGateRepository
import com.example.ui.quran.QuranCardShareHelper
import com.example.ui.theme.OrnateIslamicCard
import com.example.util.AppStrings

@Composable
fun HadithLibraryDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var query by remember { mutableStateOf("") }
    val hadiths = remember(query) { HadithLibraryCatalog.search(query) }
    val lockHadithEnabled by MindfulGateRepository.hadithLockEnabled.collectAsState()

    LaunchedEffect(Unit) { MindfulGateRepository.init(context) }

    Dialog(onDismissRequest = onDismiss) {
        OrnateIslamicCard(cornerRadius = 22.dp, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.88f)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.MenuBook, null, tint = MaterialTheme.colorScheme.primary)
                        Text(if (isAr) "مكتبة الأحاديث النبوية" else "Prophetic Hadith Library", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "إظهار حديث اليوم في ويدجت وشاشة القفل" else "Enable Daily Hadith Reminder", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Switch(checked = lockHadithEnabled, onCheckedChange = { MindfulGateRepository.setHadithLockEnabled(context, it) })
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text(if (isAr) "ابحث في الأحاديث..." else "Search Hadiths...", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    hadiths.forEach { item ->
                        Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (isAr) item.titleAr else item.titleEn, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                    IconButton(
                                        onClick = { QuranCardShareHelper.shareOrnateStoryCard(context, "قال رسول الله ﷺ:", item.arabicText, "${item.sourceAr} • ${item.narratorAr}") },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share Story", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Text(if (isAr) item.arabicText else item.englishText, fontSize = 13.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium)
                                Text("${item.sourceAr} • ${item.narratorAr}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                                Text(item.benefitAr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
