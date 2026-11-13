package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.*

object AdhanSoundPlayer {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun playPrayerAlert(context: Context) {
        scope.launch {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
                repeat(4) {
                    toneGen.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 400)
                    delay(550)
                    toneGen.startTone(ToneGenerator.TONE_CDMA_MED_L, 400)
                    delay(700)
                }
                toneGen.release()
            } catch (_: Exception) {}
        }
        performAdhanVibration(context)
    }

    private fun performAdhanVibration(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } ?: return

            val pattern = longArrayOf(0, 500, 300, 500, 300, 700)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        } catch (_: Exception) {}
    }
}
