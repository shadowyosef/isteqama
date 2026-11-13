package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppFontStyle
import com.example.ui.theme.getFontFamily

@Composable
fun QuranFontPreviewCard(
    selectedFont: AppFontStyle,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    val family = getFontFamily(selectedFont)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(12.dp)
            .testTag("quran_font_preview_box"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = if (isArabic) "معاينة الخط: ${selectedFont.labelAr}" else "Font Preview: ${selectedFont.labelEn}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = "﴿ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۝ الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ﴾",
            fontFamily = family,
            fontSize = 19.sp,
            lineHeight = 32.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (isArabic) "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ • رَبِّ اشْرَحْ لِي صَدْرِي" else "SubhanAllahi wa bihamdihi • Daily Wird Preview",
            fontFamily = family,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
