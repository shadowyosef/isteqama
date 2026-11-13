package com.example.data

import android.content.Context
import com.example.model.*
import com.example.util.PrayerTimesCalculator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

object PrayerTimesRepository {
    private const val PREFS_NAME = "istaqama_prayer_prefs"
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val currentLocation = MutableStateFlow(PrayerPresetCities.defaultLocation)
    val isOnlineEnabled = MutableStateFlow(false)
    val isAdhanAlertEnabled = MutableStateFlow(true)
    val isPrayerLockEnabled = MutableStateFlow(true)
    val iqamaOffsetMinutes = MutableStateFlow(15)
    val prayerAdjustments = MutableStateFlow(PrayerAdjustments())
    val prayedPrayersToday = MutableStateFlow<Set<String>>(emptySet())
    val prayerTimes = MutableStateFlow<List<PrayerTimeItem>>(emptyList())

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isOnlineEnabled.value = prefs.getBoolean("is_online", false)
        isAdhanAlertEnabled.value = prefs.getBoolean("is_adhan_alert", true)
        isPrayerLockEnabled.value = prefs.getBoolean("is_prayer_lock", true)
        iqamaOffsetMinutes.value = prefs.getInt("iqama_offset_mins", 15)
        val lat = prefs.getFloat("lat", 32.0178f).toDouble()
        val lng = prefs.getFloat("lng", 36.0463f).toDouble()
        val cityAr = prefs.getString("city_ar", "الرصيفة") ?: "الرصيفة"
        val cityEn = prefs.getString("city_en", "Russeifa") ?: "Russeifa"
        val countryAr = prefs.getString("country_ar", "الأردن") ?: "الأردن"
        val countryEn = prefs.getString("country_en", "Jordan") ?: "Jordan"
        val tz = prefs.getFloat("tz", 3.0f).toDouble()
        currentLocation.value = PrayerLocation(cityAr, cityEn, lat, lng, countryAr, countryEn, tz)

        prayerAdjustments.value = PrayerAdjustments(
            fajrOffset = prefs.getInt("adj_fajr", 0),
            sunriseOffset = prefs.getInt("adj_sunrise", 0),
            dhuhrOffset = prefs.getInt("adj_dhuhr", 0),
            asrOffset = prefs.getInt("adj_asr", 0),
            maghribOffset = prefs.getInt("adj_maghrib", 0),
            ishaOffset = prefs.getInt("adj_isha", 0)
        )

        loadPrayedToday(context)
        refreshPrayerTimes(context)
    }

    fun refreshPrayerTimes(context: Context) {
        scope.launch {
            val loc = currentLocation.value
            val adj = prayerAdjustments.value
            var list = if (isOnlineEnabled.value) {
                PrayerOnlineFetcher.fetchOnline(loc.latitude, loc.longitude, adj)
            } else null
            if (list == null) {
                list = PrayerTimesCalculator.calculate(loc.latitude, loc.longitude, Calendar.getInstance(), adj, loc.timezoneHours)
            }
            val prayed = prayedPrayersToday.value
            prayerTimes.value = list.map { it.copy(isPrayed = prayed.contains(it.type.id)) }
        }
    }

    fun togglePrayerPrayed(context: Context, type: PrayerType, isPrayed: Boolean) {
        val current = prayedPrayersToday.value.toMutableSet()
        if (isPrayed) current.add(type.id) else current.remove(type.id)
        prayedPrayersToday.value = current
        val todayKey = "prayed_" + Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(todayKey, current).commit()
        prayerTimes.value = prayerTimes.value.map { if (it.type == type) it.copy(isPrayed = isPrayed) else it }
        try { com.example.widget.PrayerWidgetProvider.updateAllWidgets(context) } catch (_: Exception) {}
    }

    private fun loadPrayedToday(context: Context) {
        val todayKey = "prayed_" + Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(todayKey, emptySet()) ?: emptySet()
        prayedPrayersToday.value = set
    }

    fun updateAdjustments(context: Context, adjustments: PrayerAdjustments) {
        prayerAdjustments.value = adjustments
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("adj_fajr", adjustments.fajrOffset)
            .putInt("adj_sunrise", adjustments.sunriseOffset)
            .putInt("adj_dhuhr", adjustments.dhuhrOffset)
            .putInt("adj_asr", adjustments.asrOffset)
            .putInt("adj_maghrib", adjustments.maghribOffset)
            .putInt("adj_isha", adjustments.ishaOffset)
            .commit()
        emitCalculatedNow()
        refreshPrayerTimes(context)
    }

    fun setLocation(context: Context, loc: PrayerLocation) {
        currentLocation.value = loc
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString("city_ar", loc.nameAr)
            .putString("city_en", loc.nameEn)
            .putString("country_ar", loc.countryAr)
            .putString("country_en", loc.countryEn)
            .putFloat("lat", loc.latitude.toFloat())
            .putFloat("lng", loc.longitude.toFloat())
            .putFloat("tz", loc.timezoneHours.toFloat())
            .commit()
        emitCalculatedNow()
        refreshPrayerTimes(context)
    }

    private fun emitCalculatedNow() {
        val loc = currentLocation.value
        val adj = prayerAdjustments.value
        val prayed = prayedPrayersToday.value
        val calc = PrayerTimesCalculator.calculate(loc.latitude, loc.longitude, Calendar.getInstance(), adj, loc.timezoneHours)
        prayerTimes.value = calc.map { it.copy(isPrayed = prayed.contains(it.type.id)) }
    }

    fun toggleOnlineMode(context: Context, enabled: Boolean) {
        isOnlineEnabled.value = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean("is_online", enabled).commit()
        refreshPrayerTimes(context)
    }

    fun toggleAdhanAlert(context: Context, enabled: Boolean) {
        isAdhanAlertEnabled.value = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean("is_adhan_alert", enabled).commit()
    }

    fun togglePrayerLock(context: Context, enabled: Boolean) {
        isPrayerLockEnabled.value = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean("is_prayer_lock", enabled).commit()
    }

    fun setIqamaOffset(context: Context, mins: Int) {
        iqamaOffsetMinutes.value = mins
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putInt("iqama_offset_mins", mins).commit()
    }
}
