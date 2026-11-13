package com.example.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object QuranAudioDownloadManager {

    val downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadedSurahs = MutableStateFlow<Set<String>>(emptySet())

    fun init(context: Context) {
        val rootDir = getAudioDir(context)
        val files = rootDir.listFiles() ?: emptyArray()
        val downloaded = files.filter { it.isFile && it.length() > 1024 }.map { it.nameWithoutExtension }.toSet()
        downloadedSurahs.value = downloaded
    }

    fun getAudioDir(context: Context): File {
        val dir = File(context.filesDir, "quran_audio")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getLocalSurahFile(context: Context, reciterId: String, surahNumber: Int): File {
        val dir = getAudioDir(context)
        val key = "${reciterId}_surah_${String.format("%03d", surahNumber)}"
        return File(dir, "$key.mp3")
    }

    fun isSurahDownloaded(context: Context, reciterId: String, surahNumber: Int): Boolean {
        val file = getLocalSurahFile(context, reciterId, surahNumber)
        return file.exists() && file.length() > 1024
    }

    suspend fun downloadSurahAudio(
        context: Context,
        reciterId: String,
        surahNumber: Int,
        onProgress: (Float) -> Unit = {}
    ): Boolean = withContext(Dispatchers.IO) {
        val fileKey = "${reciterId}_surah_${String.format("%03d", surahNumber)}"
        val targetFile = getLocalSurahFile(context, reciterId, surahNumber)
        val tempFile = File(targetFile.parentFile, "$fileKey.tmp")

        // Try downloading full Surah mp3 from reliable CDN (e.g. server12 or islamic.network)
        val primaryUrl = "https://server12.mp3quran.net/maher/${String.format("%03d", surahNumber)}.mp3"
        val fallbackUrl = "https://download.quranicaudio.com/quran/mishaari_raashid_al_3afaasee/${String.format("%03d", surahNumber)}.mp3"

        var success = tryDownloadUrl(primaryUrl, tempFile, onProgress)
        if (!success) {
            success = tryDownloadUrl(fallbackUrl, tempFile, onProgress)
        }

        if (success && tempFile.exists() && tempFile.length() > 5000) {
            tempFile.renameTo(targetFile)
            val updated = downloadedSurahs.value + fileKey
            downloadedSurahs.value = updated
            true
        } else {
            tempFile.delete()
            false
        }
    }

    private fun tryDownloadUrl(urlString: String, destination: File, onProgress: (Float) -> Unit): Boolean {
        return try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 8000
            conn.readTimeout = 15000
            conn.instanceFollowRedirects = true
            if (conn.responseCode !in 200..299) return false
            val length = conn.contentLength
            val input = conn.inputStream
            val output = FileOutputStream(destination)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalRead = 0L
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
                totalRead += bytesRead
                if (length > 0) onProgress(totalRead.toFloat() / length.toFloat())
            }
            output.flush()
            output.close()
            input.close()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun deleteDownloadedSurah(context: Context, reciterId: String, surahNumber: Int) {
        val file = getLocalSurahFile(context, reciterId, surahNumber)
        if (file.exists()) file.delete()
        val fileKey = "${reciterId}_surah_${String.format("%03d", surahNumber)}"
        downloadedSurahs.value = downloadedSurahs.value - fileKey
    }
}
