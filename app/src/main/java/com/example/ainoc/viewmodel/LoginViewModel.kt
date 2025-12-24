package com.example.ainoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainoc.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginResponse(val token: String)

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    // --- State for Login Screen ---
    private val _serverUrl = MutableStateFlow("https://ainoc.yourcompany.com")
    val serverUrl = _serverUrl.asStateFlow()
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    private val _rememberUrl = MutableStateFlow(true)
    val rememberUrl = _rememberUrl.asStateFlow()
    private val _loginState = MutableStateFlow<Resource<Unit>>(Resource.Idle())
    val loginState = _loginState.asStateFlow()
    private val _passwordVisible = MutableStateFlow(false) // State for password visibility
    val passwordVisible = _passwordVisible.asStateFlow()

    // --- State for MFA Email Screen ---
    private val _mfaEmail = MutableStateFlow("")
    val mfaEmail = _mfaEmail.asStateFlow()
    private val _mfaEmailState = MutableStateFlow<Resource<Unit>>(Resource.Idle())
    val mfaEmailState = _mfaEmailState.asStateFlow()

    // --- State for MFA Code Screen ---
    private val _mfaCode = MutableStateFlow("")
    val mfaCode = _mfaCode.asStateFlow()
    private val _mfaCodeState = MutableStateFlow<Resource<LoginResponse>>(Resource.Idle())
    val mfaCodeState = _mfaCodeState.asStateFlow()

    // --- State for Reset Password Screen ---
    private val _resetEmail = MutableStateFlow("")
    val resetEmail = _resetEmail.asStateFlow()
    private val _resetPasswordState = MutableStateFlow<Resource<Unit>>(Resource.Idle())
    val resetPasswordState = _resetPasswordState.asStateFlow()

    // --- Event Handlers ---
    fun onServerUrlChange(newValue: String) { _serverUrl.value = newValue }
    fun onUsernameChange(newValue: String) { _username.value = newValue }
    fun onPasswordChange(newValue: String) { _password.value = newValue }
    fun onRememberUrlChange(newValue: Boolean) { _rememberUrl.value = newValue }
    fun onMfaEmailChange(newValue: String) { _mfaEmail.value = newValue }
    fun onMfaCodeChange(newValue: String) { _mfaCode.value = newValue }
    fun onResetEmailChange(newValue: String) { _resetEmail.value = newValue }
    fun togglePasswordVisibility() { _passwordVisible.value = !_passwordVisible.value }
    fun clearLoginError() { _loginState.value = Resource.Idle() }

    fun loginUser() {
        if (username.value.isBlank() || password.value.isBlank()) {
            _loginState.value = Resource.Error("Please enter a username and password.")
            return
        }
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            delay(1500)
            _mfaEmail.value = username.value // Pre-fill MFA email with username
            _loginState.value = Resource.Success(Unit)
        }
    }

    fun sendMfaCode() {
        viewModelScope.launch {
            _mfaEmailState.value = Resource.Loading()
            delay(1500) // Simulate sending code to authenticator
            _mfaEmailState.value = Resource.Success(Unit)
        }
    }

    fun verifyMfaCode() {
        viewModelScope.launch {
            _mfaCodeState.value = Resource.Loading()
            delay(1500)
            val fakeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ik5ldHdvcmt"
            _mfaCodeState.value = Resource.Success(LoginResponse(fakeToken))
        }
    }

    fun sendPasswordResetLink() {
        viewModelScope.launch {
            _resetPasswordState.value = Resource.Loading()
            delay(2000)
            _resetPasswordState.value = Resource.Success(Unit)
        }
    }
}