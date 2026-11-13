package com.example.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuranRepository
import com.example.model.Ayah

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuranPageContentBody(
    ayahs: List<Ayah>,
    selectedAyah: Int?,
    fontSize: Float,
    isDarkTheme: Boolean,
    onSelectAyah: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (isDarkTheme) Color(0xFFF3F4F6) else Color(0xFF1E293B)
    val highlightColor = if (isDarkTheme) Color(0x662E7D32) else Color(0x66FFE082)
    val bannerBg = if (isDarkTheme) Color(0xFF1B2820) else Color(0xFFF0EBE0)
    val bannerBorder = if (isDarkTheme) Color(0xFFC5A059) else Color(0xFFD4AF37)
    val headerColor = if (isDarkTheme) Color(0xFFFFD54F) else Color(0xFFB45309)

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        var currentRenderSurah = -1
        ayahs.forEach { ayah ->
            if (ayah.surahNumber != currentRenderSurah) {
                currentRenderSurah = ayah.surahNumber
                val sInfo = QuranRepository.getSurah(currentRenderSurah)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = bannerBg,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).border(1.dp, bannerBorder, RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = "سورة ${sInfo?.nameAr ?: ""}",
                        fontSize = (fontSize * 0.9f).coerceIn(14f, 22f).sp,
                        fontWeight = FontWeight.Bold,
                        color = headerColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                if (ayah.numberInSurah == 1 && currentRenderSurah != 9 && currentRenderSurah != 1) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        fontSize = (fontSize * 0.95f).coerceIn(15f, 24f).sp,
                        fontWeight = FontWeight.Bold,
                        color = headerColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ayahs.forEach { ayah ->
                val isSelected = selectedAyah == ayah.numberInSurah
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) highlightColor else Color.Transparent)
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) bannerBorder else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectAyah(ayah.numberInSurah) }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${ayah.textArabic} ﴿${ayah.numberInSurah}﴾",
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * 1.85f).sp,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
