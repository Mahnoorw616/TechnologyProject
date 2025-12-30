package com.example.ainoc.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainoc.data.model.User
import com.example.ainoc.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

// Represents the state for the ENTIRE authentication flow
data class AuthUiState(
    // Login State
    val serverUrl: String = "https://ainoc.yourcompany.com",
    val username: String = "",
    val password: String = "",
    val rememberUrl: Boolean = true,
    val passwordVisible: Boolean = false,
    val loginResource: Resource<Unit> = Resource.Idle(),

    // MFA State
    val mfaEmail: String = "",
    val mfaEmailResource: Resource<Unit> = Resource.Idle(),
    val isMfaEmailValid: Boolean = false,
    val mfaCode: String = "",
    val mfaCodeResource: Resource<User> = Resource.Idle(),
    val canResendCode: Boolean = false,

    val resendTimerSeconds: Int = 30,
    val isResendTimerRunning: Boolean = false,

    // Reset Password State
    val resetEmail: String = "",
    val resetPasswordResource: Resource<Unit> = Resource.Idle()
)

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private var generatedMfaCode: String? = null
    private var timerJob: Job? = null

    // --- Event Handlers for UI ---
    fun onServerUrlChange(newValue: String) { _uiState.update { it.copy(serverUrl = newValue) } }
    fun onUsernameChange(newValue: String) { _uiState.update { it.copy(username = newValue) } }
    fun onPasswordChange(newValue: String) { _uiState.update { it.copy(password = newValue) } }
    fun onRememberUrlChange(newValue: Boolean) { _uiState.update { it.copy(rememberUrl = newValue) } }
    fun togglePasswordVisibility() { _uiState.update { it.copy(passwordVisible = !it.passwordVisible) } }

    fun onMfaEmailChange(newValue: String) {
        _uiState.update {
            it.copy(
                mfaEmail = newValue,
                isMfaEmailValid = Patterns.EMAIL_ADDRESS.matcher(newValue).matches()
            )
        }
    }

    fun onMfaCodeChange(newValue: String) {
        if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
            _uiState.update {
                it.copy(
                    mfaCode = newValue,
                    // Reset error state immediately when user starts typing again
                    mfaCodeResource = Resource.Idle()
                )
            }
        }
    }

    fun onResetEmailChange(newValue: String) { _uiState.update { it.copy(resetEmail = newValue) } }

    /**
     * Call this from the UI to reset a navigation-triggering state.
     */
    fun consumeLoginEvent() { _uiState.update { it.copy(loginResource = Resource.Idle()) } }
    fun consumeMfaEmailEvent() { _uiState.update { it.copy(mfaEmailResource = Resource.Idle()) } }

    fun loginUser() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.username.isBlank() || state.password.isBlank()) {
                _uiState.update { it.copy(loginResource = Resource.Error("Please enter username and password.")) }
                return@launch
            }

            _uiState.update { it.copy(loginResource = Resource.Loading()) }
            delay(1500)

            if (state.username.equals("adminainoc@gmail.com", ignoreCase = true) && state.password == "password") {
                _uiState.update { it.copy(
                    loginResource = Resource.Success(Unit),
                    mfaEmail = state.username,
                    isMfaEmailValid = true
                )}
            } else {
                _uiState.update { it.copy(loginResource = Resource.Error("Invalid credentials.")) }
            }
        }
    }

    /**
     * Simulates sending a new MFA code and starts the resend timer.
     */
    fun sendMfaCode(isResend: Boolean = false) {
        timerJob?.cancel()
        viewModelScope.launch {
            _uiState.update { it.copy(mfaEmailResource = Resource.Loading()) }

            // Artificial delay to simulate network request
            delay(1500)

            generatedMfaCode = String.format("%06d", Random.nextInt(100_000, 999_999))
            println("AI-NOC Verification Code for ${_uiState.value.mfaEmail}: $generatedMfaCode")

            if (!isResend) {
                // Initial send: Trigger navigation
                _uiState.update { it.copy(mfaEmailResource = Resource.Success(Unit)) }
            } else {
                // Resend: Stay on screen, just stop loading and reset logic
                _uiState.update { it.copy(mfaEmailResource = Resource.Idle()) }
            }

            startResendTimer()
        }
    }

    private fun startResendTimer() {
        timerJob = viewModelScope.launch {
            _uiState.update { it.copy(isResendTimerRunning = true, resendTimerSeconds = 30, canResendCode = false) }
            for (i in 30 downTo 1) {
                delay(1000)
                _uiState.update { it.copy(resendTimerSeconds = i - 1) }
            }
            _uiState.update { it.copy(isResendTimerRunning = false, canResendCode = true) }
        }
    }

    fun verifyMfaCode() {
        viewModelScope.launch {
            _uiState.update { it.copy(mfaCodeResource = Resource.Loading()) }

            // Check code
            if (_uiState.value.mfaCode == generatedMfaCode) {
                // SUCCESS: No delay here, proceed immediately
                val user = User(username = "Admin AI NOC", email = _uiState.value.mfaEmail)
                _uiState.update { it.copy(mfaCodeResource = Resource.Success(user)) }
            } else {
                // FAILURE: Small delay to simulate server check, then show error
                delay(500)
                _uiState.update {
                    it.copy(mfaCodeResource = Resource.Error("Incorrect code. Please try again."))
                }
                // We DO NOT auto-clear the error here. It persists so the user can read it.
                // It clears in onMfaCodeChange when they type.
            }
        }
    }

    fun sendPasswordResetLink() {
        viewModelScope.launch {
            _uiState.update { it.copy(resetPasswordResource = Resource.Loading()) }
            delay(2000)
            _uiState.update { it.copy(resetPasswordResource = Resource.Success(Unit)) }
        }
    }
}