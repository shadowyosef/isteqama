package com.example.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.OrnateIslamicCard
import com.example.util.AppStrings
import com.example.util.PrayerSeasonsHelper
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeHeroBanner(modifier: Modifier = Modifier) {
    val isAr = AppStrings.isArabic
    val todayStr = remember(isAr) {
        val locale = if (isAr) Locale.forLanguageTag("ar") else Locale.ENGLISH
        SimpleDateFormat("EEEE، d MMMM yyyy", locale).format(Date())
    }
    val season = remember(isAr) { PrayerSeasonsHelper.getSeasonalBanner(isAr) }

    OrnateIslamicCard(modifier = modifier, cornerRadius = 22.dp) {
        Box(modifier = Modifier.fillMaxWidth().height(136.dp)) {
            Image(
                painter = painterResource(id = R.drawable.img_islamic_hero_banner_1794560217113),
                contentDescription = AppStrings.get("زخرفة إسلامية", "Islamic Ornament Banner"),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x66041A13), Color(0xCC041A13))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = AppStrings.get("السلام عليكم ورحمة الله", "As-salamu Alaykum"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFF8E1)
                )
                Text(
                    text = todayStr,
                    fontSize = 12.sp,
                    color = Color(0xFFE2E8F0)
                )
                if (season != null) {
                    Spacer(Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.28f)
                    ) {
                        Text(
                            text = season,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE68A),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
