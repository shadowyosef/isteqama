package com.example.service

import android.content.Context
import com.example.data.FastingTrackerRepository
import com.example.data.PrayerTimesRepository
import com.example.data.ScreenLockRepository
import com.example.data.local.entity.ScreenLockEntity
import com.example.model.LockContentConfig
import com.example.model.PrayerType
import com.example.ui.lock.ActiveLockActivity
import com.example.util.AdhanSoundPlayer
import com.example.util.LockScheduleTracker
import com.example.util.SpiritualNotificationManager
import java.util.Calendar

object PrayerSchedulerEvaluator {
    private var lastTriggeredMinute = -1
    private val triggeredEventsToday = mutableSetOf<String>()
    private var lastDayOfYear = -1
    private var silentUntilTotalMinutes = -1

    fun checkAndTrigger(context: Context, hour: Int, minute: Int) {
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        if (currentDay != lastDayOfYear) {
            lastDayOfYear = currentDay
            triggeredEventsToday.clear()
            silentUntilTotalMinutes = -1
        }
        val currentMinute = hour * 60 + minute
        if (currentMinute == lastTriggeredMinute) return
        lastTriggeredMinute = currentMinute

        if (silentUntilTotalMinutes in 0..currentMinute) {
            silentUntilTotalMinutes = -1
            AutoSilentPrayerManager.restoreNormalMode(context)
        }

        val prayers = PrayerTimesRepository.prayerTimes.value
        val iqamaOffset = PrayerTimesRepository.iqamaOffsetMinutes.value
        if (iqamaOffset > 0) {
            val iqamaMatch = prayers.find { it.type.isObligatoryPrayer && (it.hour * 60 + it.minute + iqamaOffset) == currentMinute }
            if (iqamaMatch != null && triggeredEventsToday.add("${currentDay}_iqama_${iqamaMatch.type.id}")) {
                SpiritualNotificationManager.sendReminder(context, "إقامة صلاة ${iqamaMatch.type.nameAr}", "تبقى لحظات على الإقامة • أقم الصلاة بخشوع", false)
            }
        }

        if (hour == 20 && minute == 0 && triggeredEventsToday.add("${currentDay}_fast_eve")) {
            FastingTrackerRepository.getTomorrowRecommendation()?.let { (fType, _) ->
                SpiritualNotificationManager.sendReminder(context, "تذكير صيام التطوع غداً: ${fType.labelAr}", "انْوِ الصيام وتجهز للسحور المبارك", false)
            }
        }

        val postPrayerMatch = prayers.find { it.type.isObligatoryPrayer && (it.hour * 60 + it.minute + 15) == currentMinute }
        if (postPrayerMatch != null && PrayerTimesRepository.isPrayerLockEnabled.value &&
            !LockScheduleTracker.isHourInRestWindow(hour, true, 23, 8) &&
            ScreenLockRepository.currentActiveLock.value == null &&
            triggeredEventsToday.add("${currentDay}_postlock_${postPrayerMatch.type.id}")
        ) {
            val prayerLock = ScreenLockEntity(
                id = "prayer_lock_${postPrayerMatch.type.id}",
                title = "أذكار ما بعد صلاة ${postPrayerMatch.type.nameAr}",
                lockType = "DHIKR",
                targetAmount = 7,
                contentConfigJson = LockContentConfig(selectedRoutineId = "prayer_${postPrayerMatch.type.id}").serialize()
            )
            ActiveLockActivity.launch(context, prayerLock)
        }

        checkDynamicAdhkarOffset(context, prayers, PrayerType.FAJR, "lock_morning_adhkar", currentMinute, currentDay, hour)
        checkDynamicAdhkarOffset(context, prayers, PrayerType.ASR, "lock_evening_adhkar", currentMinute, currentDay, hour)

        val match = prayers.find { it.type.isObligatoryPrayer && it.hour == hour && it.minute == minute } ?: return
        if (!triggeredEventsToday.add("${currentDay}_adhan_${match.type.id}")) return
        com.example.data.MindfulGateRepository.init(context)
        com.example.data.MindfulGateRepository.activatePrayerFreezeWindow(context)
        if (AutoSilentPrayerManager.isAutoSilentEnabled(context)) {
            AutoSilentPrayerManager.applySilentMode(context)
            silentUntilTotalMinutes = currentMinute + AutoSilentPrayerManager.getDurationMinutes(context)
        }
        if (PrayerTimesRepository.isAdhanAlertEnabled.value) {
            AdhanSoundPlayer.playPrayerAlert(context)
            SpiritualNotificationManager.sendReminder(context, "حان الآن موعد أذان ${match.type.nameAr}", "الله أكبر، حي على الصلاة • بادر بأداء الفريضة", false)
        }
    }

    private fun checkDynamicAdhkarOffset(
        context: Context,
        prayers: List<com.example.model.PrayerTimeItem>,
        basePrayer: PrayerType,
        lockId: String,
        currentMinute: Int,
        currentDay: Int,
        hour: Int
    ) {
        val lock = ScreenLockRepository.locks.value.find { it.id == lockId && it.isEnabled } ?: return
        val base = prayers.find { it.type == basePrayer } ?: return
        if (currentMinute != (base.hour * 60 + base.minute + 60)) return
        val slot = lock.getScheduleSlots().firstOrNull()
        if (slot != null && LockScheduleTracker.isHourInRestWindow(hour, slot.quietHoursEnabled, slot.quietHoursStart, slot.quietHoursEnd)) return
        if (ScreenLockRepository.currentActiveLock.value == null && triggeredEventsToday.add("${currentDay}_$lockId")) {
            ActiveLockActivity.launch(context, lock)
        }
    }
}
