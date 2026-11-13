package com.example.util

import android.content.Context
import com.example.data.local.entity.ScreenLockEntity
import com.example.model.LockContentConfig
import java.util.Calendar

object SmartAutoLockHelper {
    private const val PREFS = "smart_gradient_lock_prefs"
    private const val KEY_ENABLED = "smart_gradient_enabled"

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun getPhaseLabel(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY), isAr: Boolean = AppStrings.isArabic): String {
        return when (hour) {
            in 4..10 -> if (isAr) "الفترة الصباحية: أذكار الصباح" else "Morning: Morning Adhkar"
            in 11..15 -> if (isAr) "فترة الظهيرة: تسبيح واستغفار" else "Noon: Tasbeeh & Istighfar"
            in 16..20 -> if (isAr) "فترة المساء: أذكار المساء وحفظ" else "Evening: Evening Adhkar & Hifz"
            else -> if (isAr) "فترة الليل: سورة الملك وأذكار النوم" else "Night: Surah Al-Mulk & Sleep"
        }
    }

    fun buildSmartLockForHour(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): ScreenLockEntity {
        return when (hour) {
            in 4..10 -> ScreenLockEntity(
                id = "smart_morning_lock",
                title = "الورد الذكي • أذكار الصباح",
                lockType = "DHIKR",
                targetAmount = 5,
                contentConfigJson = LockContentConfig(selectedRoutineId = "r_morning_adhkar").serialize()
            )
            in 11..15 -> ScreenLockEntity(
                id = "smart_noon_lock",
                title = "الورد الذكي • تسبيح واستغفار الظهيرة",
                lockType = "TASBEEH",
                targetAmount = 15,
                contentConfigJson = LockContentConfig(
                    selectedDhikrIds = listOf("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", "أَسْتَغْفِرُ اللَّهَ")
                ).serialize()
            )
            in 16..20 -> ScreenLockEntity(
                id = "smart_evening_lock",
                title = "الورد الذكي • أذكار المساء",
                lockType = "DHIKR",
                targetAmount = 5,
                contentConfigJson = LockContentConfig(selectedRoutineId = "r_evening_adhkar").serialize()
            )
            else -> ScreenLockEntity(
                id = "smart_night_lock",
                title = "الورد الذكي • سورة الملك قبل النوم",
                lockType = "QURAN",
                targetAmount = 5,
                contentConfigJson = LockContentConfig(
                    selectedSurahIds = listOf(67),
                    startAyah = 1,
                    endAyah = 30,
                    ayahsPerSession = 5
                ).serialize()
            )
        }
    }
}
