package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KhatmaLockPlan
import com.example.util.TimeFormatUtils

@Composable
fun KhatmaSchedulePreviewCard(plan: KhatmaLockPlan, isArabic: Boolean) {
    val timesStr = plan.scheduleTimes.take(8).joinToString("، ") { (h, m) ->
        TimeFormatUtils.format12Hour(h, m, isArabic)
    } + if (plan.scheduleTimes.size > 8) "..." else ""

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "${if (isArabic) "الهدف اليومي:" else "Daily Goal:"} ${plan.pagesPerDay} ${if (isArabic) "صفحة باليوم" else "pages/day"} (${if (isArabic) "ختمة خلال" else "Khatma in"} ${plan.targetDays} ${if (isArabic) "يوم" else "days"})",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${if (isArabic) "أوقات التذكير:" else "Reminder window:"} ${TimeFormatUtils.format12Hour(plan.startHour, 0, isArabic)} - ${TimeFormatUtils.format12Hour(plan.endHour, 0, isArabic)} (${plan.activeHoursCount} ${if (isArabic) "ساعة نشطة" else "active hrs"} • ${plan.restHoursCount} ${if (isArabic) "ساعة نوم وراحة" else "rest hrs"})",
                fontSize = 11.sp
            )
            Text(
                text = "${if (isArabic) "مهمة كل قفل:" else "Per lock task:"} ${plan.pagesPerSession} ${if (isArabic) "صفحات في كل جلسة" else "pages per session"} (${plan.scheduleTimes.size} ${if (isArabic) "جلسات يومياً" else "sessions/day"})",
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${if (isArabic) "مواعيد الأقفال اليومية:" else "Lock times:"} $timesStr",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
