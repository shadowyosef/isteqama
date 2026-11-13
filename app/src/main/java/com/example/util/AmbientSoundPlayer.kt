package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

enum class AmbientNatureType(val id: String, val labelAr: String, val labelEn: String) {
    NONE("none", "صامت", "Off"),
    SOFT_RAIN("rain", "مطر هادئ", "Soft Rain"),
    DAWN_BREEZE("breeze", "نسيم الفجر", "Dawn Breeze"),
    CALM_HUSH("hush", "سكون الليل", "Night Quiet")
}

object AmbientSoundPlayer {
    private val _currentSound = MutableStateFlow(AmbientNatureType.NONE)
    val currentSound = _currentSound.asStateFlow()

    private var track: AudioTrack? = null
    private var worker: Thread? = null

    fun play(type: AmbientNatureType) {
        stop()
        if (type == AmbientNatureType.NONE) return
        _currentSound.value = type
        val sampleRate = 16000
        val bufSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT).coerceAtLeast(4096)
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
                )
                .setAudioFormat(
                    AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build()
                )
                .setBufferSizeInBytes(bufSize).setTransferMode(AudioTrack.MODE_STREAM).build()
            track = audioTrack
            audioTrack.play()
            worker = Thread {
                val buf = ShortArray(bufSize / 2)
                var lp = 0f
                var slow = 0f
                val alpha = when (type) {
                    AmbientNatureType.SOFT_RAIN -> 0.12f
                    AmbientNatureType.DAWN_BREEZE -> 0.04f
                    else -> 0.02f
                }
                while (!Thread.currentThread().isInterrupted && _currentSound.value == type) {
                    for (i in buf.indices) {
                        val white = (Random.nextFloat() * 2f - 1f)
                        lp += alpha * (white - lp)
                        slow += 0.002f * (lp - slow)
                        val sample = if (type == AmbientNatureType.SOFT_RAIN) lp else slow
                        buf[i] = (sample * 5200f).toInt().coerceIn(-32000, 32000).toShort()
                    }
                    try { audioTrack.write(buf, 0, buf.size) } catch (_: Exception) { break }
                }
            }.apply { isDaemon = true; start() }
        } catch (_: Exception) { stop() }
    }

    fun stop() {
        _currentSound.value = AmbientNatureType.NONE
        worker?.interrupt()
        worker = null
        try { track?.pause(); track?.flush(); track?.stop(); track?.release() } catch (_: Exception) {}
        track = null
    }
}
