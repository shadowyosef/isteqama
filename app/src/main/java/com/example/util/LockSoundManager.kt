package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.net.Uri
import kotlinx.coroutines.*

object LockSoundManager {
    private const val PREFS_NAME = "istaqama_lock_sound_prefs"
    const val SOUND_ADHAN = "ADHAN_FULL"
    const val SOUND_TAKBEER = "TAKBEERAT"
    const val SOUND_CHIME = "SPIRITUAL_CHIME"
    const val SOUND_RINGTONE = "SYSTEM_RINGTONE"
    const val SOUND_CUSTOM = "CUSTOM_PHONE"
    const val SOUND_MUTE = "MUTE"

    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun isSoundEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean("sound_enabled", true)
    }

    fun setSoundEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean("sound_enabled", enabled).apply()
    }

    fun getSelectedSound(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString("sound_type", SOUND_ADHAN) ?: SOUND_ADHAN
    }

    fun setSelectedSound(context: Context, soundType: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString("sound_type", soundType).apply()
    }

    fun getCustomSoundUri(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString("custom_uri", null)
    }

    fun setCustomSoundUri(context: Context, uri: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString("custom_uri", uri).apply()
    }

    fun playLockAlert(context: Context) {
        if (!isSoundEnabled(context)) return
        val soundType = getSelectedSound(context)
        if (soundType == SOUND_MUTE) return

        stopSound()
        try {
            if (soundType == SOUND_CUSTOM) {
                val uriStr = getCustomSoundUri(context)
                if (!uriStr.isNullOrBlank()) {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(context, Uri.parse(uriStr))
                        prepare()
                        start()
                    }
                    return
                }
            }
            if (soundType == SOUND_RINGTONE) {
                val defUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                RingtoneManager.getRingtone(context, defUri)?.play()
                return
            }

            // Built-in offline audio generation for Adhan and Takbeerat
            scope.launch {
                try {
                    val tone = ToneGenerator(AudioManager.STREAM_ALARM, 100)
                    if (soundType == SOUND_TAKBEER) {
                        repeat(3) {
                            tone.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 450)
                            delay(600)
                            tone.startTone(ToneGenerator.TONE_CDMA_MED_L, 450)
                            delay(700)
                        }
                    } else if (soundType == SOUND_CHIME) {
                        tone.startTone(ToneGenerator.TONE_PROP_BEEP, 300)
                        delay(400)
                        tone.startTone(ToneGenerator.TONE_PROP_ACK, 500)
                    } else { // SOUND_ADHAN
                        // Melodic Adhan call simulation
                        val notes = intArrayOf(
                            ToneGenerator.TONE_CDMA_HIGH_L,
                            ToneGenerator.TONE_CDMA_MED_L,
                            ToneGenerator.TONE_CDMA_HIGH_L,
                            ToneGenerator.TONE_CDMA_LOW_L
                        )
                        repeat(2) {
                            for (n in notes) {
                                tone.startTone(n, 500)
                                delay(650)
                            }
                            delay(400)
                        }
                    }
                    delay(500)
                    tone.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    fun playUnlockChime(context: Context) {
        if (!isSoundEnabled(context) || getSelectedSound(context) == SOUND_MUTE) return
        scope.launch {
            try {
                val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
                tone.startTone(ToneGenerator.TONE_PROP_ACK, 220)
                delay(240)
                tone.release()
            } catch (_: Exception) {}
        }
    }

    fun stopSound() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }
}
