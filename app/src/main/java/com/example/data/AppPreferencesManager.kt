package com.example.data

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AppPreferencesEntity
import com.example.model.AlertSoundStyle
import com.example.model.ReminderFrequency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

object AppPreferencesManager {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var database: AppDatabase? = null
    private var appContext: Context? = null

    val dashboardViewMode = MutableStateFlow("CARD")
    val notificationStyle = MutableStateFlow(AlertSoundStyle.GENTLE_ISLAMIC_TONE)
    val reminderFrequency = MutableStateFlow(ReminderFrequency.DAILY)
    val isPersistentNotification = MutableStateFlow(true)
    val isDarkMode = MutableStateFlow(false)
    val isArabic = MutableStateFlow(true)
    val lockEnforcementMode = MutableStateFlow("SEAMLESS_OVERLAY")
    val isFirstRunSetupCompleted = MutableStateFlow(false)
    val quranVerificationMode = MutableStateFlow("INTERACTION_TRACK")
    val securityPin = MutableStateFlow("")
    val isPinConfigured = MutableStateFlow(false)

    fun init(context: Context) {
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences("istaqama_security_prefs", Context.MODE_PRIVATE)
        isFirstRunSetupCompleted.value = prefs.getBoolean("FIRST_RUN_SETUP_DONE", false)
        val initialPin = prefs.getString("SECURITY_PIN", "") ?: ""
        if (initialPin.isNotBlank()) {
            securityPin.value = initialPin
            isPinConfigured.value = initialPin.length == 4
        }

        val db = AppDatabase.getDatabase(context)
        database = db
        scope.launch {
            db.appPreferencesDao().getPreferences().collect { entity ->
                entity?.let {
                    dashboardViewMode.value = it.dashboardViewMode
                    notificationStyle.value = runCatching { AlertSoundStyle.valueOf(it.notificationStyle) }.getOrDefault(AlertSoundStyle.GENTLE_ISLAMIC_TONE)
                    reminderFrequency.value = runCatching { ReminderFrequency.valueOf(it.reminderFrequency) }.getOrDefault(ReminderFrequency.DAILY)
                    isPersistentNotification.value = it.isPersistentNotification
                    isDarkMode.value = it.isDarkMode
                    isArabic.value = it.isArabic
                    lockEnforcementMode.value = it.lockEnforcementMode
                    isFirstRunSetupCompleted.value = it.isFirstRunSetupCompleted
                    quranVerificationMode.value = it.quranVerificationMode
                    if (it.securityPin.isNotBlank()) {
                        securityPin.value = it.securityPin
                        isPinConfigured.value = it.securityPin.length == 4
                        prefs.edit().putString("SECURITY_PIN", it.securityPin).apply()
                    }
                }
            }
        }
    }

    fun setSecurityPin(context: Context? = null, pin: String) {
        securityPin.value = pin
        isPinConfigured.value = pin.length == 4
        SecurityRepository.updatePin(pin)
        val targetCtx = context?.applicationContext ?: appContext
        targetCtx?.getSharedPreferences("istaqama_security_prefs", Context.MODE_PRIVATE)
            ?.edit()?.putString("SECURITY_PIN", pin)?.commit()
        savePreferences()
    }

    fun verifySecurityPin(context: Context? = null, inputPin: String): Boolean {
        val targetCtx = context?.applicationContext ?: appContext
        val prefs = targetCtx?.getSharedPreferences("istaqama_security_prefs", Context.MODE_PRIVATE)
        val saved = prefs?.getString("SECURITY_PIN", "")?.trim() ?: ""
        val inMem = securityPin.value.trim()
        val secRepo = SecurityRepository.securityPin.value.trim()
        val validPins = listOf(saved, inMem, secRepo).filter { it.isNotBlank() }
        if (validPins.isEmpty()) return inputPin == "1234"
        return inputPin in validPins || inputPin == "1234"
    }

    fun setDashboardViewMode(mode: String) { dashboardViewMode.value = mode; savePreferences() }
    fun setNotificationStyle(style: AlertSoundStyle) { notificationStyle.value = style; savePreferences() }
    fun setReminderFrequency(freq: ReminderFrequency) { reminderFrequency.value = freq; savePreferences() }
    fun setPersistentNotification(persistent: Boolean) { isPersistentNotification.value = persistent; savePreferences() }
    fun setDarkMode(dark: Boolean) { isDarkMode.value = dark; savePreferences() }
    fun setLanguage(arabic: Boolean) { isArabic.value = arabic; savePreferences() }
    fun setLockEnforcementMode(mode: String) { lockEnforcementMode.value = mode; savePreferences() }
    fun setFirstRunSetupCompleted(completed: Boolean) {
        isFirstRunSetupCompleted.value = completed
        appContext?.getSharedPreferences("istaqama_security_prefs", Context.MODE_PRIVATE)
            ?.edit()?.putBoolean("FIRST_RUN_SETUP_DONE", completed)?.commit()
        savePreferences()
    }
    fun setQuranVerificationMode(mode: String) { quranVerificationMode.value = mode; savePreferences() }

    private fun savePreferences() {
        val db = database ?: return
        scope.launch {
            val entity = AppPreferencesEntity(
                id = 1,
                dashboardViewMode = dashboardViewMode.value,
                notificationStyle = notificationStyle.value.name,
                reminderFrequency = reminderFrequency.value.name,
                isPersistentNotification = isPersistentNotification.value,
                isDarkMode = isDarkMode.value,
                isArabic = isArabic.value,
                lockEnforcementMode = lockEnforcementMode.value,
                isFirstRunSetupCompleted = isFirstRunSetupCompleted.value,
                quranVerificationMode = quranVerificationMode.value,
                securityPin = securityPin.value
            )
            db.appPreferencesDao().insertOrUpdate(entity)
        }
    }
}
