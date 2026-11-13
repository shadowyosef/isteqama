package com.example.ui.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppAppearancePreferences
import com.example.data.AppPreferencesManager
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun FontScaleControlCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isAr by AppPreferencesManager.isArabic.collectAsState()
    val fontScale by AppAppearancePreferences.fontScale.collectAsState()
    val pct = (fontScale * 100f).roundToInt()
    val presets = listOf(1.0f to "100%", 1.25f to "125%", 1.5f to "150%", 2.0f to "200%", 3.0f to "300%")

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = modifier.fillMaxWidth().testTag("font_scale_card")
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.FormatSize, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = if (isAr) "حجم الخط بالتطبيق كاملاً" else "Global App Font Size",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "$pct%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("font_scale_value")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                presets.forEach { (scale, label) ->
                    val selected = abs(fontScale - scale) < 0.03f
                    FilterChip(
                        selected = selected,
                        onClick = { AppAppearancePreferences.setFontScale(context, scale) },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.heightIn(min = 42.dp).testTag("font_scale_chip_${label.removeSuffix("%")}")
                    )
                }
            }

            Slider(
                value = fontScale,
                onValueChange = { AppAppearancePreferences.setFontScale(context, it) },
                valueRange = 0.85f..3.0f,
                modifier = Modifier.fillMaxWidth().testTag("font_scale_slider")
            )

            Text(
                text = if (isAr) "معاينة: بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ • سبحان الله وبحمده" else "Preview: In the name of Allah • SubhanAllah",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
