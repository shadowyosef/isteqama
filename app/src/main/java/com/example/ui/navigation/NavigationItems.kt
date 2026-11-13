package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.util.AppStrings

enum class AppDestination(val icon: ImageVector) {
    HOME(Icons.Default.Home),
    QURAN(Icons.Default.AutoStories),
    ROUTINES(Icons.Default.Spa),
    LOCKER(Icons.Default.Lock),
    STATS(Icons.Default.BarChart),
    SETTINGS(Icons.Default.Settings);

    fun getTitle(): String = when (this) {
        HOME -> AppStrings.get("الرئيسية", "Home")
        QURAN -> AppStrings.quran
        ROUTINES -> AppStrings.get("الروتين اليومي", "Daily Routines")
        LOCKER -> AppStrings.get("القفل والورد", "Lock & Ward")
        STATS -> AppStrings.stats
        SETTINGS -> AppStrings.settings
    }
}
