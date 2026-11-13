package com.example.ui.lock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.data.LockRepository
import com.example.data.ScreenLockRepository
import com.example.data.local.entity.ScreenLockEntity
import com.example.ui.theme.MyApplicationTheme
import com.example.util.LockPersistenceManager
import com.example.util.LockScheduleTracker

class ActiveLockActivity : ComponentActivity() {

    private var isCompletedAndDismissed = false

    companion object {
        fun launch(context: Context, lock: ScreenLockEntity? = null) {
            val isPreview = lock?.id?.startsWith("preview_") == true
            if (!isPreview && LockScheduleTracker.isGlobalCooldownActive(context)) return
            if (lock != null) {
                ScreenLockRepository.triggerLock(lock, context)
            }
            val intent = Intent(context, ActiveLockActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(intent)
        }

        fun launchMultiple(context: Context, locks: List<ScreenLockEntity>) {
            if (LockScheduleTracker.isGlobalCooldownActive(context)) return
            if (locks.isNotEmpty()) {
                ScreenLockRepository.triggerLocks(locks, context)
                val intent = Intent(context, ActiveLockActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.data.AppPreferencesManager.init(this)
        com.example.data.AppAppearancePreferences.init(this)
        ActiveLockIntentResolver.resolve(this, intent)
        if (ScreenLockRepository.currentActiveLock.value == null && LockPersistenceManager.isLockActive(this)) {
            LockPersistenceManager.restoreActiveLock(this)?.let { ScreenLockRepository.triggerLock(it, this) }
        }
        LockKioskHelper.setupLockWindow(this)
        LockKioskHelper.startKioskLock(this)
        com.example.util.LockSoundManager.playLockAlert(this)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        setContent {
            val activeLock by ScreenLockRepository.currentActiveLock.collectAsState()
            val queue by ScreenLockRepository.activeLocksQueue.collectAsState()

            if (activeLock != null) {
                MyApplicationTheme {
                    ActiveLockContent(
                        lock = activeLock!!,
                        queueSize = queue.size,
                        onComplete = {
                            val finishedLock = activeLock
                            ScreenLockRepository.dismissLock(this@ActiveLockActivity)
                            if (finishedLock != null && !finishedLock.id.startsWith("preview_")) {
                                LockScheduleTracker.setLockCompleted(
                                    this@ActiveLockActivity,
                                    finishedLock.id,
                                    finishedLock.getScheduleSlots()
                                )
                            }
                            if (ScreenLockRepository.currentActiveLock.value == null) {
                                isCompletedAndDismissed = true
                                LockKioskHelper.stopKioskLock(this@ActiveLockActivity)
                                LockPersistenceManager.clearActiveLock(this@ActiveLockActivity)
                                LockRepository.completeLock()
                                finish()
                            }
                        }
                    )
                }
            } else {
                LaunchedEffect(Unit) {
                    isCompletedAndDismissed = true
                    LockKioskHelper.stopKioskLock(this@ActiveLockActivity)
                    LockPersistenceManager.clearActiveLock(this@ActiveLockActivity)
                    LockRepository.completeLock()
                    finish()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        ActiveLockIntentResolver.resolve(this, intent)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (!isFinishing && !isCompletedAndDismissed) reopenLockActivity()
    }

    override fun onPause() {
        super.onPause()
        if (!isFinishing && !isCompletedAndDismissed) reopenLockActivity()
    }

    private fun reopenLockActivity() {
        if (isFinishing || isCompletedAndDismissed || LockScheduleTracker.isGlobalCooldownActive(this)) return
        if (LockPersistenceManager.isLockActive(this) || ScreenLockRepository.currentActiveLock.value != null) {
            val reopenIntent = Intent(this, ActiveLockActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(reopenIntent)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            LockKioskHelper.hideSystemBars(this)
        } else if (!isFinishing && !isCompletedAndDismissed) {
            reopenLockActivity()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        com.example.util.LockSoundManager.stopSound()
    }
}
