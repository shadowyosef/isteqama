package com.example.ui.kids

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.KidsModeCatalog
import com.example.util.AppStrings

@Composable
fun KidsModeDialog(onDismiss: () -> Unit) {
    val isAr = AppStrings.isArabic
    var stars by remember { mutableStateOf(0) }
    var completedIds by remember { mutableStateOf(setOf<String>()) }
    var qIndex by remember { mutableStateOf(0) }
    var quizFeedback by remember { mutableStateOf("") }
    val q = KidsModeCatalog.kidsQuiz[qIndex % KidsModeCatalog.kidsQuiz.size]

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth().padding(6.dp)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAr) "وضع الصغار الممتع 🌈" else "Kids Worship Mode 🌈", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)) {
                    Text(
                        text = if (isAr) "نجومي اليوم: ⭐ $stars نجمة" else "Today's Stars: ⭐ $stars",
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                KidsModeCatalog.dailySteps.forEach { item ->
                    val done = item.id in completedIds
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (done) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${item.emoji} ${if (isAr) item.titleAr else item.titleEn}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(item.textAr, fontSize = 13.sp)
                            Button(
                                onClick = {
                                    if (!done) {
                                        completedIds = completedIds + item.id
                                        stars += item.starsReward
                                    }
                                },
                                enabled = !done,
                                modifier = Modifier.fillMaxWidth()
                            ) { Text(if (done) "✓ ⭐+${item.starsReward}" else if (isAr) "قرأتها (+${item.starsReward} نجوم)" else "Done (+${item.starsReward} ⭐)") }
                        }
                    }
                }

                HorizontalDivider()
                Text(if (isAr) "مسابقة الصغار:" else "Kids Mini Quiz:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(if (isAr) q.questionAr else q.questionEn, fontSize = 13.sp)
                q.options.forEachIndexed { idx, opt ->
                    FilledTonalButton(
                        onClick = {
                            if (idx == q.correctIdx) {
                                stars += 5
                                qIndex++
                                quizFeedback = if (isAr) "رائع! +٥ نجوم ⭐" else "Awesome! +5 ⭐"
                            } else {
                                quizFeedback = if (isAr) "جرب الإجابة الأخرى 💡" else "Try again 💡"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(opt) }
                }
                if (quizFeedback.isNotEmpty()) Text(quizFeedback, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
