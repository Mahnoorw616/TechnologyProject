package com.example.ainoc.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ainoc.R
import com.example.ainoc.ui.navigation.Screen
import com.example.ainoc.ui.theme.*
import com.example.ainoc.util.NoRippleInteractionSource
import com.example.ainoc.util.Resource
import com.example.ainoc.viewmodel.LoginViewModel

// This is the first screen the user sees after the splash screen.
// It allows users to enter their credentials to sign in.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    // These variables keep track of what the user is typing and if the login is loading or failed.
    val uiState by viewModel.uiState.collectAsState()
    val loginResource = uiState.loginResource
    val snackbarHostState = remember { SnackbarHostState() }

    // Chooses the background color gradient based on whether the phone is in Dark or Light mode.
    val isDark = MaterialTheme.colorScheme.isDark
    val gradientColors = if (isDark) {
        listOf(SplashStartDark, SplashEndDark)
    } else {
        listOf(SplashStartLight, SplashEndLight)
    }

    // Automatically moves to the next screen (2FA Email) if login is successful.
    LaunchedEffect(loginResource) {
        when (loginResource) {
            is Resource.Success -> {
                navController.navigate(Screen.MfaEmail.route)
                viewModel.consumeLoginEvent()
            }
            is Resource.Error -> {
                // Shows a small popup message if something goes wrong.
                snackbarHostState.showSnackbar(
                    message = loginResource.message ?: "An unknown error occurred",
                    duration = SnackbarDuration.Short
                )
                viewModel.consumeLoginEvent()
            }
            else -> {}
        }
    }

    // Scaffold provides the basic structure for the screen, like where to put the snackbar messages.
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors)) // Applies the colorful background.
                .padding(paddingValues)
                .padding(32.dp)
                .verticalScroll(rememberScrollState()), // Allows scrolling if the screen is small.
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Displays the app logo at the top.
            Image(
                painter = painterResource(id = R.drawable.ai_noc_logo),
                contentDescription = "AI-NOC Logo",
                modifier = Modifier.size(90.dp).padding(bottom = 16.dp)
            )

            // Shows the welcome text.
            Text("Welcome to AI-NOC", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(32.dp))

            // Defines the colors for all text boxes so they look good on the gradient background.
            val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            // Input field for the Server URL.
            OutlinedTextField(
                value = uiState.serverUrl,
                onValueChange = viewModel::onServerUrlChange,
                label = { Text("Server URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Input field for Username or Email.
            OutlinedTextField(
                value = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                label = { Text("Username / Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Input field for Password with a show/hide eye icon.
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (uiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(
                        onClick = { viewModel.togglePasswordVisibility() },
                        interactionSource = remember { NoRippleInteractionSource() }
                    ) {
                        Icon(imageVector = image, contentDescription = if (uiState.passwordVisible) "Hide password" else "Show password")
                    }
                },
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Checkbox to remember the server URL for next time.
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = uiState.rememberUrl,
                    onCheckedChange = { viewModel.onRememberUrlChange(it) },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                    interactionSource = remember { NoRippleInteractionSource() }
                )
                Text("Remember Server URL", modifier = Modifier.padding(start = 8.dp), color = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Shows a spinner if logging in, otherwise shows the Login button.
            if (loginResource is Resource.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Button(
                    onClick = { viewModel.loginUser() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                    interactionSource = remember { NoRippleInteractionSource() }
                ) {
                    Text("Login")
                }
            }

            // Button to go to the password reset screen.
            TextButton(
                onClick = { navController.navigate(Screen.ResetPassword.route) },
                interactionSource = remember { NoRippleInteractionSource() }
            ) {
                Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}