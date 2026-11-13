package com.example.util

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay

@Composable
fun OnLifecycleResumeEffect(onResume: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val windowInfo = LocalWindowInfo.current
    val currentOnResume by rememberUpdatedState(onResume)
    var refreshTrigger by remember { mutableIntStateOf(0) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentOnResume()
                refreshTrigger++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(windowInfo.isWindowFocused, refreshTrigger) {
        if (windowInfo.isWindowFocused || refreshTrigger > 0) {
            currentOnResume()
            delay(350)
            currentOnResume()
            delay(450)
            currentOnResume()
        }
    }
}
