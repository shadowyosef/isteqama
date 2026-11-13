package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OrnateIslamicCard

private data class LockTypeOption(
    val type: String,
    val titleAr: String,
    val titleEn: String,
    val descAr: String,
    val descEn: String,
    val icon: ImageVector
)

@Composable
fun LockWizardStep1Type(
    title: String,
    onTitleChange: (String) -> Unit,
    lockType: String,
    onTypeChange: (String) -> Unit,
    isArabic: Boolean
) {
    val options = listOf(
        LockTypeOption("QURAN", "قراءة قرآن أو ختمة", "Quran / Khatma", "تقرأ آيات أو صفحات لفتح الشاشة", "Read verses or pages to unlock", Icons.AutoMirrored.Filled.MenuBook),
        LockTypeOption("HIFZ", "تسميع وحفظ آيات", "Hifz Recitation", "تسميع صوتي أو تفاعلي لتثبيت الحفظ", "Voice or interactive memorization check", Icons.Default.RecordVoiceOver),
        LockTypeOption("DHIKR", "أذكار وأدعية", "Adhkar Routine", "أذكار الصباح والمساء أو أذكار تختارها", "Morning/Evening or custom Adhkar", Icons.Default.AutoAwesome),
        LockTypeOption("TASBEEH", "تسبيح واستغفار", "Tasbeeh Counter", "تتم عدداً محدداً من التسبيحات لفتح القفل", "Complete target Tasbeehs to unlock", Icons.Default.Psychology),
        LockTypeOption("QUIZ", "سؤال ديني تفاعلي", "Islamic Quiz", "سؤال معرفي ممتع ومختصر لفك القفل", "Answer a short Islamic knowledge quiz", Icons.Default.QuestionAnswer)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(if (isArabic) "اسم القفل (مثال: ورد الفجر أو أذكار المذاكرة)" else "Lock Title (e.g., Fajr Wird)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("lock_title_input")
        )

        Text(
            text = if (isArabic) "كيف تريد أن يُفتح القفل؟ اختر النوع:" else "How would you like to unlock? Pick a type:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        options.forEach { opt ->
            val selected = lockType == opt.type
            OrnateIslamicCard(
                cornerRadius = 14.dp,
                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            ) {
                Surface(
                    onClick = { onTypeChange(opt.type) },
                    color = androidx.compose.ui.graphics.Color.Transparent,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("lock_type_opt_${opt.type}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RadioButton(selected = selected, onClick = { onTypeChange(opt.type) })
                        Icon(opt.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(if (isArabic) opt.titleAr else opt.titleEn, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(if (isArabic) opt.descAr else opt.descEn, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
