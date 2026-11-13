package com.example.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LockContentConfig

@Composable
fun QuranAyahConfigFields(
    config: LockContentConfig,
    onConfigChange: (LockContentConfig) -> Unit,
    isArabic: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                value = config.startAyah.toString(),
                onValueChange = {
                    val v = it.filter { c -> c.isDigit() }.toIntOrNull() ?: 1
                    onConfigChange(config.copy(startAyah = v, currentProgressAyah = v))
                },
                label = { Text(if (isArabic) "من آية" else "From", fontSize = 10.sp) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = config.endAyah.toString(),
                onValueChange = {
                    val v = it.filter { c -> c.isDigit() }.toIntOrNull() ?: 7
                    onConfigChange(config.copy(endAyah = v))
                },
                label = { Text(if (isArabic) "إلى آية" else "To", fontSize = 10.sp) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = config.ayahsPerSession.toString(),
                onValueChange = {
                    val v = it.filter { c -> c.isDigit() }.toIntOrNull() ?: 7
                    onConfigChange(config.copy(ayahsPerSession = v))
                },
                label = { Text(if (isArabic) "آيات/قفل" else "Per Lock", fontSize = 10.sp) },
                modifier = Modifier.weight(1.2f)
            )
        }
        Text(
            text = if (isArabic) "💡 يستأنف القفل تلقائياً بالمرة القادمة من حيث توقفت (مثلاً 1-7 ثم 8-14...)"
            else "💡 Next lock automatically resumes where you left off (e.g. 1-7 then 8-14...)",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
