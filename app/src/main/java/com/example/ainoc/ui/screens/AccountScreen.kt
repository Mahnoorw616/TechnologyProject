package com.example.ainoc.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ainoc.ui.theme.AccentBeige // Note: These are aliases for compatibility
import com.example.ainoc.ui.theme.BackgroundDark

// This screen shows the profile information of the current user.
// It is accessed from the Settings or Profile menu.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            // Standard top bar with a Back button.
            TopAppBar(
                title = { Text("Account Details", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                // Makes the top bar transparent so the background color/gradient shows through.
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        // Main content area with padding to avoid being covered by the top bar.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Section Header
            Text("User Information", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(16.dp))

            // Displays specific user details using a helper function.
            AccountDetailRow("Name:", "Network Admin")
            AccountDetailRow("Email:", "administrator@company.com")
            AccountDetailRow("Role:", "Administrator")
            AccountDetailRow("Member Since:", "Jan 20, 2024")
        }
    }
}

// A simple helper function to draw a row with a Label (e.g., "Name:") and a Value (e.g., "John").
// This keeps the main code clean and reusable.
@Composable
private fun AccountDetailRow(label: String, value: String) {
    Row(Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            modifier = Modifier.width(120.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Text(text = value, color = MaterialTheme.colorScheme.onBackground)
    }
}