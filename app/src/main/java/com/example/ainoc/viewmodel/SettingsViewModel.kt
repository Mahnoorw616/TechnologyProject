package com.example.ainoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainoc.data.local.SessionManager
import com.example.ainoc.ui.theme.ThemeSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

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

    fun onThemeSelected(theme: ThemeSetting) {
        viewModelScope.launch {
            sessionManager.saveTheme(theme.name)
        }
        // State will update via the collector in init block
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
        _uiState.update { it.copy(isRecalibrating = true) }
        // Simulate operation
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _uiState.update { it.copy(isRecalibrating = false) }
        }
    }
}