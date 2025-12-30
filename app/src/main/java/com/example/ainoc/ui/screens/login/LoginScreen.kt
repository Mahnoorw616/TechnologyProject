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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val loginResource = uiState.loginResource
    val snackbarHostState = remember { SnackbarHostState() }

    // THEME-AWARE GRADIENT
    val isDark = MaterialTheme.colorScheme.isDark
    val gradientColors = if (isDark) {
        // Fix: Use correct variable names from Color.kt
        listOf(SplashStartDark, SplashEndDark)
    } else {
        // Fix: Use correct variable names from Color.kt
        listOf(SplashStartLight, SplashEndLight)
    }

    LaunchedEffect(loginResource) {
        when (loginResource) {
            is Resource.Success -> {
                navController.navigate(Screen.MfaEmail.route)
                viewModel.consumeLoginEvent()
            }
            is Resource.Error -> {
                snackbarHostState.showSnackbar(
                    message = loginResource.message ?: "An unknown error occurred",
                    duration = SnackbarDuration.Short
                )
                viewModel.consumeLoginEvent()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Fix: explicit assignment to 'colors' parameter to avoid ambiguity
                .background(Brush.verticalGradient(colors = gradientColors))
                .padding(paddingValues)
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ai_noc_logo),
                contentDescription = "AI-NOC Logo",
                modifier = Modifier.size(90.dp).padding(bottom = 16.dp)
            )

            Text("Welcome to AI-NOC", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(32.dp))

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

            OutlinedTextField(
                value = uiState.serverUrl,
                onValueChange = viewModel::onServerUrlChange,
                label = { Text("Server URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                label = { Text("Username / Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))
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
            TextButton(
                onClick = { navController.navigate(Screen.ResetPassword.route) },
                interactionSource = remember { NoRippleInteractionSource() }
            ) {
                Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}