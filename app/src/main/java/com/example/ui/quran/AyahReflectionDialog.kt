package com.example.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.HifzLoopAndReflectionRepository
import com.example.data.QuranRepository
import com.example.model.Ayah
import com.example.util.AppStrings

@Composable
fun AyahReflectionDialog(
    ayah: Ayah,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isAr = AppStrings.isArabic
    val surahName = QuranRepository.getSurah(ayah.surahNumber)?.nameAr ?: ""
    var note by remember(ayah) {
        mutableStateOf(HifzLoopAndReflectionRepository.getReflection(ayah.surahNumber, ayah.numberInSurah))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isAr) "تدبر وخاطرة • سورة $surahName (${ayah.numberInSurah})" else "Reflection • Surah $surahName (${ayah.numberInSurah})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(ayah.textArabic, fontSize = 13.sp, maxLines = 3)
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(if (isAr) "اكتب خاطرتك أو دعاءً مستلهماً..." else "Write reflection or dua...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(if (isAr) "إلغاء" else "Cancel")
                    }
                    Button(
                        onClick = {
                            HifzLoopAndReflectionRepository.saveReflection(context, ayah.surahNumber, ayah.numberInSurah, surahName, note)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isAr) "حفظ الخاطرة" else "Save Note")
                    }
                }
            }
        }
    }
}
