package com.example.ui.navigation

import com.example.model.SurahInfo
import kotlinx.coroutines.flow.MutableStateFlow

object NavigationState {
    val currentDestination = MutableStateFlow(AppDestination.HOME)
    val routinesSubTab = MutableStateFlow(0)
    val pendingSurahToOpen = MutableStateFlow<SurahInfo?>(null)
    val openQiblaDialog = MutableStateFlow(false)
}
