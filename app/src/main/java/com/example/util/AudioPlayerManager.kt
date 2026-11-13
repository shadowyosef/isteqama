package com.example.util

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.data.QuranAudioDownloadManager
import com.example.data.StatsRepository
import com.example.model.ReciterListProvider
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

object AudioPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    val isPlaying = MutableStateFlow(false)
    val currentSurah = MutableStateFlow<Int?>(null)
    val currentAyah = MutableStateFlow<Int?>(null)
    val playbackSpeed = MutableStateFlow(1.0f)
    val isContinuousMode = MutableStateFlow(true)
    val isSurahLoopMode = MutableStateFlow(false)
    val playbackProgress = MutableStateFlow(0f)
    val localAudioTreeUri = MutableStateFlow<String?>(null)

    var localAudioFolderPath: String? = null
    var selectedReciter: String = "ar.alafasy"
    private var currentContext: Context? = null
    private var progressThread: Thread? = null

    fun init(context: Context) {
        localAudioTreeUri.value = SafAudioLookupHelper.loadSavedTreeUri(context)
        localAudioFolderPath = SafAudioLookupHelper.loadSavedPath(context)
    }

    fun setLocalAudioTreeUri(context: Context, uri: Uri?) {
        localAudioTreeUri.value = SafAudioLookupHelper.saveTreeUri(context, uri)
    }

    fun playAyah(context: Context, surah: Int, ayah: Int, globalAyah: Int) {
        currentContext = context.applicationContext
        stop()
        currentSurah.value = surah
        currentAyah.value = ayah
        StatsRepository.recordAyahsRead(1)

        val offline = QuranAudioDownloadManager.getLocalSurahFile(context, selectedReciter, surah)
        val safUri = SafAudioLookupHelper.findSurahAudioUri(context, localAudioTreeUri.value, surah)
        val local = findLocalFile(surah)
        val reciter = ReciterListProvider.getReciter(selectedReciter)
        val streamUrl = String.format("https://everyayah.com/data/%s/%03d%03d.mp3", reciter.everyAyahFolder, surah, ayah)

        try {
            mediaPlayer = MediaPlayer().apply {
                when {
                    offline.exists() && offline.length() > 5000 -> setDataSource(context, Uri.fromFile(offline))
                    safUri != null -> setDataSource(context, safUri)
                    local != null && local.exists() -> setDataSource(context, Uri.fromFile(local))
                    else -> setDataSource(streamUrl)
                }
                setOnPreparedListener {
                    try { playbackParams = playbackParams.setSpeed(playbackSpeed.value) } catch (_: Exception) {}
                    start()
                    this@AudioPlayerManager.isPlaying.value = true
                    startTracker()
                }
                setOnCompletionListener { handleCompletion() }
                setOnErrorListener { _, _, _ -> tryFallback(globalAyah); true }
                prepareAsync()
            }
        } catch (_: Exception) { isPlaying.value = false }
    }

    private fun tryFallback(globalAyah: Int) {
        try {
            val url = "https://cdn.islamic.network/quran/audio/128/$selectedReciter/$globalAyah.mp3"
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener { start(); this@AudioPlayerManager.isPlaying.value = true }
                setOnCompletionListener { handleCompletion() }
                setOnErrorListener { _, _, _ -> this@AudioPlayerManager.isPlaying.value = false; true }
                prepareAsync()
            }
        } catch (_: Exception) { isPlaying.value = false }
    }

    private fun startTracker() {
        progressThread?.interrupt()
        progressThread = Thread {
            while (!Thread.currentThread().isInterrupted && mediaPlayer != null) {
                try {
                    val mp = mediaPlayer
                    if (mp != null && mp.isPlaying && mp.duration > 0) {
                        playbackProgress.value = (mp.currentPosition.toFloat() / mp.duration).coerceIn(0f, 1f)
                    }
                    Thread.sleep(120)
                } catch (_: InterruptedException) { break } catch (_: Exception) {}
            }
        }.apply { isDaemon = true; start() }
    }

    private fun handleCompletion() {
        playbackProgress.value = 1f
        if (!isContinuousMode.value) { isPlaying.value = false; currentAyah.value = null; return }
        val s = currentSurah.value ?: return; val a = currentAyah.value ?: return; val ctx = currentContext ?: return
        if (a >= com.example.data.QuranRepository.getAyahsForSurah(s).size) {
            QuranSleepTimerManager.onSurahCompleted(s)
            if (!isPlaying.value) return
        }
        AudioSequenceHelper.getNextAyahTarget(s, a, isSurahLoopMode.value)?.let {
            playAyah(ctx, it.first, it.second.numberInSurah, it.second.globalNumber)
        } ?: stop()
    }

    fun playNextAyah() {
        val s = currentSurah.value ?: return; val a = currentAyah.value ?: return; val ctx = currentContext ?: return
        AudioSequenceHelper.getNextAyahTarget(s, a, isSurahLoopMode.value)?.let { playAyah(ctx, it.first, it.second.numberInSurah, it.second.globalNumber) }
    }

    fun playPreviousAyah() {
        val s = currentSurah.value ?: return; val a = currentAyah.value ?: return; val ctx = currentContext ?: return
        AudioSequenceHelper.getPrevAyahTarget(s, a)?.let { playAyah(ctx, it.first, it.second.numberInSurah, it.second.globalNumber) }
    }

    fun setSpeed(speed: Float) {
        playbackSpeed.value = speed
        mediaPlayer?.let { try { it.playbackParams = it.playbackParams.setSpeed(speed) } catch (_: Exception) {} }
    }
    fun toggleContinuousMode() { isContinuousMode.value = !isContinuousMode.value }
    fun toggleLoopMode() { isSurahLoopMode.value = !isSurahLoopMode.value }

    private fun findLocalFile(surah: Int): File? {
        val dir = File(localAudioFolderPath ?: return null)
        if (!dir.exists() || !dir.isDirectory) return null
        val f3 = File(dir, String.format("%03d.mp3", surah))
        val fSimple = File(dir, "$surah.mp3")
        return if (f3.exists()) f3 else if (fSimple.exists()) fSimple else null
    }

    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) { it.pause(); isPlaying.value = false }
            else { it.start(); isPlaying.value = true; startTracker() }
        }
    }

    fun stop() {
        progressThread?.interrupt(); progressThread = null; playbackProgress.value = 0f
        mediaPlayer?.apply { try { if (isPlaying) stop(); release() } catch (_: Exception) {} }
        mediaPlayer = null; isPlaying.value = false
    }
}
