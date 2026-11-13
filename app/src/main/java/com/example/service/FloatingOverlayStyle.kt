package com.example.service

enum class FloatingOverlayStyle {
    INTERACTIVE_WIDGET,
    COMPACT_BUBBLE
}

object FloatingOverlayPreferences {
    var selectedStyle: FloatingOverlayStyle = FloatingOverlayStyle.INTERACTIVE_WIDGET
}
