package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FocusProfilesRepository
import com.example.data.MindfulGateRepository
import com.example.ui.theme.OrnateIslamicButton
import com.example.util.AppStrings
import kotlinx.coroutines.delay

@Composable
fun MindfulGatePromptCard(appPackage: String, appLabel: String, onBypassGranted: () -> Unit) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    var countdown by remember { mutableIntStateOf(5) }
    var dhikrClicks by remember { mutableIntStateOf(0) }

    LaunchedEffect(appPackage) {
        countdown = 5
        while (countdown > 0) { delay(1000); countdown-- }
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0A2419),
        modifier = Modifier.fillMaxWidth().padding(12.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.SelfImprovement, null, tint = Color(0xFFF5D061), modifier = Modifier.size(44.dp))
            Text(
                text = if (isAr) "وقفة ذكر قبل فتح $appLabel" else "Mindful Pause Before $appLabel",
                fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White, textAlign = TextAlign.Center
            )
            Text(
                text = if (isAr) "تنفس بهدوء واستغفر الله ٣ مرات قبل الدخول" else "Pause gently and say Astaghfirullah 3 times",
                fontSize = 12.sp, color = Color(0xFFA5D6A7), textAlign = TextAlign.Center
            )
            OrnateIslamicButton(
                onClick = {
                    if (dhikrClicks < 3) dhikrClicks++
                    if (dhikrClicks >= 3 && countdown == 0) {
                        FocusProfilesRepository.grantTemporaryBypass(appPackage, 5)
                        MindfulGateRepository.recordSavedScrollMinutes(context, 3)
                        onBypassGranted()
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("mindful_istighfar_btn")
            ) {
                Text(
                    if (countdown > 0) (if (isAr) "انتظر ($countdown) ث — أستغفر الله ($dhikrClicks/3)" else "Wait ${countdown}s ($dhikrClicks/3)")
                    else if (dhikrClicks < 3) (if (isAr) "أستغفر الله العظيم ($dhikrClicks/3)" else "Astaghfirullah ($dhikrClicks/3)")
                    else (if (isAr) "فتح مؤقت (٥ دقائق)" else "Continue for 5m")
                )
            }
            TextButton(onClick = {
                MindfulGateRepository.recordSavedScrollMinutes(context, 10)
                onBypassGranted()
            }) {
                Text(if (isAr) "تراجعت عن الفتح (وفّر ١٠ دقائق) ✓" else "Skip App (Save 10m) ✓", color = Color(0xFFF5D061))
            }
        }
    }
}
