package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CustomThemeConfig

@Composable
fun CustomThemePreviewCard(cfg: CustomThemeConfig, isAr: Boolean) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(cfg.backgroundHex),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(cfg.primaryHex), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = RoundedCornerShape(10.dp), color = Color(cfg.surfaceHex), modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isAr) "معاينة البطاقة والنص الأمامي" else "Card & Foreground Preview",
                    color = Color(cfg.foregroundHex),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(cfg.primaryHex), contentColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) { Text(if (isAr) "زر أساسي" else "Primary", fontSize = 11.sp) }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(cfg.secondaryHex), contentColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) { Text(if (isAr) "زر ثانوي" else "Accent", fontSize = 11.sp) }
            }
        }
    }
}

@Composable
fun ThemeColorSwatchRow(label: String, selectedHex: Long, swatches: List<Long>, onSelect: (Long) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            swatches.forEach { hex ->
                val selected = selectedHex == hex
                Box(
                    modifier = Modifier
                        .size(if (selected) 36.dp else 32.dp)
                        .clip(CircleShape)
                        .background(Color(hex))
                        .border(if (selected) 3.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else Color.Gray, CircleShape)
                        .clickable { onSelect(hex) }
                )
            }
        }
    }
}
