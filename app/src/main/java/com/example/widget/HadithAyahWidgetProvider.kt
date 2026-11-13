package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.HadithLibraryCatalog
import com.example.data.QuranRepository

class HadithAyahWidgetProvider : AppWidgetProvider() {
    companion object {
        const val ACTION_NEXT = "com.example.widget.ACTION_HADITH_NEXT"
        const val ACTION_TOGGLE_MODE = "com.example.widget.ACTION_HADITH_TOGGLE_MODE"
        private const val PREFS = "hadith_widget_prefs"

        fun updateAllWidgets(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, HadithAyahWidgetProvider::class.java))
            if (ids.isNotEmpty()) updateWidgets(context, mgr, ids)
        }

        private fun updateWidgets(context: Context, mgr: AppWidgetManager, ids: IntArray) {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val isAyahMode = prefs.getBoolean("is_ayah_mode", false)
            val idx = prefs.getInt("item_idx", 0)

            val (header, body, source) = if (isAyahMode) {
                val surah = QuranRepository.getAllSurahs().getOrNull((idx % 114))
                val ayahs = if (surah != null) QuranRepository.getAyahsForSurah(surah.number) else emptyList()
                val ayah = ayahs.firstOrNull()
                Triple(
                    "آية اليوم المباركة ✦",
                    ayah?.textArabic ?: "﴿إِنَّ مَعَ الْعُسْرِ يُسْرًا﴾",
                    "${surah?.nameAr ?: "الشرح"} • آية ${ayah?.numberInSurah ?: 6}"
                )
            } else {
                val h = HadithLibraryCatalog.hadiths[idx % HadithLibraryCatalog.hadiths.size]
                Triple("حديث اليوم النبوي ✦", h.arabicText, "${h.sourceAr} • ${h.narratorAr}")
            }

            ids.forEach { id ->
                val views = RemoteViews(context.packageName, R.layout.widget_hadith_ayah)
                views.setTextViewText(R.id.tv_hadith_widget_header, header)
                views.setTextViewText(R.id.tv_hadith_widget_body, body)
                views.setTextViewText(R.id.tv_hadith_widget_source, source)

                val nextPi = PendingIntent.getBroadcast(
                    context, 71, Intent(context, HadithAyahWidgetProvider::class.java).apply { action = ACTION_NEXT },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val modePi = PendingIntent.getBroadcast(
                    context, 72, Intent(context, HadithAyahWidgetProvider::class.java).apply { action = ACTION_TOGGLE_MODE },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val openPi = PendingIntent.getActivity(
                    context, 73, Intent(context, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.btn_hadith_widget_next, nextPi)
                views.setOnClickPendingIntent(R.id.btn_hadith_widget_mode, modePi)
                views.setOnClickPendingIntent(R.id.tv_hadith_widget_body, openPi)
                mgr.updateAppWidget(id, views)
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        when (intent.action) {
            ACTION_NEXT -> { prefs.edit().putInt("item_idx", prefs.getInt("item_idx", 0) + 1).apply(); updateAllWidgets(context) }
            ACTION_TOGGLE_MODE -> { prefs.edit().putBoolean("is_ayah_mode", !prefs.getBoolean("is_ayah_mode", false)).apply(); updateAllWidgets(context) }
        }
    }
}
