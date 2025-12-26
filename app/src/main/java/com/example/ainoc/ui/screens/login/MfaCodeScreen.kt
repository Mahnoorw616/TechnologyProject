package com.example.ainoc.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ainoc.ui.navigation.Screen
import com.example.ainoc.ui.theme.AccentBeige
import com.example.ainoc.ui.theme.PrimaryPurple
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
                title = { Text("Enter Verification Code") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Check your Authenticator App", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Text("Enter the 6-digit code for your account: ${uiState.mfaEmail}", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.mfaCode,
                onValueChange = { viewModel.onMfaCodeChange(it) },
                label = { Text("6-Digit Code") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = AccentBeige,
                    unfocusedTextColor = AccentBeige,
                    cursorColor = PrimaryPurple,
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = AccentBeige.copy(alpha = 0.5f)
                )
            )

            Spacer(Modifier.height(8.dp))

            val resendButtonEnabled = !uiState.isResendTimerRunning && mfaCodeResource !is Resource.Loading
            TextButton(
                onClick = { viewModel.sendMfaCode(isResend = true) }, // Corrected call
                enabled = resendButtonEnabled
            ) {
                Text(if (resendButtonEnabled) "Send Code Again" else "Resend available in ${uiState.resendTimerSeconds}s")
            }

            Spacer(Modifier.height(16.dp))

            if (mfaCodeResource is Resource.Loading) {
                CircularProgressIndicator(color = PrimaryPurple)
            } else {
                Button(
                    onClick = { viewModel.verifyMfaCode() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = uiState.mfaCode.length == 6
                ) {
                    Text("Verify & Login")
                }
            }
        }
    }
}