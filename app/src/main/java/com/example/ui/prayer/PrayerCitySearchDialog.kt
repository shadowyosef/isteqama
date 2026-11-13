package com.example.ui.prayer

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PrayerLocationsDatabase
import com.example.model.PrayerLocation
import com.example.util.AppStrings
import com.example.util.LocationDetectionHelper

@Composable
fun PrayerCitySearchDialog(
    currentLocation: PrayerLocation,
    onSelect: (PrayerLocation) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val isAr = AppStrings.isArabic

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true || perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            val detected = LocationDetectionHelper.detectCurrentLocation(context)
            if (detected != null) {
                onSelect(detected)
                onDismiss()
                Toast.makeText(context, if (isAr) "تم تحديد الموقع بنجاح" else "Location detected", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, if (isAr) "تعذر الحصول على إحداثيات GPS" else "GPS unavailable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val searchResults = remember(searchQuery) { PrayerLocationsDatabase.search(searchQuery) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.get("اختيار المدينة وتحديد الموقع", "Select City & Location"), fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 440.dp)) {
                Button(
                    onClick = {
                        if (LocationDetectionHelper.hasLocationPermission(context)) {
                            val detected = LocationDetectionHelper.detectCurrentLocation(context)
                            if (detected != null) {
                                onSelect(detected)
                                onDismiss()
                            } else {
                                Toast.makeText(context, if (isAr) "جاري البحث عن إشارة GPS..." else "Locating...", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            permissionLauncher.launch(
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("gps_detect_location_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(AppStrings.get("تحديد موقعي الحالي تلقائياً (GPS)", "Auto-Detect My GPS Location"), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(AppStrings.get("ابحث عن مدينة أو محافظة أو دولة...", "Search city or country..."), fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("city_search_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    items(searchResults) { loc ->
                        val isSelected = loc.nameAr == currentLocation.nameAr && loc.countryAr == currentLocation.countryAr
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = if (isAr) loc.nameAr else loc.nameEn,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = "${if (isAr) loc.countryAr else loc.countryEn} • UTC+${loc.timezoneHours.toInt()}",
                                    fontSize = 11.sp
                                )
                            },
                            trailingContent = {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                                }
                            },
                            modifier = Modifier.clickable {
                                onSelect(loc)
                                onDismiss()
                            }
                        )
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.get("إغلاق", "Close"))
            }
        }
    )
}
