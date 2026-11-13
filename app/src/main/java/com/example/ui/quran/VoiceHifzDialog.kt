package com.example.ui.quran

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.model.Ayah
import com.example.ui.theme.OrnateIslamicButton
import com.example.ui.theme.OrnateIslamicCard
import com.example.util.AppStrings
import com.example.util.HifzWordResult
import com.example.util.VoiceHifzVerifier

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceHifzDialog(ayahs: List<Ayah>, initialIndex: Int = 0, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var idx by remember { mutableStateOf(initialIndex) }
    var isListening by remember { mutableStateOf(false) }
    var spokenText by remember { mutableStateOf("") }
    var wordResults by remember { mutableStateOf<List<HifzWordResult>>(emptyList()) }
    var scorePct by remember { mutableStateOf<Int?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val ayah = ayahs.getOrNull(idx % ayahs.size.coerceAtLeast(1))
    val launchSpeech = {
        if (ayah != null) {
            errorMsg = null
            VoiceHifzVerifier.startListening(
                context = context,
                onResult = { spoken ->
                    spokenText = spoken
                    val (res, pct) = VoiceHifzVerifier.evaluateRecitation(ayah.textArabic, spoken)
                    wordResults = res
                    scorePct = pct
                },
                onListeningChanged = { isListening = it },
                onError = { errorMsg = it }
            )
        }
    }
    val micPermLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchSpeech()
        else errorMsg = if (isAr) "يرجى منح إذن المايكروفون لتفعيل التسميع الصوتي" else "Microphone permission required"
    }

    DisposableEffect(Unit) { onDispose { VoiceHifzVerifier.stop() } }

    Dialog(onDismissRequest = { VoiceHifzVerifier.stop(); onDismiss() }) {
        OrnateIslamicCard(cornerRadius = 22.dp, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "اختبار التسميع الصوتي" else "Voice Hifz Verifier", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = { VoiceHifzVerifier.stop(); onDismiss() }) { Icon(Icons.Default.Close, "Close") }
                }

                if (ayah != null) {
                    Text("${if (isAr) "الآية" else "Ayah"} ${ayah.numberInSurah}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    if (wordResults.isEmpty()) {
                        Text(if (isAr) "اضغط الزر وابدأ بتلاوة الآية غيباً ليتم تقييم الكلمات." else "Tap Mic and recite the ayah from memory.", fontSize = 12.sp)
                    } else {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            wordResults.forEach { w ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (w.isMatched) Color(0xFF1B5E20).copy(alpha = 0.22f) else MaterialTheme.colorScheme.errorContainer
                                ) {
                                    Text(
                                        w.originalWord,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (w.isMatched) Color(0xFF1B5E20) else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        scorePct?.let { pct ->
                            Text(
                                "${if (isAr) "دقة التسميع:" else "Accuracy:"} $pct%",
                                fontWeight = FontWeight.ExtraBold,
                                color = if (pct >= 80) Color(0xFF2E7D32) else MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    if (spokenText.isNotBlank()) Text("${if (isAr) "ما تم سماعه:" else "Heard:"} $spokenText", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    errorMsg?.let { Text(it, fontSize = 11.sp, color = MaterialTheme.colorScheme.error) }

                    OrnateIslamicButton(
                        onClick = {
                            if (isListening) { VoiceHifzVerifier.stop(); isListening = false }
                            else if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) launchSpeech()
                            else micPermLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("voice_hifz_mic_btn")
                    ) {
                        Icon(if (isListening) Icons.Default.Stop else Icons.Default.Mic, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isListening) (if (isAr) "إيقاف التسميع" else "Stop Listening") else (if (isAr) "ابدأ التسميع الآن" else "Start Reciting"))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = { if (idx > 0) { idx--; wordResults = emptyList(); spokenText = ""; scorePct = null } }) {
                            Text(if (isAr) "السابقة" else "Prev")
                        }
                        TextButton(onClick = { idx++; wordResults = emptyList(); spokenText = ""; scorePct = null }) {
                            Text(if (isAr) "الآية التالية" else "Next Ayah")
                        }
                    }
                }
            }
        }
    }
}
