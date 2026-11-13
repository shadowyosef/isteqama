package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.HifzLoopAndReflectionRepository
import com.example.model.Ayah
import com.example.util.AppStrings
import com.example.util.TajweedHighlighter

@Composable
fun HifzPracticeDialog(
    ayahs: List<Ayah>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val loopCount by HifzLoopAndReflectionRepository.hifzLoopCount.collectAsState()
    val tajweed by HifzLoopAndReflectionRepository.tajweedEnabled.collectAsState()
    var maskWords by remember { mutableStateOf(false) }
    var showVoiceHifz by remember { mutableStateOf(false) }
    var sampleIdx by remember { mutableStateOf(0) }
    var quizIdx by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf("") }

    val sampleAyah = ayahs.getOrNull(sampleIdx % ayahs.size.coerceAtLeast(1))
    val q = HifzLoopAndReflectionRepository.mutashabihatBank[quizIdx % HifzLoopAndReflectionRepository.mutashabihatBank.size]

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "مساعد الحفظ والتجويد" else "Hifz & Tajweed Studio", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Button(onClick = { showVoiceHifz = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Icon(Icons.Default.Mic, null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (isAr) "اختبار التسميع الصوتي بالمايكروفون" else "Voice Hifz Verifier", fontSize = 12.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "تلوين أحكام التجويد (مد/غنة/قلقلة)" else "Color-Coded Tajweed", fontSize = 12.sp)
                    Switch(checked = tajweed, onCheckedChange = { HifzLoopAndReflectionRepository.toggleTajweed(context) })
                }

                Text(if (isAr) "عدد تكرار الآية للحفظ:" else "Ayah Loop Count:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1, 3, 5, 7, 10).forEach { cnt ->
                        FilterChip(
                            selected = loopCount == cnt,
                            onClick = { HifzLoopAndReflectionRepository.setLoopCount(context, cnt) },
                            label = { Text("${cnt}x", fontSize = 11.sp) }
                        )
                    }
                }

                if (sampleAyah != null) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isAr) "إخفاء كلمات الآية للتسميع" else "Mask Words Drill", fontSize = 12.sp)
                        Switch(checked = maskWords, onCheckedChange = { maskWords = it })
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) {
                        val txt = if (maskWords) TajweedHighlighter.maskWordsForHifz(sampleAyah.textArabic) else sampleAyah.textArabic
                        Text(txt, modifier = Modifier.fillMaxWidth().padding(10.dp), fontSize = 15.sp, lineHeight = 24.sp)
                    }
                    TextButton(onClick = { sampleIdx++ }) {
                        Text(if (isAr) "الآية التالية للتسميع ◀" else "Next Ayah ▶")
                    }
                }

                HorizontalDivider()
                Text(if (isAr) "تحدي المتشابهات القرآنية:" else "Mutashabihat Challenge:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                Text(if (isAr) q.promptAr else q.promptEn, fontSize = 12.sp)
                q.options.forEachIndexed { idx, opt ->
                    OutlinedButton(
                        onClick = {
                            feedback = if (idx == q.correctIndex) {
                                quizIdx++
                                if (isAr) "إجابة صحيحة! أحسنت" else "Correct!"
                            } else if (isAr) "حاول مرة أخرى" else "Try again"
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(opt, fontSize = 12.sp) }
                }
                if (feedback.isNotEmpty()) Text(feedback, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }

    if (showVoiceHifz) VoiceHifzDialog(ayahs = ayahs, initialIndex = sampleIdx, onDismiss = { showVoiceHifz = false })
}
