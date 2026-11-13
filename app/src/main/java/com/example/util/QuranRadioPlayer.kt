package com.example.util

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.data.QuranRadioStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object QuranRadioPlayer {
    private var player: MediaPlayer? = null

    private val _activeStation = MutableStateFlow<QuranRadioStation?>(null)
    val activeStation = _activeStation.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering = _isBuffering.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun playStation(station: QuranRadioStation) {
        stop()
        _activeStation.value = station
        _isBuffering.value = true
        _errorMessage.value = null
        try {
            AudioPlayerManager.stop()
            player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(station.streamUrl)
                setOnPreparedListener {
                    _isBuffering.value = false
                    start()
                    _isPlaying.value = true
                }
                setOnErrorListener { _, _, _ ->
                    _isBuffering.value = false
                    _isPlaying.value = false
                    _errorMessage.value = AppStrings.get("تعذر الاتصال بالإذاعة، تحقق من الإنترنت", "Stream unavailable offline")
                    true
                }
                prepareAsync()
            }
        } catch (_: Exception) {
            _isBuffering.value = false
            _isPlaying.value = false
            _errorMessage.value = AppStrings.get("خطأ في تشغيل البث المباشر", "Failed to start radio stream")
        }
    }

    fun stop() {
        try { player?.stop(); player?.release() } catch (_: Exception) {}
        player = null
        _isPlaying.value = false
        _isBuffering.value = false
        _activeStation.value = null
    }
}
