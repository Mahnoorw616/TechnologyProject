package com.example.ainoc.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ainoc.ui.theme.AccentBeige
import com.example.ainoc.ui.theme.PrimaryPurple
import com.example.ainoc.util.Resource
import com.example.ainoc.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val resetEmail by viewModel.resetEmail.collectAsState()
    val resetState by viewModel.resetPasswordState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reset Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (resetState is Resource.Success) {
                Text("Success!", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text("If an account exists for $resetEmail, you will receive an email with a link to reset your password.", textAlign = TextAlign.Center)
            } else {
                Text("Enter your account email to receive a password reset link.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = resetEmail,
                    onValueChange = viewModel::onResetEmailChange,
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = resetState !is Resource.Loading,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = AccentBeige,
                        unfocusedTextColor = AccentBeige,
                        cursorColor = PrimaryPurple,
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = AccentBeige.copy(alpha = 0.5f),
                        focusedLabelColor = PrimaryPurple,
                        unfocusedLabelColor = AccentBeige.copy(alpha = 0.7f)
                    )
                )
                Spacer(Modifier.height(24.dp))

                if (resetState is Resource.Loading) {
                    CircularProgressIndicator(color = PrimaryPurple)
                } else {
                    Button(
                        onClick = { viewModel.sendPasswordResetLink() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = resetEmail.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                    ) {
                        Text("Send Reset Link")
                    }
                }
            }
        }
    }
}