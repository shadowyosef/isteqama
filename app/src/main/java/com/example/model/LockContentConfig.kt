package com.example.model

data class LockContentConfig(
    val selectedSurahIds: List<Int> = listOf(1), // Surah numbers (1..114)
    val startAyah: Int = 1,
    val endAyah: Int = 0, // 0 means through to the end of the surah
    val ayahsPerSession: Int = 7,
    val currentProgressAyah: Int = 1,
    val isTimedMode: Boolean = false,
    val readingDurationMinutes: Int = 15,
    val selectedDhikrIds: List<String> = emptyList(),
    val selectedRoutineId: String? = null,
    val currentSurahIndex: Int = 0,
    val isPageMode: Boolean = false,
    val pagesPerSession: Int = 1,
    val startPage: Int = 1,
    val endPage: Int = 604,
    val currentProgressPage: Int = 1
) {
    fun serialize(): String {
        val surahs = selectedSurahIds.joinToString(",")
        val dhikrs = selectedDhikrIds.joinToString(",")
        val routine = selectedRoutineId ?: ""
        return "$surahs|$startAyah|$endAyah|$ayahsPerSession|$currentProgressAyah|$isTimedMode|$readingDurationMinutes|$dhikrs|$routine|$currentSurahIndex|$isPageMode|$pagesPerSession|$startPage|$endPage|$currentProgressPage"
    }

    companion object {
        fun deserialize(raw: String): LockContentConfig {
            if (raw.isBlank()) return LockContentConfig()
            val p = raw.split("|")
            if (p.size < 9) return LockContentConfig()
            val surahs = p[0].split(",").mapNotNull { it.trim().toIntOrNull() }.ifEmpty { listOf(1) }
            val start = p[1].toIntOrNull() ?: 1
            val end = p[2].toIntOrNull() ?: 0
            val perSession = p[3].toIntOrNull() ?: 7
            val current = p[4].toIntOrNull() ?: 1
            val timed = p[5].toBooleanStrictOrNull() ?: false
            val duration = p[6].toIntOrNull() ?: 15
            val dhikrs = p[7].split(",").filter { it.isNotBlank() }
            val routine = p[8].ifBlank { null }
            val surahIdx = p.getOrNull(9)?.toIntOrNull() ?: 0
            val pageMode = p.getOrNull(10)?.toBooleanStrictOrNull() ?: false
            val pagesPerSession = p.getOrNull(11)?.toIntOrNull() ?: 1
            val startPage = p.getOrNull(12)?.toIntOrNull() ?: 1
            val endPage = p.getOrNull(13)?.toIntOrNull() ?: 604
            val currentProgressPage = p.getOrNull(14)?.toIntOrNull() ?: startPage
            return LockContentConfig(
                selectedSurahIds = surahs,
                startAyah = start,
                endAyah = end,
                ayahsPerSession = perSession,
                currentProgressAyah = current,
                isTimedMode = timed,
                readingDurationMinutes = duration,
                selectedDhikrIds = dhikrs,
                selectedRoutineId = routine,
                currentSurahIndex = surahIdx,
                isPageMode = pageMode,
                pagesPerSession = pagesPerSession,
                startPage = startPage,
                endPage = endPage,
                currentProgressPage = currentProgressPage
            )
        }
    }
}
