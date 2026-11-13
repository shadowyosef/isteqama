package com.example.ui.lock

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
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
import com.example.data.IslamicQuizCatalog
import com.example.model.IslamicQuizQuestion
import com.example.util.AppStrings

@Composable
fun IslamicQuizLockRunner(
    question: IslamicQuizQuestion = remember { IslamicQuizCatalog.getRandomQuestion() },
    onAnswerCorrect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = AppStrings.isArabic
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }

    val options = if (isAr) question.optionsAr else question.optionsEn

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13231B)),
        modifier = modifier.fillMaxWidth().testTag("islamic_quiz_lock_runner")
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Text(
                        text = if (isAr) "تحدي السؤال الديني لفك القفل" else "Islamic Quiz Unlock Challenge",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Badge(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
                    Text(if (isAr) question.category.labelAr else question.category.labelEn, color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, modifier = Modifier.padding(4.dp))
                }
            }

            Text(
                text = if (isAr) question.questionAr else question.questionEn,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 24.sp,
                textAlign = if (isAr) TextAlign.Right else TextAlign.Left,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )

            options.forEachIndexed { index, optionText ->
                val isSelected = selectedOptionIndex == index
                val isCorrect = index == question.correctIndex
                val backgroundColor = when {
                    !isAnswerSubmitted -> if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.07f)
                    isCorrect -> Color(0xFF1B5E20).copy(alpha = 0.7f)
                    isSelected -> Color(0xFFB71C1C).copy(alpha = 0.6f)
                    else -> Color.White.copy(alpha = 0.05f)
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = backgroundColor,
                    modifier = Modifier.fillMaxWidth().clickable(enabled = !isAnswerSubmitted) {
                        selectedOptionIndex = index
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${index + 1}. $optionText",
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        if (isAnswerSubmitted && isCorrect) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(20.dp))
                        } else if (isAnswerSubmitted && isSelected && !isCorrect) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFE57373), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isAnswerSubmitted) {
                val expl = if (isAr) question.explanationAr else question.explanationEn
                if (expl.isNotBlank()) {
                    Surface(shape = RoundedCornerShape(10.dp), color = Color.White.copy(alpha = 0.08f), modifier = Modifier.fillMaxWidth()) {
                        Text(text = "💡 $expl", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f), modifier = Modifier.padding(10.dp), lineHeight = 18.sp)
                    }
                }
            }

            Button(
                onClick = {
                    if (!isAnswerSubmitted) {
                        if (selectedOptionIndex != null) {
                            isAnswerSubmitted = true
                            if (selectedOptionIndex == question.correctIndex) {
                                onAnswerCorrect()
                            }
                        }
                    } else {
                        if (selectedOptionIndex == question.correctIndex) {
                            onAnswerCorrect()
                        } else {
                            // reset to try again
                            selectedOptionIndex = null
                            isAnswerSubmitted = false
                        }
                    }
                },
                enabled = selectedOptionIndex != null,
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_quiz_answer_btn")
            ) {
                Text(
                    text = when {
                        !isAnswerSubmitted -> if (isAr) "تأكيد الإجابة وفك القفل" else "Confirm Answer & Unlock"
                        selectedOptionIndex == question.correctIndex -> if (isAr) "إجابة صحيحة ✓ فتح القفل" else "Correct ✓ Unlock"
                        else -> if (isAr) "إجابة خاطئة ✗ حاول مجدداً" else "Incorrect ✗ Try Again"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
