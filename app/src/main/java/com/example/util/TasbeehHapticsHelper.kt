package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object TasbeehHapticsHelper {

    fun performCountFeedback(context: Context, isTargetReached: Boolean = false) {
        val vibrator = getVibrator(context) ?: return
        if (isTargetReached) {
            performGoalReachedPattern(vibrator)
        } else {
            performSubtleClick(vibrator)
        }
    }

    private fun performSubtleClick(vibrator: Vibrator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                return
            } catch (_: Exception) {}
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(25, 120))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(25)
        }
    }

    private fun performGoalReachedPattern(vibrator: Vibrator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Distinct celebratory rhythmic pattern: Pulse 100ms, pause 80ms, Pulse 220ms
            val timings = longArrayOf(0, 100, 80, 220)
            val amplitudes = intArrayOf(0, 200, 0, 255)
            try {
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } catch (_: Exception) {
                vibrator.vibrate(VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 100, 80, 220), -1)
        }
    }

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
