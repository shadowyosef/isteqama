package com.example.model

import android.view.Gravity

enum class ToastPositionPreset(
    val id: String,
    val labelAr: String,
    val labelEn: String,
    val gravity: Int,
    val defaultX: Int,
    val defaultY: Int
) {
    TOP_CENTER("top_center", "أعلى الشاشة (وسط)", "Top Center", Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 120),
    TOP_RIGHT("top_right", "أعلى اليمين", "Top Right", Gravity.TOP or Gravity.END, 40, 120),
    TOP_LEFT("top_left", "أعلى اليسار", "Top Left", Gravity.TOP or Gravity.START, 40, 120),
    CENTER_RIGHT("center_right", "وسط الشاشة (يمين)", "Center Right", Gravity.CENTER_VERTICAL or Gravity.END, 20, 0),
    CENTER_LEFT("center_left", "وسط الشاشة (يسار)", "Center Left", Gravity.CENTER_VERTICAL or Gravity.START, 20, 0),
    CENTER("center", "منتصف الشاشة", "Center", Gravity.CENTER, 0, 0),
    BOTTOM_CENTER("bottom_center", "أسفل الشاشة (وسط)", "Bottom Center", Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 220),
    BOTTOM_RIGHT("bottom_right", "أسفل اليمين", "Bottom Right", Gravity.BOTTOM or Gravity.END, 40, 220),
    BOTTOM_LEFT("bottom_left", "أسفل اليسار", "Bottom Left", Gravity.BOTTOM or Gravity.START, 40, 220),
    LAST_DRAGGED("last_dragged", "آخر مكان تم سحبه إليه (تلقائي)", "Last Dragged Position", Gravity.TOP or Gravity.START, 100, 200);

    companion object {
        fun fromId(id: String?): ToastPositionPreset {
            return values().find { it.id == id } ?: TOP_CENTER
        }
    }
}
