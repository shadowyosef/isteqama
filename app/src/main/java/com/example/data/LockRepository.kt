package com.example.data

import android.content.Context
import com.example.model.AppRule
import com.example.model.BlockerStyle
import com.example.model.LockSchedule
import com.example.model.LockType
import kotlinx.coroutines.flow.MutableStateFlow

object LockRepository {
    private var appContext: Context? = null
    val defaultBlockerStyle = MutableStateFlow(BlockerStyle.OVERLAY)
    val isLockActive = MutableStateFlow(false)
    val currentActiveSchedule = MutableStateFlow<LockSchedule?>(null)

    val schedules = MutableStateFlow(
        listOf(
            LockSchedule(
                id = "s_fajr",
                title = "ورد الفجر القرآني",
                lockType = LockType.QURAN_VERSES,
                blockerStyle = BlockerStyle.OVERLAY,
                targetAmount = 7,
                targetSurah = 1,
                startAyah = 1,
                endAyah = 7,
                triggerHour = 5,
                triggerMinute = 30
            ),
            LockSchedule(
                id = "s_evening",
                title = "أذكار المساء وحفظ الوقت",
                lockType = LockType.ADHKAR_ROUTINE,
                blockerStyle = BlockerStyle.STRICT,
                targetAmount = 4,
                triggerHour = 17,
                triggerMinute = 0
            ),
            LockSchedule(
                id = "s_night_tasbeeh",
                title = "تسبيح واستغفار الليل",
                lockType = LockType.TASBEEH_COUNT,
                blockerStyle = BlockerStyle.GENTLE,
                targetAmount = 100,
                triggerHour = 22,
                triggerMinute = 0
            )
        )
    )

    private val defaultFallbackApps = listOf(
        AppRule("com.whatsapp", "WhatsApp", BlockerStyle.ALLOWED, false),
        AppRule("com.instagram.android", "Instagram", BlockerStyle.ALLOWED, false),
        AppRule("com.google.android.youtube", "YouTube", BlockerStyle.ALLOWED, false),
        AppRule("com.facebook.katana", "Facebook", BlockerStyle.ALLOWED, false),
        AppRule("com.twitter.android", "X (Twitter)", BlockerStyle.ALLOWED, false),
        AppRule("com.tiktok.android", "TikTok", BlockerStyle.ALLOWED, false),
        AppRule("com.google.android.dialer", "الهاتف (Phone)", BlockerStyle.ALLOWED, false),
        AppRule("com.android.settings", "الإعدادات (Settings)", BlockerStyle.ALLOWED, false)
    )

    val appRules = MutableStateFlow(defaultFallbackApps)

    fun refreshInstalledApps(context: Context) {
        appContext = context.applicationContext
        appRules.value = InstalledAppsHelper.loadAppRules(context, defaultFallbackApps)
    }

    fun toggleSchedule(id: String) {
        schedules.value = schedules.value.map {
            if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    fun addSchedule(schedule: LockSchedule) {
        schedules.value = schedules.value + schedule
    }

    fun removeSchedule(id: String) {
        schedules.value = schedules.value.filter { it.id != id }
    }

    fun updateAppRule(packageName: String, style: BlockerStyle, isBlocked: Boolean, context: Context? = null) {
        val ctx = context?.applicationContext ?: appContext
        ctx?.let { InstalledAppsHelper.saveRule(it, packageName, style) }
        appRules.value = appRules.value.map {
            if (it.packageName == packageName) it.copy(blockerStyle = style, isBlocked = isBlocked)
            else it
        }
    }

    fun triggerLock(schedule: LockSchedule) {
        currentActiveSchedule.value = schedule
        isLockActive.value = true
    }

    fun completeLock() {
        isLockActive.value = false
        currentActiveSchedule.value = null
        StatsRepository.recordLockCompleted()
    }

    fun emergencyBypass() {
        isLockActive.value = false
        currentActiveSchedule.value = null
        StatsRepository.recordEmergencyBypass()
    }
}
