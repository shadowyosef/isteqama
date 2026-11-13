package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.example.R
import com.example.data.QuranBookmarksRepository
import com.example.data.TafseerDataProvider
import com.example.util.AppStrings
import com.example.util.AudioPlayerManager

class QuranWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            val expanded = WidgetUtils.isExpanded(appWidgetManager, id)
            appWidgetManager.updateAppWidget(id, buildQuranRemoteViews(context, expanded))
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?) {
        val expanded = WidgetUtils.isExpanded(appWidgetManager, appWidgetId, newOptions)
        appWidgetManager.updateAppWidget(appWidgetId, buildQuranRemoteViews(context, expanded))
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_PLAY_AUDIO -> {
                val (surah, ayah, globalAyah) = getCurrentAyah(context)
                if (AudioPlayerManager.isPlaying.value) AudioPlayerManager.stop()
                else AudioPlayerManager.playAyah(context, surah, ayah, globalAyah)
                updateAll(context)
            }
            ACTION_NEXT_AYAH -> {
                AudioPlayerManager.stop()
                setAyahIndex(context, (getAyahIndex(context) + 1) % sampleAyahs.size)
                updateAll(context)
            }
            ACTION_PREV_AYAH -> {
                AudioPlayerManager.stop()
                val cur = getAyahIndex(context)
                setAyahIndex(context, if (cur - 1 < 0) sampleAyahs.lastIndex else cur - 1)
                updateAll(context)
            }
            ACTION_BOOKMARK_AYAH -> {
                val item = sampleAyahs[getAyahIndex(context)]
                QuranBookmarksRepository.toggleBookmark(item.surah, item.ayah, 1, item.surahName, item.ayahText)
                updateAll(context)
            }
        }
    }

    private fun buildQuranRemoteViews(context: Context, isExpanded: Boolean): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_quran_interactive)
        val item = sampleAyahs[getAyahIndex(context)]
        val isPlaying = AudioPlayerManager.isPlaying.value
        val isAr = AppStrings.isArabic
        val isSaved = QuranBookmarksRepository.isBookmarked(item.surah, item.ayah)

        views.setTextViewText(R.id.tv_quran_surah_info, "${item.surahName} • آية ${item.ayah}")
        views.setTextViewText(R.id.tv_quran_ayah_text, item.ayahText)
        views.setTextViewText(R.id.btn_quran_bookmark, if (isSaved) "★" else "☆")
        views.setTextViewText(R.id.btn_quran_play_audio, if (isPlaying) (if (isAr) "⏸ إيقاف" else "⏸ Stop") else (if (isAr) "▶ استماع" else "▶ Listen"))
        views.setTextViewText(R.id.tv_quran_audio_status, if (isPlaying) (if (isAr) "جاري التلاوة..." else "Reciting...") else (if (isAr) "آية اليوم" else "Daily Ayah"))

        bindBroadcast(context, views, R.id.btn_quran_play_audio, ACTION_PLAY_AUDIO, 501)
        bindBroadcast(context, views, R.id.btn_quran_next_ayah, ACTION_NEXT_AYAH, 502)
        bindBroadcast(context, views, R.id.btn_quran_prev_ayah, ACTION_PREV_AYAH, 504)
        bindBroadcast(context, views, R.id.btn_quran_bookmark, ACTION_BOOKMARK_AYAH, 505)

        // Button-only deep link directly into this Surah's reader
        views.setTextViewText(R.id.btn_quran_open_reader, if (isAr) "فتح السورة" else "Open Surah")
        views.setOnClickPendingIntent(
            R.id.btn_quran_open_reader,
            WidgetUtils.createOpenTabPendingIntent(context, 503, tabIndex = 1, surahId = item.surah)
        )

        views.setViewVisibility(R.id.widget_quran_expanded, if (isExpanded) View.VISIBLE else View.GONE)
        if (isExpanded) {
            val tafseer = TafseerDataProvider.getRichTafseer(item.surah, item.ayah).muyassar
            val (khatmaPct, _) = WidgetUtils.getKhatmaPercentAndPages(context)
            views.setTextViewText(R.id.tv_quran_tafseer, tafseer)
            views.setTextViewText(R.id.tv_quran_khatma_pct, (if (isAr) "إنجاز الختمة: " else "Khatma Progress: ") + "$khatmaPct%")
        }
        return views
    }

    private fun bindBroadcast(context: Context, views: RemoteViews, viewId: Int, actionStr: String, reqCode: Int) {
        val intent = Intent(context, QuranWidgetProvider::class.java).apply { action = actionStr }
        val pi = PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        views.setOnClickPendingIntent(viewId, pi)
    }

    companion object {
        const val ACTION_PLAY_AUDIO = "com.example.widget.ACTION_QURAN_PLAY"
        const val ACTION_NEXT_AYAH = "com.example.widget.ACTION_QURAN_NEXT"
        const val ACTION_PREV_AYAH = "com.example.widget.ACTION_QURAN_PREV"
        const val ACTION_BOOKMARK_AYAH = "com.example.widget.ACTION_QURAN_BOOKMARK"
        private const val PREFS = "quran_widget_prefs"

        private val sampleAyahs = listOf(
            SampleAyah(2, 255, 262, "سورة البقرة", "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ..."),
            SampleAyah(1, 1, 1, "سورة الفاتحة", "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۝ الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ"),
            SampleAyah(112, 1, 6221, "سورة الإخلاص", "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ"),
            SampleAyah(65, 3, 5238, "سورة الطلاق", "وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ ۚ إِنَّ اللَّهَ بَالِغُ أَمْرِهِ"),
            SampleAyah(94, 5, 6095, "سورة الشرح", "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا ۝ إِنَّ مَعَ الْعُسْرِ يُسْرًا")
        )

        private fun getAyahIndex(context: Context): Int = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("idx", 0).coerceIn(sampleAyahs.indices)
        private fun setAyahIndex(context: Context, idx: Int) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt("idx", idx).apply()
        fun getCurrentAyah(context: Context): Triple<Int, Int, Int> {
            val a = sampleAyahs[getAyahIndex(context)]
            return Triple(a.surah, a.ayah, a.globalAyah)
        }

        fun updateAll(context: Context) {
            val wm = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, QuranWidgetProvider::class.java)
            val provider = QuranWidgetProvider()
            for (id in wm.getAppWidgetIds(cn)) {
                wm.updateAppWidget(id, provider.buildQuranRemoteViews(context, WidgetUtils.isExpanded(wm, id)))
            }
        }
    }
}

private data class SampleAyah(val surah: Int, val ayah: Int, val globalAyah: Int, val surahName: String, val ayahText: String)
