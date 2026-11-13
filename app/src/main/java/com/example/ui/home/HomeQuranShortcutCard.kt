package com.example.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.StatsRepository
import com.example.ui.theme.OrnateIslamicButton
import com.example.ui.theme.OrnateIslamicCard
import com.example.util.AppStrings

@Composable
fun HomeQuranShortcutCard(
    onOpenQuran: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userStats by StatsRepository.stats.collectAsState()
    val ayahsReadToday = userStats.ayahsReadToday
    val isAr = AppStrings.isArabic

    OrnateIslamicCard(
        modifier = modifier.testTag("home_quran_shortcut_card"),
        cornerRadius = 20.dp
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_quran_illumination_1794560217133),
            contentDescription = AppStrings.get("زخرفة المصحف الشريف", "Quran Illumination"),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
        )
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.AutoStories, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Text(
                        text = AppStrings.get("القرآن الكريم والورد اليومي", "Holy Quran & Daily Ward"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Badge(containerColor = MaterialTheme.colorScheme.primary) {
                    Text(
                        text = "$ayahsReadToday " + if (isAr) "آيات اليوم" else "ayahs today",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }

            Text(
                text = AppStrings.get("وردك القرآني المنتظم، اقرأ وتدبر واصل تلاوتك من حيث وقفت.", "Continue your Quran recitation and daily ward."),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OrnateIslamicButton(
                onClick = onOpenQuran,
                modifier = Modifier.fillMaxWidth().testTag("home_open_quran_btn")
            ) {
                Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(AppStrings.get("متابعة قراءة القرآن والورد", "Continue Reading Quran"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}
