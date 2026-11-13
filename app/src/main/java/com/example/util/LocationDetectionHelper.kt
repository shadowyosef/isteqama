package com.example.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.model.PrayerLocation
import java.util.Locale
import kotlin.math.round

object LocationDetectionHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    fun detectCurrentLocation(context: Context): PrayerLocation? {
        if (!hasLocationPermission(context)) return null
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null

        val loc: Location? = try {
            val gpsLoc = if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            } else null
            val netLoc = if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            } else null
            gpsLoc ?: netLoc
        } catch (_: Exception) { null }

        if (loc == null) return null

        val lat = loc.latitude
        val lng = loc.longitude
        val inJordan = lat in 29.0..33.5 && lng in 34.5..39.5
        val tz = if (inJordan) 3.0 else round(lng / 15.0).coerceIn(-12.0, 14.0)

        var cityName = if (inJordan) "موقعي (الأردن)" else "موقعي الحالي"
        var cityEn = if (inJordan) "Current Location (Jordan)" else "Current Location"
        var countryAr = if (inJordan) "الأردن" else ""
        var countryEn = if (inJordan) "Jordan" else ""

        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                val addr = addresses?.firstOrNull()
                if (addr != null) {
                    val locality = addr.locality ?: addr.subAdminArea ?: addr.adminArea
                    if (!locality.isNullOrBlank()) {
                        cityName = locality
                        cityEn = locality
                    }
                    if (!addr.countryName.isNullOrBlank()) {
                        countryAr = addr.countryName
                        countryEn = addr.countryName
                    }
                }
            }
        } catch (_: Exception) {}

        return PrayerLocation(
            nameAr = cityName,
            nameEn = cityEn,
            latitude = lat,
            longitude = lng,
            countryAr = countryAr.ifBlank { "الأردن" },
            countryEn = countryEn.ifBlank { "Jordan" },
            timezoneHours = tz,
            isAutoGps = true
        )
    }
}
