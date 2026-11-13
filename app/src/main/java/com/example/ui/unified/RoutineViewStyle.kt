package com.example.ui.unified

enum class RoutineViewStyle {
    DECK,
    EXPANDABLE,
    GRID
}

object RoutineViewPreferences {
    var currentStyle: RoutineViewStyle = RoutineViewStyle.DECK
}
