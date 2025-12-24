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
import com.example.ainoc.ui.theme.AccentBeige
import com.example.ainoc.ui.theme.BackgroundDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Details", color = AccentBeige) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = AccentBeige)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("User Information", style = MaterialTheme.typography.headlineSmall, color = AccentBeige)
            Spacer(Modifier.height(16.dp))
            AccountDetailRow("Name:", "Network Admin")
            AccountDetailRow("Email:", "administrator@company.com")
            AccountDetailRow("Role:", "Administrator")
            AccountDetailRow("Member Since:", "Jan 20, 2024")
        }
    }
}

@Composable
private fun AccountDetailRow(label: String, value: String) {
    Row(Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            modifier = Modifier.width(120.dp),
            fontWeight = FontWeight.Bold,
            color = AccentBeige.copy(alpha = 0.7f)
        )
        Text(text = value, color = AccentBeige)
    }
}