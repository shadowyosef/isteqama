package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONArray
import org.json.JSONObject

data class AyahReflection(
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val note: String,
    val timestampMs: Long = System.currentTimeMillis()
)

data class MutashabihatQuestion(
    val promptAr: String,
    val promptEn: String,
    val options: List<String>,
    val correctIndex: Int
)

object HifzLoopAndReflectionRepository {
    private const val PREF_NAME = "hifz_reflection_prefs"
    private var prefs: android.content.SharedPreferences? = null

    val tajweedEnabled = MutableStateFlow(true)
    val hifzLoopCount = MutableStateFlow(3)
    val hideWordsEnabled = MutableStateFlow(false)
    val reflections = MutableStateFlow<List<AyahReflection>>(emptyList())

    val mutashabihatBank = listOf(
        MutashabihatQuestion(
            "أكمل خواتيم البقرة: «اعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا أَنتَ مَوْلَانَا ...»",
            "Complete Al-Baqarah 286 ending:",
            listOf("فَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ", "فَافْتَحْ بَيْنَنَا وَبَيْنَ قَوْمِنَا", "إِنَّكَ أَنتَ السَّمِيعُ الْعَلِيمُ"), 0
        ),
        MutashabihatQuestion(
            "الفرق في سورة البقرة وآل عمران: أي موضع ورد بلفظ «وَبِئْسَ الْمَهَادُ» في البقرة؟",
            "Where does 'Wa bi'sa al-mihad' appear?",
            listOf("فَحَسْبُهُ جَهَنَّمُ وَلَبِئْسَ الْمِهَادُ (البقرة ٢٠٦)", "أُولَٰئِكَ مَأْوَاهُمْ جَهَنَّمُ وَسَاءَتْ مَصِيرًا", "خَالِدِينَ فِيهَا حَسُنَتْ مُسْتَقَرًّا"), 0
        ),
        MutashabihatQuestion(
            "في سورة الملك: «أَأَمِنتُم مَّن فِي السَّمَاءِ أَن يَخْسِفَ بِكُمُ الْأَرْضَ فَإِذَا هِيَ ...»",
            "Complete Al-Mulk 16 ending:",
            listOf("تَمُورُ", "تَفُورُ", "تَدُورُ"), 0
        ),
        MutashabihatQuestion(
            "في سورة الشعراء عند كل قصة نبي يتكرر قوله تعالى: «إِنَّ فِي ذَٰلِكَ لَآيَةً وَمَا كَانَ أَكْثَرُهُم ...»",
            "Complete Ash-Shu'ara refrain:",
            listOf("مُّؤْمِنِينَ", "مُسْلِمِينَ", "صَادِقِينَ"), 0
        )
    )

    fun init(context: Context) {
        if (prefs != null) return
        val p = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs = p
        tajweedEnabled.value = p.getBoolean("tajweed_enabled", true)
        hifzLoopCount.value = p.getInt("loop_count", 3)
        reflections.value = loadReflections(p.getString("reflections_json", null))
    }

    fun toggleTajweed(context: Context) {
        init(context)
        val next = !tajweedEnabled.value
        tajweedEnabled.value = next
        prefs?.edit()?.putBoolean("tajweed_enabled", next)?.apply()
    }

    fun setLoopCount(context: Context, count: Int) {
        init(context)
        val safe = count.coerceIn(1, 20)
        hifzLoopCount.value = safe
        prefs?.edit()?.putInt("loop_count", safe)?.apply()
    }

    fun saveReflection(context: Context, surah: Int, ayah: Int, surahName: String, note: String) {
        init(context)
        if (note.isBlank()) return
        val clean = AyahReflection(surah, ayah, surahName, note.trim())
        val updated = listOf(clean) + reflections.value.filterNot { it.surahNumber == surah && it.ayahNumber == ayah }
        reflections.value = updated.take(100)
        saveReflectionsList(updated.take(100))
    }

    fun getReflection(surah: Int, ayah: Int): String {
        return reflections.value.firstOrNull { it.surahNumber == surah && it.ayahNumber == ayah }?.note ?: ""
    }

    private fun saveReflectionsList(list: List<AyahReflection>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(JSONObject().put("s", it.surahNumber).put("a", it.ayahNumber).put("n", it.surahName).put("note", it.note).put("ts", it.timestampMs))
        }
        prefs?.edit()?.putString("reflections_json", arr.toString())?.apply()
    }

    private fun loadReflections(raw: String?): List<AyahReflection> {
        if (raw.isNullOrBlank()) return emptyList()
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                AyahReflection(o.getInt("s"), o.getInt("a"), o.optString("n"), o.getString("note"), o.optLong("ts"))
            }
        } catch (_: Exception) { emptyList() }
    }
}
