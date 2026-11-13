package com.example.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.*
import android.widget.TextView
import com.example.data.TasbeehRepository
import com.example.model.*
import com.example.util.AppStrings
import com.example.util.FloatingToastPositionManager
import com.example.util.RequestPermissionsManager
import com.example.util.SpiritualNotificationManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

object FloatingTasbeehToastManager {
    private var windowManager: WindowManager? = null
    private var currentFloatingView: View? = null
    private var autoDismissJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _activeInAppToast = MutableStateFlow<TasbeehScheduleModel?>(null)
    val activeInAppToast = _activeInAppToast.asStateFlow()
    private var sessionTapCount = 0

    fun triggerTasbeehAlert(context: Context, model: TasbeehScheduleModel) {
        sessionTapCount = 0
        val active = model.getActivePhrase()
        val target = model.copy(phraseAr = active.phraseAr, phraseEn = active.phraseEn, targetCount = active.targetCount)
        com.example.data.TasbeehScheduleRepository.recordTriggered(model.id)
        com.example.util.WhisperTtsHelper.speakSoftly(
            context,
            if (AppStrings.isArabic) target.phraseAr else target.phraseEn,
            forceForItem = target.whisperVoiceEnabled
        )

        if (target.deliveryStyle == TasbeehDeliveryStyle.NOTIFICATION || target.deliveryStyle == TasbeehDeliveryStyle.BOTH) {
            SpiritualNotificationManager.sendTasbeehReminder(context, target)
        }
        if (target.deliveryStyle == TasbeehDeliveryStyle.TOAST_POPUP || target.deliveryStyle == TasbeehDeliveryStyle.BOTH) {
            showInAppToast(target)
            scope.launch(Dispatchers.Main) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                    RequestPermissionsManager.requestOverlayPermission(context)
                } else {
                    showFloatingToast(context, target)
                }
            }
        }
    }

    fun showInAppToast(model: TasbeehScheduleModel) {
        _activeInAppToast.value = model
        scheduleAutoDismiss(model.durationSeconds) { _activeInAppToast.value = null }
    }

    fun dismissInAppToast() {
        autoDismissJob?.cancel()
        _activeInAppToast.value = null
    }

    private fun showFloatingToast(context: Context, model: TasbeehScheduleModel) {
        dismissFloatingToast()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager = wm
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        FloatingToastPositionManager.applyToLayoutParams(context, params)

        val subText = TextView(context).apply {
            val stroke = FloatingToastViewHelper.getStrokeColor(model.themeColor)
            text = if (model.countOnClickWithDismiss) "$sessionTapCount/${model.targetCount} • ${if (AppStrings.isArabic) "اضغط للتسبيح" else "Tap"}"
            else "${model.targetCount}x • ${if (AppStrings.isArabic) "انقر للإخفاء" else "Dismiss"}"
            setTextColor(stroke); textSize = 11f; gravity = Gravity.CENTER; setPadding(0, 4, 0, 0)
        }

        val toastView = FloatingToastViewHelper.createToastView(context, model, subText)
        attachDragListener(context, wm, toastView, params, model, subText)

        try {
            wm.addView(toastView, params)
            currentFloatingView = toastView
            scheduleAutoDismiss(model.durationSeconds) { dismissFloatingToast() }
        } catch (_: Exception) { showInAppToast(model) }
    }

    private fun attachDragListener(context: Context, wm: WindowManager, view: View, params: WindowManager.LayoutParams, model: TasbeehScheduleModel, subText: TextView) {
        var initialX = 0; var initialY = 0; var touchX = 0f; var touchY = 0f; var moved = false
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x; initialY = params.y; touchX = event.rawX; touchY = event.rawY
                    moved = false; true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - touchX).toInt(); val dy = (event.rawY - touchY).toInt()
                    if (abs(dx) > 12 || abs(dy) > 12) {
                        moved = true
                        params.gravity = Gravity.TOP or Gravity.START
                        params.x = initialX + dx; params.y = initialY + dy
                        try { wm.updateViewLayout(view, params) } catch (_: Exception) {}
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (moved) {
                        FloatingToastPositionManager.saveLastDraggedPosition(context, params.x, params.y)
                    } else {
                        handleTap(context, model, subText) { dismissFloatingToast() }
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun handleTap(context: Context, model: TasbeehScheduleModel, subText: TextView, onDismiss: () -> Unit) {
        if (model.countOnClickWithDismiss) {
            sessionTapCount++
            val isGoal = sessionTapCount >= model.targetCount
            com.example.util.TasbeehHapticsHelper.performCountFeedback(context, isGoal)
            TasbeehRepository.increment()
            if (isGoal) onDismiss()
            else {
                subText.text = "$sessionTapCount/${model.targetCount} • ${if (AppStrings.isArabic) "اضغط للتسبيح" else "Tap"}"
                scheduleAutoDismiss(model.durationSeconds) { onDismiss() }
            }
        } else onDismiss()
    }

    private fun scheduleAutoDismiss(seconds: Int, onDismiss: () -> Unit) {
        autoDismissJob?.cancel()
        autoDismissJob = scope.launch { delay(seconds * 1000L); onDismiss() }
    }

    fun dismissFloatingToast() {
        autoDismissJob?.cancel()
        currentFloatingView?.let { try { windowManager?.removeView(it) } catch (_: Exception) {} }
        currentFloatingView = null
    }
}
