package com.example.ainoc.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ainoc.ui.navigation.Screen
import com.example.ainoc.ui.theme.*
import com.example.ainoc.util.NoRippleInteractionSource
import com.example.ainoc.util.Resource
import com.example.ainoc.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MfaCodeScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val mfaCodeResource = uiState.mfaCodeResource
    val snackbarHostState = remember { SnackbarHostState() }

    // Dynamic Gradient
    val isDark = MaterialTheme.colorScheme.isDark
    val gradientColors = if (isDark) {
        listOf(SplashStartDark, SplashEndDark)
    } else {
        listOf(SplashStartLight, SplashEndLight)
    }

    LaunchedEffect(mfaCodeResource) {
        if (mfaCodeResource is Resource.Success) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        } else if (mfaCodeResource is Resource.Error) {
            snackbarHostState.showSnackbar(mfaCodeResource.message ?: "An error occurred")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Enter Verification Code", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        interactionSource = remember { NoRippleInteractionSource() }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
                .padding(paddingValues)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Check your Authenticator App",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Enter the 6-digit code for your account: ${uiState.mfaEmail}",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(32.dp))

            val isError = mfaCodeResource is Resource.Error

            OutlinedTextField(
                value = uiState.mfaCode,
                onValueChange = { viewModel.onMfaCodeChange(it) },
                label = { Text("6-Digit Code") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = isError,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            )

            if (isError) {
                Text(
                    text = mfaCodeResource.message ?: "Incorrect code",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            val resendButtonEnabled = !uiState.isResendTimerRunning && mfaCodeResource !is Resource.Loading
            TextButton(
                onClick = { viewModel.sendMfaCode(isResend = true) },
                enabled = resendButtonEnabled,
                interactionSource = remember { NoRippleInteractionSource() }
            ) {
                Text(
                    text = if (uiState.isResendTimerRunning) "Resend available in ${uiState.resendTimerSeconds}s" else "Send Code Again",
                    color = if (resendButtonEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Spacer(Modifier.height(16.dp))

            if (mfaCodeResource is Resource.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Button(
                    onClick = { viewModel.verifyMfaCode() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = uiState.mfaCode.length == 6,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    interactionSource = remember { NoRippleInteractionSource() }
                ) {
                    Text("Verify & Login")
                }
            }
        }
    }
}