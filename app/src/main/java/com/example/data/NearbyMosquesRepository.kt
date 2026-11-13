package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.math.*

data class NearbyMosque(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Int
)

object NearbyMosquesRepository {
    suspend fun fetchNearbyMosques(lat: Double, lng: Double): Result<List<NearbyMosque>> = withContext(Dispatchers.IO) {
        try {
            val query = "[out:json][timeout:12];(node[\"amenity\"=\"place_of_worship\"][\"religion\"=\"muslim\"](around:4500,$lat,$lng);way[\"amenity\"=\"place_of_worship\"][\"religion\"=\"muslim\"](around:4500,$lat,$lng););out center 20;"
            val encoded = URLEncoder.encode(query, "UTF-8")
            val conn = (URL("https://overpass-api.de/api/interpreter?data=$encoded").openConnection() as HttpURLConnection).apply {
                connectTimeout = 9000
                readTimeout = 9000
                setRequestProperty("User-Agent", "IstaqamaAndroidApp/1.0")
            }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val arr = JSONObject(body).optJSONArray("elements") ?: return@withContext Result.success(emptyList())
            val list = mutableListOf<NearbyMosque>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val tags = obj.optJSONObject("tags")
                val name = tags?.optString("name")?.takeIf { it.isNotBlank() } ?: tags?.optString("name:ar") ?: "مسجد قريب"
                val mLat = if (obj.has("lat")) obj.getDouble("lat") else obj.optJSONObject("center")?.optDouble("lat") ?: continue
                val mLng = if (obj.has("lon")) obj.getDouble("lon") else obj.optJSONObject("center")?.optDouble("lon") ?: continue
                list.add(NearbyMosque(name, mLat, mLng, haversineMeters(lat, lng, mLat, mLng)))
            }
            Result.success(list.sortedBy { it.distanceMeters })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        return (2 * r * asin(sqrt(a))).roundToInt()
    }
}
