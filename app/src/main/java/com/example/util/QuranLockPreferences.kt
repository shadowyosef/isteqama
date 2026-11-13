package com.example.util

import android.content.Context

object QuranLockPreferences {
    private const val PREFS_NAME = "istaqama_quran_lock_prefs"
    private const val KEY_FONT_SIZE = "quran_lock_font_size"
    private const val KEY_DARK_THEME = "quran_lock_dark_theme"
    private const val KEY_LAST_AYAH_PREFIX = "quran_last_ayah_page_"

    fun getFontSize(context: Context): Float {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_FONT_SIZE, 18f).coerceIn(13f, 30f)
    }

    fun setFontSize(context: Context, size: Float) {
        val clamped = size.coerceIn(13f, 30f)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_FONT_SIZE, clamped).apply()
    }

    fun isDarkTheme(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK_THEME, true)
    }

    fun setDarkTheme(context: Context, isDark: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_THEME, isDark).apply()
    }

    fun getLastSelectedAyah(context: Context, pageNumber: Int): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt("$KEY_LAST_AYAH_PREFIX$pageNumber", -1)
    }

    fun setLastSelectedAyah(context: Context, pageNumber: Int, ayahNumber: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt("$KEY_LAST_AYAH_PREFIX$pageNumber", ayahNumber).apply()
    }
}
