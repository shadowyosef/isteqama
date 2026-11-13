package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract

object SafAudioLookupHelper {
    private const val PREFS_NAME = "quran_audio_prefs"

    fun loadSavedTreeUri(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString("local_audio_tree_uri", null)

    fun loadSavedPath(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString("local_audio_path", null)

    fun saveTreeUri(context: Context, uri: Uri?): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (uri == null) {
            prefs.edit().remove("local_audio_tree_uri").apply()
            return null
        }
        try {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: Exception) {}
        val str = uri.toString()
        prefs.edit().putString("local_audio_tree_uri", str).apply()
        return str
    }

    fun findSurahAudioUri(context: Context, treeUriString: String?, surah: Int): Uri? {
        if (treeUriString.isNullOrBlank()) return null
        val treeUri = runCatching { Uri.parse(treeUriString) }.getOrNull() ?: return null
        return try {
            val treeDocId = DocumentsContract.getTreeDocumentId(treeUri)
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, treeDocId)
            val padded = String.format("%03d", surah)
            val targets = setOf("$padded.mp3", "$surah.mp3", "$padded.m4a", "$surah.m4a")
            val projection = arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME
            )
            context.contentResolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
                val idIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val nameIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val docId = if (idIdx >= 0) cursor.getString(idIdx) else continue
                    val name = if (nameIdx >= 0) cursor.getString(nameIdx)?.lowercase() ?: "" else ""
                    if (name in targets || name.startsWith("${padded}_") || name.startsWith("$padded.")) {
                        return DocumentsContract.buildDocumentUriUsingTree(treeUri, docId)
                    }
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    fun readableFolderName(treeUriString: String?): String {
        if (treeUriString.isNullOrBlank()) return ""
        val decoded = Uri.decode(treeUriString)
        return decoded.substringAfterLast(':').ifBlank { decoded.substringAfterLast('/') }
    }
}
