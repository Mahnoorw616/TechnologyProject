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

    LaunchedEffect(loginResource) {
        when (loginResource) {
            is Resource.Success -> {
                navController.navigate(Screen.MfaEmail.route)
                viewModel.consumeLoginEvent() // Prevent re-triggering
            }
            is Resource.Error -> {
                snackbarHostState.showSnackbar(
                    message = loginResource.message ?: "An unknown error occurred",
                    duration = SnackbarDuration.Short
                )
                viewModel.consumeLoginEvent() // Also reset after showing error
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
                .background(Brush.verticalGradient(listOf(SplashGradientStart, SplashGradientEnd)))
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

            Text("Welcome to AI-NOC", style = MaterialTheme.typography.headlineMedium, color = AccentBeige)
            Spacer(modifier = Modifier.height(32.dp))

            val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = AccentBeige,
                unfocusedTextColor = AccentBeige,
                cursorColor = PrimaryPurple,
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = AccentBeige.copy(alpha = 0.5f),
                focusedLabelColor = PrimaryPurple,
                unfocusedLabelColor = AccentBeige.copy(alpha = 0.7f),
                focusedTrailingIconColor = PrimaryPurple,
                unfocusedTrailingIconColor = AccentBeige.copy(alpha = 0.7f)
            )

            OutlinedTextField(value = uiState.serverUrl, onValueChange = viewModel::onServerUrlChange, label = { Text("Server URL") }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = textFieldColors)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = uiState.username, onValueChange = viewModel::onUsernameChange, label = { Text("Username / Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = textFieldColors)
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
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
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
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryPurple)
                )
                Text("Remember Server URL", modifier = Modifier.padding(start = 8.dp), color = AccentBeige)
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (loginResource is Resource.Loading) {
                CircularProgressIndicator(color = PrimaryPurple)
            } else {
                Button(
                    onClick = { viewModel.loginUser() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple, contentColor = AccentBeige),
                    interactionSource = remember { NoRippleInteractionSource() }
                ) {
                    Text("Login")
                }
            }
            TextButton(
                onClick = { navController.navigate(Screen.ResetPassword.route) },
                interactionSource = remember { NoRippleInteractionSource() }
            ) {
                Text("Forgot Password?", color = PrimaryPurple)
            }
        }
    }
}