package com.example.ui.home

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StatsRepository
import com.example.ui.theme.OrnateIslamicCard
import com.example.ui.theme.arabesqueButtonRim
import com.example.util.AppStrings

@Composable
fun HomeQuickTasbeehWidget(
    onOpenTasbeeh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var count by remember { mutableIntStateOf(0) }
    val phrases = remember {
        listOf("سبحان الله وبحمده", "أستغفر الله وأتوب إليه", "لا حول ولا قوة إلا بالله", "اللهم صلِّ وسلم على نبينا محمد", "الحمد لله رب العالمين")
    }
    var selectedPhraseIndex by remember { mutableIntStateOf(0) }

    fun triggerVibration() {
        try {
            val v = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) v?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            else @Suppress("DEPRECATION") v?.vibrate(35)
        } catch (_: Exception) {}
    }

    OrnateIslamicCard(modifier = modifier.testTag("home_quick_tasbeeh_widget"), cornerRadius = 20.dp) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.AllInclusive, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Text(AppStrings.get("المسبحة الإلكترونية السريعة", "Quick Digital Tasbeeh"), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                IconButton(onClick = { count = 0 }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(18.dp))
                }
            }

            Text(
                text = phrases[selectedPhraseIndex],
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { selectedPhraseIndex = (selectedPhraseIndex + 1) % phrases.size }
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(AppStrings.get("اضغط على الدائرة للتسبيح", "Tap circle to count"), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .arabesqueButtonRim(rimColor = MaterialTheme.colorScheme.secondary, cornerRadius = 37.dp)
                        .clickable {
                            count += 1
                            StatsRepository.recordDhikrCount(1)
                            triggerVibration()
                        }
                        .testTag("quick_tasbeeh_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$count", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
