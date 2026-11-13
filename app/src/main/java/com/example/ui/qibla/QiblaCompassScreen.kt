package com.example.ui.qibla

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PrayerTimesRepository
import com.example.util.AppStrings
import com.example.util.CompassSensorManager
import com.example.util.QiblaCalculator
import kotlin.math.abs

@Composable
fun QiblaCompassScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val location by PrayerTimesRepository.currentLocation.collectAsState()

    val compassManager = remember { CompassSensorManager(context) }
    DisposableEffect(Unit) {
        compassManager.startListening()
        onDispose { compassManager.stopListening() }
    }

    val azimuth by compassManager.azimuthFlow.collectAsState()
    val qiblaBearing = remember(location) {
        QiblaCalculator.calculateQiblaBearing(location.latitude, location.longitude)
    }
    val distanceKm = remember(location) {
        QiblaCalculator.calculateDistanceToKaabaKm(location.latitude, location.longitude)
    }

    val relativeAngle = (qiblaBearing - azimuth + 360) % 360
    val isAligned = relativeAngle < 4f || relativeAngle > 356f

    LaunchedEffect(isAligned) {
        if (isAligned) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION") vibrator?.vibrate(40)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).padding(bottom = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (isAr) "اتجاه القبلة الشريفة" else "Qibla Direction", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("${if (isAr) location.nameAr else location.nameEn} • ${qiblaBearing.toInt()}° (${distanceKm} ${if (isAr) "كم إلى مكة" else "km to Makkah"})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
            // Rotating compass rose
            Canvas(modifier = Modifier.fillMaxSize().rotate(-azimuth)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2 - 8
                drawCircle(color = Color(0xFF1E382B), radius = radius)
                drawCircle(color = Color(0xFF4CAF50).copy(alpha = 0.5f), radius = radius, style = Stroke(width = 3.dp.toPx()))
            }

            // Needle pointer to Qibla
            Box(
                modifier = Modifier.size(220.dp).rotate(relativeAngle),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Mosque,
                        contentDescription = "Kaaba",
                        tint = if (isAligned) Color(0xFFFFD54F) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(34.dp)
                    )
                    Box(modifier = Modifier.width(4.dp).height(50.dp).clip(RoundedCornerShape(2.dp)).background(if (isAligned) Color(0xFFFFD54F) else MaterialTheme.colorScheme.primary))
                }
            }

            // Center status circle
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(if (isAligned) Color(0xFF2E7D32) else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (isAligned) (if (isAr) "باتجاه القبلة" else "Aligned") else "${relativeAngle.toInt()}°", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isAligned) Color.White else MaterialTheme.colorScheme.primary)
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isAligned) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Explore, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAligned) (if (isAr) "أنت الآن بمواجهة القبلة تماماً!" else "You are now facing the Qibla directly!")
                    else (if (isAr) "ضع الهاتف بشكل مستوٍ وأدره حتى يستقر المؤشر على القبلة." else "Keep phone flat and rotate until aligned with the Kaaba."),
                    fontSize = 11.sp
                )
            }
        }
    }
}
