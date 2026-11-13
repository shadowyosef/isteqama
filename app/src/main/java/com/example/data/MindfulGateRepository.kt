package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

object MindfulGateRepository {
    private const val PREFS = "mindful_digital_wellbeing_prefs"
    private const val KEY_MINDFUL_GATE = "mindful_gate_enabled"
    private const val KEY_PRAYER_FREEZE = "prayer_freeze_enabled"
    private const val KEY_FREEZE_MINS = "prayer_freeze_mins"
    private const val KEY_BEDTIME_ENABLED = "bedtime_lock_enabled"
    private const val KEY_BEDTIME_START = "bedtime_start_hour"
    private const val KEY_BEDTIME_END = "bedtime_end_hour"
    private const val KEY_SAVED_MINS = "saved_scroll_mins_total"
    private const val KEY_FREEZE_UNTIL = "prayer_freeze_until_ms"
    private const val KEY_HADITH_LOCK = "hadith_lock_enabled"

    private val _mindfulGateEnabled = MutableStateFlow(true)
    val mindfulGateEnabled = _mindfulGateEnabled.asStateFlow()

    private val _prayerFreezeEnabled = MutableStateFlow(true)
    val prayerFreezeEnabled = _prayerFreezeEnabled.asStateFlow()

    private val _prayerFreezeMins = MutableStateFlow(20)
    val prayerFreezeMins = _prayerFreezeMins.asStateFlow()

    private val _bedtimeEnabled = MutableStateFlow(false)
    val bedtimeEnabled = _bedtimeEnabled.asStateFlow()

    private val _bedtimeStartHour = MutableStateFlow(23)
    val bedtimeStartHour = _bedtimeStartHour.asStateFlow()

    private val _bedtimeEndHour = MutableStateFlow(5)
    val bedtimeEndHour = _bedtimeEndHour.asStateFlow()

    private val _savedMinutes = MutableStateFlow(0)
    val savedMinutes = _savedMinutes.asStateFlow()

    private val _hadithLockEnabled = MutableStateFlow(false)
    val hadithLockEnabled = _hadithLockEnabled.asStateFlow()

    fun init(context: Context) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        _mindfulGateEnabled.value = p.getBoolean(KEY_MINDFUL_GATE, true)
        _prayerFreezeEnabled.value = p.getBoolean(KEY_PRAYER_FREEZE, true)
        _prayerFreezeMins.value = p.getInt(KEY_FREEZE_MINS, 20)
        _bedtimeEnabled.value = p.getBoolean(KEY_BEDTIME_ENABLED, false)
        _bedtimeStartHour.value = p.getInt(KEY_BEDTIME_START, 23)
        _bedtimeEndHour.value = p.getInt(KEY_BEDTIME_END, 5)
        _savedMinutes.value = p.getInt(KEY_SAVED_MINS, 0)
        _hadithLockEnabled.value = p.getBoolean(KEY_HADITH_LOCK, false)
    }

    fun setMindfulGate(ctx: Context, enabled: Boolean) {
        _mindfulGateEnabled.value = enabled
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_MINDFUL_GATE, enabled).apply()
    }

    fun setPrayerFreeze(ctx: Context, enabled: Boolean, mins: Int = _prayerFreezeMins.value) {
        val clamped = mins.coerceIn(5, 60)
        _prayerFreezeEnabled.value = enabled
        _prayerFreezeMins.value = clamped
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_PRAYER_FREEZE, enabled).putInt(KEY_FREEZE_MINS, clamped).apply()
    }

    fun activatePrayerFreezeWindow(ctx: Context) {
        if (!_prayerFreezeEnabled.value) return
        val until = System.currentTimeMillis() + _prayerFreezeMins.value * 60_000L
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putLong(KEY_FREEZE_UNTIL, until).apply()
    }

    fun isPrayerFreezeActiveNow(ctx: Context): Boolean {
        if (!_prayerFreezeEnabled.value) return false
        return System.currentTimeMillis() < ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(KEY_FREEZE_UNTIL, 0L)
    }

    fun setBedtimeConfig(ctx: Context, enabled: Boolean, startHour: Int = _bedtimeStartHour.value, endHour: Int = _bedtimeEndHour.value) {
        _bedtimeEnabled.value = enabled
        _bedtimeStartHour.value = startHour.coerceIn(0, 23)
        _bedtimeEndHour.value = endHour.coerceIn(0, 23)
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_BEDTIME_ENABLED, enabled)
            .putInt(KEY_BEDTIME_START, _bedtimeStartHour.value)
            .putInt(KEY_BEDTIME_END, _bedtimeEndHour.value).apply()
    }

    fun isBedtimeWindowNow(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): Boolean {
        if (!_bedtimeEnabled.value) return false
        return com.example.util.LockScheduleTracker.isHourInRestWindow(hour, true, _bedtimeStartHour.value, _bedtimeEndHour.value)
    }

    fun setHadithLockEnabled(ctx: Context, enabled: Boolean) {
        _hadithLockEnabled.value = enabled
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_HADITH_LOCK, enabled).apply()
    }

    fun recordSavedScrollMinutes(ctx: Context, deltaMins: Int) {
        val next = (_savedMinutes.value + deltaMins.coerceAtLeast(1)).coerceAtLeast(0)
        _savedMinutes.value = next
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY_SAVED_MINS, next).apply()
    }
}
