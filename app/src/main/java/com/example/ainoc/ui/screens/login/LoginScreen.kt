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
import com.example.ainoc.util.Resource
import com.example.ainoc.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val serverUrl by viewModel.serverUrl.collectAsState()
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val rememberUrl by viewModel.rememberUrl.collectAsState()
    val loginState by viewModel.loginState.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginState) {
        when (loginState) {
            is Resource.Success -> {
                navController.navigate(Screen.MfaEmail.route)
                viewModel.clearLoginError()
            }
            is Resource.Error -> {
                snackbarHostState.showSnackbar(
                    message = (loginState as Resource.Error<Unit>).message ?: "An unknown error occurred",
                    duration = SnackbarDuration.Short
                )
                viewModel.clearLoginError()
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
                modifier = Modifier
                    .size(90.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                "Welcome to AI-NOC",
                style = MaterialTheme.typography.headlineMedium,
                color = AccentBeige
            )
            Spacer(modifier = Modifier.height(32.dp))

            // --- THIS IS THE DEFINITIVE FIX ---
            val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = AccentBeige, // Correct parameter for text color when focused
                unfocusedTextColor = AccentBeige, // Correct parameter for text color when unfocused
                cursorColor = PrimaryPurple,
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = AccentBeige.copy(alpha = 0.5f),
                focusedLabelColor = PrimaryPurple,
                unfocusedLabelColor = AccentBeige.copy(alpha = 0.7f),
                focusedTrailingIconColor = PrimaryPurple,
                unfocusedTrailingIconColor = AccentBeige.copy(alpha = 0.7f)
            )

            OutlinedTextField(value = serverUrl, onValueChange = viewModel::onServerUrlChange, label = { Text("Server URL") }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = textFieldColors)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = username, onValueChange = viewModel::onUsernameChange, label = { Text("Username / Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = textFieldColors)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                    }
                },
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = rememberUrl,
                    onCheckedChange = viewModel::onRememberUrlChange,
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryPurple)
                )
                Text("Remember Server URL", modifier = Modifier.padding(start = 8.dp), color = AccentBeige)
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (loginState is Resource.Loading) {
                CircularProgressIndicator(color = PrimaryPurple)
            } else {
                Button(
                    onClick = { viewModel.loginUser() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple,
                        contentColor = AccentBeige
                    )
                ) {
                    Text("Login")
                }
            }

            TextButton(onClick = { navController.navigate(Screen.ResetPassword.route) }) {
                Text("Forgot Password?", color = PrimaryPurple)
            }
        }
    }
}