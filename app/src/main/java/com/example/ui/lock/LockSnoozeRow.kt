package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AppStrings

@Composable
fun LockSnoozeRow(onSnoozeMinutes: (Int) -> Unit) {
    val isAr = AppStrings.isArabic
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = if (isAr) "تأجيل القفل:" else "Postpone Lock:",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFDE68A)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(5, 10, 15, 20).forEach { mins ->
                OutlinedButton(
                    onClick = { onSnoozeMinutes(mins) },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                ) {
                    Text(if (isAr) "$mins د" else "${mins}m", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}
