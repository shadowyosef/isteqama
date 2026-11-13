package com.example.ui.quran

import androidx.compose.runtime.Composable
import com.example.data.QuranRepository
import com.example.model.Ayah

@Composable
fun TafseerModal(
    ayah: Ayah,
    onDismiss: () -> Unit
) {
    val surahName = QuranRepository.getSurah(ayah.surahNumber)?.nameAr ?: "سورة ${ayah.surahNumber}"
    TafseerBottomSheet(
        surahNumber = ayah.surahNumber,
        ayahNumber = ayah.numberInSurah,
        ayahText = ayah.textArabic,
        surahName = surahName,
        globalAyahNumber = ayah.globalNumber,
        pageNumber = ayah.page,
        onDismiss = onDismiss
    )
}
