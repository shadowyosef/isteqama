package com.example.ui.setup

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppAppearancePreferences
import com.example.data.AppPreferencesManager
import com.example.ui.common.WizardStepFooter
import com.example.ui.common.WizardStepProgressHeader
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.IslamicOrnamentBackground

@Composable
fun FirstRunSetupScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic by AppPreferencesManager.isArabic.collectAsState()
    val currentTheme by AppAppearancePreferences.themeStyle.collectAsState()
    val customTheme by AppAppearancePreferences.customTheme.collectAsState()
    val patOverride by AppAppearancePreferences.patternOverride.collectAsState()
    val patternStyle = AppAppearancePreferences.resolvePattern(currentTheme, patOverride, customTheme)
    val patternOpacity = if (currentTheme == AppThemeStyle.CUSTOM_STUDIO) customTheme.patternOpacity else 0.20f
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 6

    val titlesAr = listOf("اللغة والموقع", "الأذونات الأساسية", "المظهر والخطوط", "الأوراد والأسلوب", "الرقم السري", "التطبيقات والحدود")
    val titlesEn = listOf("Language & City", "Core Permissions", "Theme & Fonts", "Defaults & Style", "Security PIN", "Exempt Apps & Limits")

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        IslamicOrnamentBackground(patternStyle = patternStyle, patternOpacity = patternOpacity)
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                Surface(tonalElevation = 6.dp, shadowElevation = 8.dp) {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.widthIn(max = 600.dp)) {
                            WizardStepFooter(
                                currentStep = currentStep,
                                totalSteps = totalSteps,
                                isArabic = isArabic,
                                onCancelOrBack = {
                                    if (currentStep > 0) currentStep-- else {
                                        AppPreferencesManager.setFirstRunSetupCompleted(true)
                                        onComplete()
                                    }
                                },
                                onNext = { if (currentStep < totalSteps - 1) currentStep++ },
                                onFinish = {
                                    AppPreferencesManager.setFirstRunSetupCompleted(true)
                                    onComplete()
                                },
                                finishLabel = if (isArabic) "بدء رحلة استقامة" else "Start Istaqama",
                                finishTestTag = "finish_setup_btn"
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxSize()
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                        .verticalScroll(rememberScrollState())
                        .testTag("first_run_setup_screen"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            (0 until totalSteps).forEach { idx ->
                                FilterChip(
                                    selected = currentStep == idx,
                                    onClick = { currentStep = idx },
                                    label = { Text("${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("setup_step_chip_${idx + 1}")
                                )
                            }
                        }
                        TextButton(
                            onClick = {
                                AppPreferencesManager.setFirstRunSetupCompleted(true)
                                onComplete()
                            },
                            modifier = Modifier.heightIn(min = 48.dp).testTag("skip_setup_btn")
                        ) {
                            Text(if (isArabic) "تخطي الآن" else "Skip", fontSize = 12.sp)
                        }
                    }

                    WizardStepProgressHeader(
                        currentStep = currentStep,
                        totalSteps = totalSteps,
                        stepTitle = if (isArabic) titlesAr[currentStep] else titlesEn[currentStep],
                        isArabic = isArabic
                    )

                    when (currentStep) {
                        0 -> SetupStepWelcomeLanguage()
                        1 -> SetupStepPermissions()
                        2 -> SetupStepAppearance()
                        3 -> SetupStepLocksDefaults()
                        4 -> SetupStepSecurityPin()
                        else -> SetupStepExemptAndLimits()
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
