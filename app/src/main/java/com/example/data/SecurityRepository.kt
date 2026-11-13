package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow

enum class SecurityType {
    PIN_4_DIGIT,
    BIOMETRIC,
    PASSCODE
}

object SecurityRepository {
    val isSecurityEnabled = MutableStateFlow(true)
    val securityPin = MutableStateFlow("1234")
    val securityType = MutableStateFlow(SecurityType.PIN_4_DIGIT)
    val adminLockEnforced = MutableStateFlow(true)

    fun verifyPin(input: String): Boolean {
        if (!isSecurityEnabled.value) return true
        return input == securityPin.value
    }

    fun updatePin(newPin: String) {
        if (newPin.isNotBlank()) {
            securityPin.value = newPin
        }
    }

    fun setSecurityType(type: SecurityType) {
        securityType.value = type
    }

    fun toggleSecurity(enabled: Boolean) {
        isSecurityEnabled.value = enabled
    }
}
