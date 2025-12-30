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

// Holds all the data for the Login, 2FA, and Password Reset screens.
// If any of this data changes (like typing in a text box), the screen updates automatically.
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

// This class handles the logic for logging in.
// It checks passwords, sends 2FA codes, and manages the timers.
@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    // Holds the current state of the login screens.
    private val _uiState = MutableStateFlow(AuthUiState())
    // Allows the screens to watch for changes.
    val uiState = _uiState.asStateFlow()

    private var generatedMfaCode: String? = null
    private var timerJob: Job? = null

    // Functions to update the text fields as the user types.
    fun onServerUrlChange(newValue: String) { _uiState.update { it.copy(serverUrl = newValue) } }
    fun onUsernameChange(newValue: String) { _uiState.update { it.copy(username = newValue) } }
    fun onPasswordChange(newValue: String) { _uiState.update { it.copy(password = newValue) } }
    fun onRememberUrlChange(newValue: Boolean) { _uiState.update { it.copy(rememberUrl = newValue) } }

    // Shows or hides the password text (eye icon).
    fun togglePasswordVisibility() { _uiState.update { it.copy(passwordVisible = !it.passwordVisible) } }

    // Validates the email format as the user types.
    fun onMfaEmailChange(newValue: String) {
        _uiState.update {
            it.copy(
                mfaEmail = newValue,
                isMfaEmailValid = Patterns.EMAIL_ADDRESS.matcher(newValue).matches()
            )
        }
    }

    // Updates the 6-digit code field, ensuring only numbers are entered.
    fun onMfaCodeChange(newValue: String) {
        if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
            _uiState.update {
                it.copy(
                    mfaCode = newValue,
                    // Clears any previous error message as soon as the user starts typing again.
                    mfaCodeResource = Resource.Idle()
                )
            }
        }
    }

    fun onResetEmailChange(newValue: String) { _uiState.update { it.copy(resetEmail = newValue) } }

    // Resets the success status so navigation doesn't trigger again if we come back to the screen.
    fun consumeLoginEvent() { _uiState.update { it.copy(loginResource = Resource.Idle()) } }
    fun consumeMfaEmailEvent() { _uiState.update { it.copy(mfaEmailResource = Resource.Idle()) } }

    // Simulates the login process.
    // Checks if the fields are empty and if the username/password match the dummy data.
    fun loginUser() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.username.isBlank() || state.password.isBlank()) {
                _uiState.update { it.copy(loginResource = Resource.Error("Please enter username and password.")) }
                return@launch
            }

            _uiState.update { it.copy(loginResource = Resource.Loading()) }
            delay(1500) // Fake delay to look like a real network call.

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

    // Generates a random 6-digit code and starts the 30-second countdown timer.
    fun sendMfaCode(isResend: Boolean = false) {
        timerJob?.cancel()
        viewModelScope.launch {
            _uiState.update { it.copy(mfaEmailResource = Resource.Loading()) }

            delay(1500)

            generatedMfaCode = String.format("%06d", Random.nextInt(100_000, 999_999))
            println("AI-NOC Verification Code for ${_uiState.value.mfaEmail}: $generatedMfaCode")

            if (!isResend) {
                // If it's the first time, tell the UI to move to the next screen.
                _uiState.update { it.copy(mfaEmailResource = Resource.Success(Unit)) }
            } else {
                // If resending, just reset the loading state.
                _uiState.update { it.copy(mfaEmailResource = Resource.Idle()) }
            }

            startResendTimer()
        }
    }

    // Counts down from 30 to 0 before allowing the user to request a new code.
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

    // Checks if the entered code matches the one we generated.
    fun verifyMfaCode() {
        viewModelScope.launch {
            _uiState.update { it.copy(mfaCodeResource = Resource.Loading()) }

            // Check code
            if (_uiState.value.mfaCode == generatedMfaCode) {
                // Success: Log the user in immediately.
                val user = User(username = "Admin AI NOC", email = _uiState.value.mfaEmail)
                _uiState.update { it.copy(mfaCodeResource = Resource.Success(user)) }
            } else {
                // Failure: Show an error message.
                delay(500)
                _uiState.update {
                    it.copy(mfaCodeResource = Resource.Error("Incorrect code. Please try again."))
                }
            }
        }
    }

    // Simulates sending a password reset email.
    fun sendPasswordResetLink() {
        viewModelScope.launch {
            _uiState.update { it.copy(resetPasswordResource = Resource.Loading()) }
            delay(2000)
            _uiState.update { it.copy(resetPasswordResource = Resource.Success(Unit)) }
        }
    }
}