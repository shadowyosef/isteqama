package com.example.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.data.FocusProfileMode
import com.example.data.FocusProfilesRepository
import com.example.data.LockRepository
import com.example.model.AppRule
import com.example.model.BlockerStyle

class AppUsageMonitor(private val context: Context) {
    private val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager

    fun getForegroundAppPackage(): String? {
        val usm = usageStatsManager ?: return null
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 4000
        val usageEvents = usm.queryEvents(beginTime, endTime)
        val event = UsageEvents.Event()
        var lastForegroundPackage: String? = null
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                lastForegroundPackage = event.packageName
            }
        }
        return lastForegroundPackage
    }

    fun isRestrictedApp(packageName: String): Boolean {
        if (packageName == context.packageName) return false
        FocusProfilesRepository.init(context)
        com.example.data.MindfulGateRepository.init(context)
        if (FocusProfilesRepository.isTemporaryBypassActive(packageName)) return false

        val rule = LockRepository.appRules.value.find { it.packageName == packageName }
        if (rule != null && rule.blockerStyle != BlockerStyle.ALLOWED) {
            if (com.example.data.MindfulGateRepository.isPrayerFreezeActiveNow(context)) return true
            if (com.example.data.MindfulGateRepository.isBedtimeWindowNow()) return true
        }
        val overDailyCap = FocusProfilesRepository.recordForegroundSeconds(context, packageName, 1)
        if (overDailyCap) return true

        val activeFocus = FocusProfilesRepository.effectiveProfile()
        if (activeFocus == FocusProfileMode.STUDY_WORK || activeFocus == FocusProfileMode.QIYAM_SECLUSION) {
            if (rule != null && rule.blockerStyle != BlockerStyle.ALLOWED) return true
        }
        return rule != null && rule.isBlocked && rule.blockerStyle != BlockerStyle.ALLOWED
    }

    fun getAppRule(packageName: String): AppRule? {
        return LockRepository.appRules.value.find { it.packageName == packageName }
    }
}
