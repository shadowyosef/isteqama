package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object NearbyMosquesFinder {
    fun openNearbyMosquesInMaps(context: Context, latitude: Double = 32.02, longitude: Double = 36.04) {
        try {
            // Search for mosques near location or current GPS
            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=مسجد|mosque")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(mapIntent)
        } catch (_: Exception) {
            // Fallback to browser search
            try {
                val browserUri = Uri.parse("https://www.google.com/maps/search/mosques/@$latitude,$longitude,14z")
                context.startActivity(Intent(Intent.ACTION_VIEW, browserUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (_: Exception) {}
        }
    }
}
