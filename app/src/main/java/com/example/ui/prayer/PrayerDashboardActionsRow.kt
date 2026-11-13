package com.example.ui.prayer

import android.content.Context
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrayerDashboardActionsRow(
    context: Context,
    isAr: Boolean,
    latitude: Double,
    longitude: Double,
    onOpenQibla: () -> Unit,
    onOpenPostPrayer: () -> Unit
) {
    var showMosquesDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TextButton(onClick = onOpenQibla, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
            Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isAr) "القبلة" else "Qibla", fontSize = 11.sp)
        }
        TextButton(
            onClick = { showMosquesDialog = true },
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Icon(Icons.Default.Mosque, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isAr) "المساجد القريبة" else "Nearby Mosques", fontSize = 11.sp)
        }
        TextButton(onClick = onOpenPostPrayer, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
            Icon(Icons.Default.Spa, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isAr) "أذكار الصلاة" else "Post-Prayer", fontSize = 11.sp)
        }
    }

    if (showMosquesDialog) NearbyMosquesDialog(onDismiss = { showMosquesDialog = false })
}
