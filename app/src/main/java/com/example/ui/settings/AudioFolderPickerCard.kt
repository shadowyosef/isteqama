package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AppStrings
import com.example.util.AudioPlayerManager
import com.example.util.SafAudioLookupHelper

@Composable
fun AudioFolderPickerCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val treeUri by AudioPlayerManager.localAudioTreeUri.collectAsState()
    var legacyPath by remember { mutableStateOf(AudioPlayerManager.localAudioFolderPath ?: "") }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            AudioPlayerManager.setLocalAudioTreeUri(context, uri)
        }
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = modifier.fillMaxWidth().testTag("audio_folder_picker_card")
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = if (isAr) "مجلد الصوتيات المحلي للقرآن (أوفلاين)" else "Offline Quran Audio Folder",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            val folderLabel = SafAudioLookupHelper.readableFolderName(treeUri)
            Text(
                text = if (folderLabel.isNotBlank()) {
                    if (isAr) "المجلد المحدد: $folderLabel ✓" else "Selected Folder: $folderLabel ✓"
                } else if (isAr) {
                    "اختر مجلداً يحتوي ملفات السور (مثل 001.mp3 أو 1.mp3)"
                } else {
                    "Pick a folder containing Surah MP3 files (001.mp3, 1.mp3)"
                },
                fontSize = 11.sp,
                color = if (folderLabel.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { folderPickerLauncher.launch(null) },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("pick_audio_folder_btn")
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isAr) "اختيار مجلد من الجهاز" else "Choose Device Folder", fontSize = 12.sp)
                }
                if (treeUri != null) {
                    IconButton(
                        onClick = { AudioPlayerManager.setLocalAudioTreeUri(context, null) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }

            OutlinedTextField(
                value = legacyPath,
                onValueChange = { legacyPath = it; AudioPlayerManager.localAudioFolderPath = it.ifBlank { null } },
                label = { Text(if (isAr) "أو مسار مباشر (اختياري)" else "Or direct path (optional)", fontSize = 11.sp) },
                placeholder = { Text("/sdcard/QuranAudio/") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
