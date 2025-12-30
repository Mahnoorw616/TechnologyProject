package com.example.ainoc.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ainoc.ui.navigation.Screen
import com.example.ainoc.ui.theme.*
import com.example.ainoc.util.Resource
import com.example.ainoc.viewmodel.LoginViewModel

// This screen is where the user enters their email address.
// It's the first step of the "Two-Factor Authentication" process after logging in.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MfaEmailScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    // Watches the live data from the app's brain (ViewModel), like what email is typed.
    val uiState by viewModel.uiState.collectAsState()
    val mfaEmailResource = uiState.mfaEmailResource

    // Decides which background colors to use based on if the phone is in Dark or Light mode.
    val isDark = MaterialTheme.colorScheme.isDark
    val gradientColors = if (isDark) {
        listOf(SplashStartDark, SplashEndDark)
    } else {
        listOf(SplashStartLight, SplashEndLight)
    }

    // Automatically moves to the "Enter Code" screen once the email is successfully sent.
    LaunchedEffect(mfaEmailResource) {
        if (mfaEmailResource is Resource.Success) {
            navController.navigate(Screen.MfaCode.route)
            viewModel.consumeMfaEmailEvent()
        }
    }

    Scaffold(
        topBar = {
            // Standard top bar with a title and a back button.
            TopAppBar(
                title = { Text("Two-Factor Authentication", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                .background(Brush.verticalGradient(colors = gradientColors)) // Applies the colorful background.
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main instructions for the user.
            Text(
                "Verify Your Identity",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "For your security, please enter your email. A verification code will be sent to your authenticator app.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(32.dp))

            // The text box where the user types their email.
            OutlinedTextField(
                value = uiState.mfaEmail,
                onValueChange = viewModel::onMfaEmailChange,
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                // Turns the box red if the email format is wrong.
                isError = uiState.mfaEmail.isNotEmpty() && !uiState.isMfaEmailValid,
                enabled = mfaEmailResource !is Resource.Loading,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            )
            // Shows a helper message below the box if the email is invalid.
            if (uiState.mfaEmail.isNotEmpty() && !uiState.isMfaEmailValid) {
                Text(
                    "Please enter a valid email address",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(Modifier.height(24.dp))

            // Shows a loading circle while sending, or the "Send" button if ready.
            if (mfaEmailResource is Resource.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Button(
                    onClick = { viewModel.sendMfaCode(isResend = false) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = uiState.isMfaEmailValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Send Verification Code")
                }
            }
        }
    }
}