package com.example.ui.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BadgesRepository
import com.example.ui.theme.OrnateIslamicCard

@Composable
fun BadgesGalleryCard(isArabic: Boolean) {
    val badges = remember { BadgesRepository.getBadges() }
    val earnedCount = badges.count { it.earned }

    OrnateIslamicCard(cornerRadius = 18.dp) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = if (isArabic) "أوسمة وسلاسل الإنجاز" else "Streaks & Badges",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("$earnedCount / ${badges.size}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
            }
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                badges.forEach { badge ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (badge.earned) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (badge.earned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.width(125.dp).alpha(if (badge.earned) 1f else 0.55f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(badge.emoji, fontSize = 22.sp)
                            Text(if (isArabic) badge.titleAr else badge.titleEn, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                            Text(if (isArabic) badge.descAr else badge.descEn, fontSize = 10.sp, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}
