package com.example.data

import com.example.model.PrayerAdjustments
import com.example.model.PrayerTimeItem
import com.example.model.PrayerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

object PrayerOnlineFetcher {
    suspend fun fetchOnline(
        lat: Double,
        lng: Double,
        adjustments: PrayerAdjustments
    ): List<PrayerTimeItem>? = withContext(Dispatchers.IO) {
        try {
            val urlStr = "https://api.aladhan.com/v1/timings?latitude=$lat&longitude=$lng&method=4"
            val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                connectTimeout = 4000
                readTimeout = 4000
                requestMethod = "GET"
            }
            if (conn.responseCode != 200) return@withContext null
            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            val json = JSONObject(reader.readText())
            reader.close()
            val timings = json.getJSONObject("data").getJSONObject("timings")

            val now = Calendar.getInstance()
            val nowTotal = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

            fun parseTime(type: PrayerType, key: String): PrayerTimeItem {
                val raw = timings.getString(key).split(" ")[0]
                val parts = raw.split(":")
                var total = (parts[0].toInt() * 60 + parts[1].toInt() + adjustments.getOffset(type)) % 1440
                if (total < 0) total += 1440
                val h = total / 60
                val m = total % 60
                return PrayerTimeItem(type, String.format("%02d:%02d", h, m), h, m, nowTotal >= total)
            }

            listOf(
                parseTime(PrayerType.FAJR, "Fajr"),
                parseTime(PrayerType.SUNRISE, "Sunrise"),
                parseTime(PrayerType.DHUHR, "Dhuhr"),
                parseTime(PrayerType.ASR, "Asr"),
                parseTime(PrayerType.MAGHRIB, "Maghrib"),
                parseTime(PrayerType.ISHA, "Isha")
            )
        } catch (_: Exception) {
            null
        }
    }
}
