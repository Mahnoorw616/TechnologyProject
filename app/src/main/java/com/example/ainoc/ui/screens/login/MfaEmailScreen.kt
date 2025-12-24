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
import com.example.ainoc.ui.navigation.Screen
import com.example.ainoc.ui.theme.AccentBeige
import com.example.ainoc.ui.theme.PrimaryPurple
import com.example.ainoc.util.Resource
import com.example.ainoc.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MfaEmailScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val mfaEmail by viewModel.mfaEmail.collectAsState()
    val mfaEmailState by viewModel.mfaEmailState.collectAsState()

    LaunchedEffect(mfaEmailState) {
        if (mfaEmailState is Resource.Success) {
            navController.navigate(Screen.MfaCode.route)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Two-Factor Authentication") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            Text("Verify Your Identity", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Text("For your security, we will send a code to your authenticator app linked to this email.", textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = mfaEmail,
                onValueChange = viewModel::onMfaEmailChange,
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = mfaEmailState !is Resource.Loading,
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

            if (mfaEmailState is Resource.Loading) {
                CircularProgressIndicator(color = PrimaryPurple)
            } else {
                Button(
                    onClick = { viewModel.sendMfaCode() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = mfaEmail.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text("Send Verification Code")
                }
            }
        }
    }
}