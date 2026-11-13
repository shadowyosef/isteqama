package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow

enum class BookmarkCategory(val labelAr: String, val labelEn: String) {
    TADABBUR("تدبر", "Reflection"),
    HIFZ("حفظ", "Memorization"),
    KHATMA("ختمة", "Khatma")
}

data class QuranBookmark(
    val id: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val pageNumber: Int,
    val surahNameAr: String,
    val ayahText: String = "",
    val category: BookmarkCategory = BookmarkCategory.TADABBUR,
    val timestamp: Long = System.currentTimeMillis()
)

object QuranBookmarksRepository {
    val bookmarks = MutableStateFlow(
        listOf(
            QuranBookmark(
                id = "b_default",
                surahNumber = 67,
                ayahNumber = 1,
                pageNumber = 562,
                surahNameAr = "سورة الملك",
                ayahText = "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
                category = BookmarkCategory.TADABBUR
            )
        )
    )

    fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        return bookmarks.value.any { it.surahNumber == surahNumber && it.ayahNumber == ayahNumber }
    }

    fun toggleBookmark(
        surahNumber: Int,
        ayahNumber: Int,
        pageNumber: Int,
        surahNameAr: String,
        ayahText: String = "",
        category: BookmarkCategory = BookmarkCategory.TADABBUR
    ) {
        val existing = bookmarks.value.find { it.surahNumber == surahNumber && it.ayahNumber == ayahNumber }
        if (existing != null && existing.category == category) {
            bookmarks.value = bookmarks.value.filter { it.id != existing.id }
        } else {
            val cleaned = bookmarks.value.filterNot { it.surahNumber == surahNumber && it.ayahNumber == ayahNumber }
            val newBm = QuranBookmark(
                id = "bm_${System.currentTimeMillis()}",
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                pageNumber = pageNumber,
                surahNameAr = surahNameAr,
                ayahText = ayahText,
                category = category
            )
            bookmarks.value = cleaned + newBm
        }
    }
}
