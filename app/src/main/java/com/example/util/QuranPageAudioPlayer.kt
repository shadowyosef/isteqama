package com.example.util

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

class QuranPageAudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    var isPlaying: Boolean = false
        private set
    var isLoading: Boolean = false
        private set
    var currentPage: Int = -1
        private set

    fun playPage(pageNumber: Int, onStateChange: (isPlaying: Boolean, isLoading: Boolean) -> Unit) {
        if (currentPage == pageNumber && mediaPlayer != null) {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                isPlaying = false
                isLoading = false
                onStateChange(false, false)
            } else {
                mediaPlayer?.start()
                isPlaying = true
                isLoading = false
                onStateChange(true, false)
            }
            return
        }

        stop()
        currentPage = pageNumber
        isLoading = true
        isPlaying = false
        onStateChange(false, true)

        try {
            val formatted = String.format("%03d", pageNumber)
            val url = "https://everyayah.com/data/Alafasy_128kbps/PageAudio/Page_$formatted.mp3"
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    this@QuranPageAudioPlayer.isLoading = false
                    this@QuranPageAudioPlayer.isPlaying = true
                    mp.start()
                    onStateChange(true, false)
                }
                setOnCompletionListener {
                    this@QuranPageAudioPlayer.isPlaying = false
                    this@QuranPageAudioPlayer.isLoading = false
                    onStateChange(false, false)
                }
                setOnErrorListener { _, what, extra ->
                    Log.w("QuranPageAudioPlayer", "Audio error: what=$what extra=$extra")
                    stop()
                    onStateChange(false, false)
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("QuranPageAudioPlayer", "Failed to start audio", e)
            stop()
            onStateChange(false, false)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        isPlaying = false
        isLoading = false
        currentPage = -1
    }

    fun release() {
        stop()
    }
}
