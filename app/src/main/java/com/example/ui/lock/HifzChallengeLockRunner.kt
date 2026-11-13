package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferencesManager
import com.example.util.HifzChallengeHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HifzChallengeLockRunner(
    surahIds: List<Int>,
    onSuccess: () -> Unit
) {
    val isAr by AppPreferencesManager.isArabic.collectAsState()
    var roundSeed by remember { mutableIntStateOf(0) }
    val challenge = remember(surahIds, roundSeed) { HifzChallengeHelper.buildChallenge(surahIds) }
    var filledWords by remember(challenge) { mutableStateOf<List<String>>(emptyList()) }
    var feedbackError by remember(challenge) { mutableStateOf(false) }

    val expectedWords = remember(challenge) { challenge.blankIndices.map { challenge.words[it] } }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().testTag("hifz_challenge_card")
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (isAr) "تحدي الحفظ: ${challenge.surahName} (${challenge.ayahNumber})" else "Hifz Challenge (${challenge.ayahNumber})",
                        fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { roundSeed++ }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "Next Verse", modifier = Modifier.size(18.dp))
                }
            }

            val displayText = remember(challenge, filledWords) {
                var fillPos = 0
                challenge.words.mapIndexed { idx, word ->
                    if (idx in challenge.blankIndices) filledWords.getOrNull(fillPos++)?.let { "[$it]" } ?: "[ ____ ]" else word
                }.joinToString(" ")
            }

            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f), modifier = Modifier.fillMaxWidth()) {
                Text(displayText, fontSize = 16.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(12.dp))
            }

            Text(
                text = if (isAr) "أكمل الكلمات الناقصة بالترتيب الصحيح لفك القفل:" else "Tap missing words in order to unlock:",
                fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                challenge.candidates.forEach { word ->
                    val alreadyUsed = filledWords.count { it == word } >= expectedWords.count { it == word }.coerceAtLeast(1)
                    FilterChip(
                        selected = word in filledWords,
                        onClick = {
                            feedbackError = false
                            if (!alreadyUsed && filledWords.size < expectedWords.size) {
                                val nextList = filledWords + word
                                filledWords = nextList
                                if (nextList.size == expectedWords.size) {
                                    if (nextList == expectedWords) onSuccess()
                                    else {
                                        feedbackError = true
                                        filledWords = emptyList()
                                    }
                                }
                            }
                        },
                        label = { Text(word, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            if (feedbackError) {
                Text(if (isAr) "الترتيب غير صحيح، حاول مرة أخرى" else "Incorrect order, try again", fontSize = 11.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }

            if (filledWords.isNotEmpty()) {
                TextButton(onClick = { filledWords = emptyList(); feedbackError = false }, modifier = Modifier.align(Alignment.End)) {
                    Text(if (isAr) "مسح الاختيار" else "Clear", fontSize = 11.sp)
                }
            }
        }
    }
}
