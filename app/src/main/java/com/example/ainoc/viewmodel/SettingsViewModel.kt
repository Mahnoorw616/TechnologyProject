package com.example.ainoc.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Represents all settings state
data class SettingsUiState(
    val selectedTheme: ThemeSetting = ThemeSetting.SYSTEM_DEFAULT,
    val notificationsEnabled: Boolean = true,
    val criticalNotifications: Boolean = true,
    val highNotifications: Boolean = true,
    val mediumNotifications: Boolean = false,
    val lowNotifications: Boolean = false,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "10:00 PM",
    val quietHoursEnd: String = "7:00 AM",
    val isRecalibrating: Boolean = false
)

enum class ThemeSetting(val title: String) {
    SYSTEM_DEFAULT("System Default"),
    LIGHT("Light"),
    DARK("Dark")
}

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun onThemeSelected(theme: ThemeSetting) {
        _uiState.update { it.copy(selectedTheme = theme) }
    }

    fun onNotificationToggle(type: String, isEnabled: Boolean) {
        _uiState.update {
            when (type) {
                "master" -> it.copy(notificationsEnabled = isEnabled)
                "critical" -> it.copy(criticalNotifications = isEnabled)
                "high" -> it.copy(highNotifications = isEnabled)
                "medium" -> it.copy(mediumNotifications = isEnabled)
                "low" -> it.copy(lowNotifications = isEnabled)
                else -> it
            }
        }
    }

    fun onQuietHoursToggle(isEnabled: Boolean) {
        _uiState.update { it.copy(quietHoursEnabled = isEnabled) }
    }

    fun recalibrateAi() {
        // In a real app, this would trigger a long-running backend process
        _uiState.update { it.copy(isRecalibrating = true) }
    }
}