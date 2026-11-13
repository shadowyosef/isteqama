package com.example.util

import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import com.example.model.ToastPositionPreset

object FloatingToastPositionManager {
    private const val PREFS_NAME = "istaqama_toast_pos_prefs"
    private const val KEY_PRESET = "toast_preset_id"
    private const val KEY_POS_X = "toast_last_x"
    private const val KEY_POS_Y = "toast_last_y"
    private const val KEY_HAS_CUSTOM = "toast_has_custom_drag"

    fun getSelectedPreset(context: Context): ToastPositionPreset {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val id = prefs.getString(KEY_PRESET, ToastPositionPreset.TOP_CENTER.id)
        return ToastPositionPreset.fromId(id)
    }

    fun setPreset(context: Context, preset: ToastPositionPreset) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PRESET, preset.id).apply()
    }

    fun saveLastDraggedPosition(context: Context, x: Int, y: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_POS_X, x)
            .putInt(KEY_POS_Y, y)
            .putBoolean(KEY_HAS_CUSTOM, true)
            .putString(KEY_PRESET, ToastPositionPreset.LAST_DRAGGED.id)
            .apply()
    }

    fun getLastDraggedX(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_POS_X, 100)
    }

    fun getLastDraggedY(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_POS_Y, 200)
    }

    fun applyToLayoutParams(context: Context, params: WindowManager.LayoutParams) {
        val preset = getSelectedPreset(context)
        if (preset == ToastPositionPreset.LAST_DRAGGED) {
            params.gravity = Gravity.TOP or Gravity.START
            params.x = getLastDraggedX(context)
            params.y = getLastDraggedY(context)
        } else {
            params.gravity = preset.gravity
            params.x = preset.defaultX
            params.y = preset.defaultY
        }
    }
}
