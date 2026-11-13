package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.data.LockRepository
import com.example.ui.lock.ActiveLockActivity
import com.example.ui.lock.SystemOverlayWindowManager
import kotlinx.coroutines.*
import java.util.Calendar

class LockOverlayService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var usageMonitor: AppUsageMonitor
    private var lastTriggeredMinute = -1
    private var monitorJob: Job? = null

    companion object {
        fun startService(context: Context) {
            val intent = Intent(context, LockOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, LockOverlayService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        usageMonitor = AppUsageMonitor(this)
        com.example.util.SpiritualNotificationManager.init(this)
        com.example.data.PrayerTimesRepository.init(this)
        LockNotificationHelper.createNotificationChannel(this)
        startForeground(LockNotificationHelper.NOTIFICATION_ID, LockNotificationHelper.buildNotification(this))
        if (com.example.util.LockPersistenceManager.isLockActive(this)) {
            com.example.util.LockPersistenceManager.enforceLockReopen(this)
        }
        startMonitoringLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (com.example.util.LockPersistenceManager.isLockActive(this)) {
            com.example.util.LockPersistenceManager.enforceLockReopen(this)
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (com.example.util.LockPersistenceManager.isLockActive(this)) {
            com.example.util.LockPersistenceManager.scheduleWatchdog(applicationContext, 200L)
            com.example.util.LockPersistenceManager.enforceLockReopen(applicationContext)
        }
    }

    private fun startMonitoringLoop() {
        monitorJob?.cancel()
        monitorJob = serviceScope.launch {
            while (isActive) {
                try {
                    ensureLockForegroundEnforcement()
                    checkForegroundAppUsage()
                    TasbeehAlertScheduler.checkAndTrigger(this@LockOverlayService)
                    checkScheduledRoutines()
                    val c = Calendar.getInstance()
                    PrayerSchedulerEvaluator.checkAndTrigger(this@LockOverlayService, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
                } catch (_: Exception) {}
                delay(500)
            }
        }
    }

    private fun ensureLockForegroundEnforcement() {
        if (com.example.util.LockScheduleTracker.isGlobalCooldownActive(this)) return
        if (com.example.util.LockPersistenceManager.isLockActive(this) || com.example.data.ScreenLockRepository.currentActiveLock.value != null) {
            val pkg = usageMonitor.getForegroundAppPackage()
            if (pkg != null && pkg != packageName) {
                ActiveLockActivity.launch(this)
            }
        }
    }

    private fun checkForegroundAppUsage() {
        if (com.example.util.AppForegroundTracker.isAppInForeground.value) return
        if (com.example.util.LockScheduleTracker.isGlobalCooldownActive(this)) return
        if (com.example.util.LockPersistenceManager.isLockActive(this) || LockRepository.isLockActive.value || com.example.data.ScreenLockRepository.currentActiveLock.value != null) return
        val currentPackage = usageMonitor.getForegroundAppPackage() ?: return
        if (usageMonitor.isRestrictedApp(currentPackage)) {
            val anyLockEnabled = com.example.data.ScreenLockRepository.locks.value.any { it.isEnabled }
            val anyScheduleEnabled = LockRepository.schedules.value.any { it.isEnabled }
            if (!anyLockEnabled && !anyScheduleEnabled) return
            val rule = usageMonitor.getAppRule(currentPackage)
            val appName = rule?.appName ?: currentPackage
            val legacySched = LockRepository.schedules.value.firstOrNull { it.isEnabled }?.copy(title = "قفل تطبيق $appName")
            if (com.example.data.SmartAlertPreferences.smartAlertMode.value == com.example.model.SmartAlertMode.PERSISTENT_LOCK) {
                if (com.example.data.MindfulGateRepository.mindfulGateEnabled.value && !com.example.data.MindfulGateRepository.isPrayerFreezeActiveNow(this)) {
                    val gateLock = com.example.data.local.entity.ScreenLockEntity(id = "mindful_gate_$currentPackage", title = appName, lockType = "MINDFUL_GATE")
                    ActiveLockActivity.launch(this, gateLock)
                } else if (legacySched != null) SystemOverlayWindowManager.launchOverlay(this, legacySched)
                else {
                    val entity = if (com.example.util.SmartAutoLockHelper.isEnabled(this))
                        com.example.util.SmartAutoLockHelper.buildSmartLockForHour().copy(title = "قفل $appName")
                    else com.example.data.ScreenLockRepository.locks.value.firstOrNull { it.isEnabled } ?: return
                    ActiveLockActivity.launch(this, entity)
                }
            } else SmartAlertWindowManager.showSmartAlert(this, appName)
        }
    }

    private fun checkScheduledRoutines() {
        if (com.example.util.LockScheduleTracker.isGlobalCooldownActive(this)) return
        if (LockRepository.isLockActive.value || com.example.data.ScreenLockRepository.currentActiveLock.value != null) return
        val cal = Calendar.getInstance()
        val currentMinute = cal.get(Calendar.MINUTE)
        val isMinuteChanged = (currentMinute != lastTriggeredMinute)
        if (isMinuteChanged) {
            lastTriggeredMinute = currentMinute
            LockNotificationHelper.updateNotification(this)
        }
        ScheduledTriggerEvaluator.evaluate(
            this,
            cal.get(Calendar.HOUR_OF_DAY),
            currentMinute,
            cal.get(Calendar.DAY_OF_WEEK),
            isMinuteChanged
        )
    }

    override fun onDestroy() {
        monitorJob?.cancel()
        SmartAlertWindowManager.dismiss()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
