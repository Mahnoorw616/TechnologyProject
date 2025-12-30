package com.example.ainoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainoc.data.local.SessionManager
import com.example.ainoc.ui.theme.ThemeSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Holds the current state of all settings switches and options.
data class SettingsUiState(
    val selectedTheme: ThemeSetting = ThemeSetting.DARK,
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

// This ViewModel manages the Settings screen.
// It saves changes (like theme selection) to persistent storage.
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    // When the app starts, this watches the saved theme setting.
    // If the theme changes, it updates the UI state immediately.
    init {
        viewModelScope.launch {
            sessionManager.appTheme.collectLatest { themeString ->
                val theme = try {
                    ThemeSetting.valueOf(themeString)
                } catch (e: Exception) {
                    ThemeSetting.DARK
                }
                _uiState.update { it.copy(selectedTheme = theme) }
            }
        }
    }

    // Saves the new theme choice to storage.
    fun onThemeSelected(theme: ThemeSetting) {
        viewModelScope.launch {
            sessionManager.saveTheme(theme.name)
        }
        // The state updates automatically via the watcher in 'init'.
    }

    // Toggles various notification settings on or off.
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

    // Simulates a long-running AI calibration task.
    fun recalibrateAi() {
        _uiState.update { it.copy(isRecalibrating = true) }
        viewModelScope.launch {
            delay(2000)
            _uiState.update { it.copy(isRecalibrating = false) }
        }
    }
}