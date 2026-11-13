package com.example.ui.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AppStrings

@Composable
fun QuranPageBottomBar(
    pageOffset: Int,
    totalPages: Int,
    selectedAyah: Int,
    isDarkTheme: Boolean,
    onPreviousPage: () -> Unit,
    onNextOrFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomBg = if (isDarkTheme) Color(0xFF141E18) else Color(0xFFF1EFE6)
    val reachedColor = if (isDarkTheme) Color(0xFFFFD54F) else Color(0xFFB45309)

    Surface(modifier = modifier.fillMaxWidth(), color = bottomBg, shadowElevation = 8.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
            if (selectedAyah > 0) {
                Text(
                    text = "${AppStrings.get("وصلت إلى الآية", "Reached Ayah")} $selectedAyah ${AppStrings.get("في هذه الصفحة", "on this page")}",
                    fontSize = 11.sp, color = reachedColor,
                    fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (pageOffset > 0) {
                    OutlinedButton(
                        onClick = onPreviousPage, modifier = Modifier.weight(0.7f).height(46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(AppStrings.get("السابق", "Prev"), fontSize = 12.sp)
                    }
                }
                Button(
                    onClick = onNextOrFinish,
                    modifier = Modifier.weight(1f).height(46.dp).testTag("quran_page_lock_action_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val isLast = pageOffset + 1 >= totalPages
                    val label = if (isLast) AppStrings.get("أتممت قراءة الورد وفتح القفل ✓", "Finish & Unlock ✓")
                    else "${AppStrings.get("الصفحة التالية", "Next Page")} (${pageOffset + 2}/$totalPages)"
                    Icon(if (isLast) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
