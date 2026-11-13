package com.example.ui.prayer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.NearbyMosque
import com.example.data.NearbyMosquesRepository
import com.example.data.PrayerTimesRepository
import com.example.ui.theme.OrnateIslamicCard
import com.example.util.AppStrings

@Composable
fun NearbyMosquesDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val loc by PrayerTimesRepository.currentLocation.collectAsState()
    var mosques by remember { mutableStateOf<List<NearbyMosque>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(loc.latitude, loc.longitude) {
        isLoading = true
        val res = NearbyMosquesRepository.fetchNearbyMosques(loc.latitude, loc.longitude)
        isLoading = false
        res.onSuccess { mosques = it }.onFailure { errorMsg = if (isAr) "تعذر جلب المساجد، يمكنك البحث مباشرة عبر الخرائط." else "Could not load online mosques; open Maps directly." }
    }

    Dialog(onDismissRequest = onDismiss) {
        OrnateIslamicCard(cornerRadius = 22.dp, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.82f)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Mosque, null, tint = MaterialTheme.colorScheme.primary)
                        Text(if (isAr) "أقرب المساجد (${loc.nameAr})" else "Nearby Mosques", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close") }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        errorMsg?.let { Text(it, fontSize = 11.sp, color = MaterialTheme.colorScheme.error) }
                        if (mosques.isEmpty()) {
                            Text(if (isAr) "لا توجد نتائج محلية أو لا يوجد إنترنت — استخدم الزر أدناه للبحث في خرائط جوجل." else "Open Google Maps below to locate nearby mosques.", fontSize = 12.sp)
                        }
                        mosques.forEach { m ->
                            val dist = if (m.distanceMeters >= 1000) String.format("%.1f كم", m.distanceMeters / 1000f) else "${m.distanceMeters} م"
                            Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f), modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(m.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(dist, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                    }
                                    IconButton(
                                        onClick = {
                                            val uri = Uri.parse("geo:${m.latitude},${m.longitude}?q=${m.latitude},${m.longitude}(${Uri.encode(m.name)})")
                                            context.startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                                        },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(Icons.Default.Directions, contentDescription = "Directions", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                    Button(
                        onClick = {
                            val uri = Uri.parse("geo:${loc.latitude},${loc.longitude}?q=مسجد")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    ) {
                        Text(if (isAr) "فتح أقرب المساجد على الخرائط" else "Open Mosques in Maps")
                    }
                }
            }
        }
    }
}
