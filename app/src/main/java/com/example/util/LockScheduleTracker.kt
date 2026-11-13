package com.example.util

import android.content.Context
import com.example.model.LockScheduleSlot
import java.util.Calendar

object LockScheduleTracker {
    private const val PREFS_NAME = "istaqama_lock_schedule_tracker"
    private const val KEY_COOLDOWN_UNTIL = "lock_global_cooldown_until"
    private const val KEY_LAST_COMPLETED_TIME = "last_lock_completed_time"
    private var inMemoryCooldownUntil = 0L

    fun setLockCompleted(context: Context, lockId: String, slots: List<LockScheduleSlot>) {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance().apply { timeInMillis = now }
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        inMemoryCooldownUntil = now + 90_000L
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit().putLong(KEY_COOLDOWN_UNTIL, inMemoryCooldownUntil).putLong("${KEY_LAST_COMPLETED_TIME}_$lockId", now)
        slots.forEach { slot ->
            if (slot.intervalMinutes > 0) editor.putLong("slot_last_trigger_${slot.id}", now)
            else editor.putInt("slot_completed_day_${slot.id}", dayOfYear)
        }
        editor.commit()
    }

    fun scheduleSnoozeLock(context: Context, lockId: String, lockTitle: String, lockType: String, contentJson: String, delayMinutes: Int) {
        val targetAt = System.currentTimeMillis() + delayMinutes * 60_000L
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString("snooze_id", lockId)
            .putString("snooze_title", lockTitle)
            .putString("snooze_type", lockType)
            .putString("snooze_cfg", contentJson)
            .putLong("snooze_until", targetAt)
            .commit()
    }

    fun popDueSnoozedLock(context: Context): com.example.data.local.entity.ScreenLockEntity? {
        val p = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val until = p.getLong("snooze_until", 0L)
        if (until <= 0L || System.currentTimeMillis() < until) return null
        val id = p.getString("snooze_id", null) ?: return null
        val title = p.getString("snooze_title", "") ?: ""
        val type = p.getString("snooze_type", "DHIKR") ?: "DHIKR"
        val cfg = p.getString("snooze_cfg", "") ?: ""
        p.edit().remove("snooze_until").remove("snooze_id").commit()
        return com.example.data.local.entity.ScreenLockEntity(id = id, title = title, lockType = type, contentConfigJson = cfg)
    }

    fun isSlotCompletedToday(context: Context, slotId: String, dayOfYear: Int): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt("slot_completed_day_$slotId", -1) == dayOfYear

    fun isDailyLimitReached(context: Context, key: String, maxRepeats: Int, dayOfYear: Int): Boolean {
        if (maxRepeats <= 0) return false
        return getDailyTriggerCount(context, key, dayOfYear) >= maxRepeats
    }

    fun getDailyTriggerCount(context: Context, key: String, dayOfYear: Int): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedDay = prefs.getInt("daily_day_$key", -1)
        return if (savedDay == dayOfYear) prefs.getInt("daily_cnt_$key", 0) else 0
    }

    fun incrementDailyTriggerCount(context: Context, key: String, dayOfYear: Int) {
        val next = getDailyTriggerCount(context, key, dayOfYear) + 1
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putInt("daily_day_$key", dayOfYear).putInt("daily_cnt_$key", next).commit()
    }

    fun isHourInRestWindow(hour: Int, enabled: Boolean, start: Int, end: Int): Boolean {
        if (!enabled || start == end) return false
        val h = ((hour % 24) + 24) % 24
        val s = ((start % 24) + 24) % 24
        val e = ((end % 24) + 24) % 24
        return if (s < e) h in s until e else (h >= s || h < e)
    }

    fun getLastTriggerTime(context: Context, slotId: String): Long =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong("slot_last_trigger_$slotId", 0L)

    fun getLastCompletedTime(context: Context, lockId: String): Long =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong("${KEY_LAST_COMPLETED_TIME}_$lockId", 0L)

    fun setLastTriggerTime(context: Context, slotId: String, timestamp: Long) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putLong("slot_last_trigger_$slotId", timestamp).commit()
    }

    fun isGlobalCooldownActive(context: Context? = null): Boolean {
        val now = System.currentTimeMillis()
        if (now < inMemoryCooldownUntil) return true
        if (context != null) {
            val persisted = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong(KEY_COOLDOWN_UNTIL, 0L)
            if (now < persisted) { inMemoryCooldownUntil = persisted; return true }
        }
        return false
    }

    fun setCooldown(context: Context, durationMs: Long = 90_000L) {
        val target = System.currentTimeMillis() + durationMs
        inMemoryCooldownUntil = target
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putLong(KEY_COOLDOWN_UNTIL, target).commit()
    }
}
