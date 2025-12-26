package com.example.ainoc.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ainoc.R
import com.example.ainoc.ui.navigation.Screen
import com.example.ainoc.ui.theme.*
import com.example.ainoc.util.NoRippleInteractionSource
import com.example.ainoc.viewmodel.SettingsViewModel
import com.example.ainoc.viewmodel.ThemeSetting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    contentNavController: NavController,
    mainNavController: NavController
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            containerColor = CardBackground,
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out?", color = AccentBeige) },
            text = { Text("Are you sure you want to log out of your session?", color = AccentBeige.copy(alpha = 0.8f)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        // Use mainNavController to go back to Login (Root Graph)
                        mainNavController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    interactionSource = remember { NoRippleInteractionSource() }
                ) {
                    Text("Log Out", color = CriticalRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = AccentBeige)
                }
            }
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item { SettingsSection("ACCOUNT") }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { NoRippleInteractionSource() },
                        indication = null,
                        onClick = { contentNavController.navigate(Screen.ProfileAndSecurity.route) }
                    ),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AA", color = AccentBeige, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Admin AI NOC", fontWeight = FontWeight.Bold, color = AccentBeige)
                        Text("Administrator", color = PrimaryPurple, fontSize = 14.sp)
                    }
                    Icon(Icons.Default.ChevronRight, "Navigate", tint = AccentBeige)
                }
            }
        }

        item { SettingsSection("PREFERENCES") }
        item { SettingsRow("Theme", Icons.Default.DarkMode, value = "Dark") { contentNavController.navigate(Screen.ThemeSettings.route) } }
        item { SettingsRow("Notifications", Icons.Default.Notifications) { contentNavController.navigate(Screen.NotificationSettings.route) } }

        item { SettingsSection("ADMINISTRATION") }
        item { SettingsRow("AI Management", Icons.Default.AutoAwesome) { contentNavController.navigate(Screen.AiManagement.route) } }
        item { SettingsRow("Asset Management", Icons.Default.Dns) { contentNavController.navigate(Screen.Explorer.route) } }
        item { SettingsRow("Maintenance Windows", Icons.Default.CalendarMonth) { contentNavController.navigate(Screen.MaintenanceWindows.route) } }

        item { SettingsSection("APPLICATION") }
        item { SettingsRow("About", Icons.Default.Info, value = "v1.0.0") { contentNavController.navigate(Screen.About.route) } }

        item {
            Box(Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
                TextButton(
                    onClick = { showLogoutDialog = true },
                    interactionSource = remember { NoRippleInteractionSource() }
                ) {
                    Text("Log Out", color = CriticalRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String) {
    Text(
        text = title,
        color = AccentBeige.copy(alpha = 0.7f),
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun SettingsRow(title: String, icon: ImageVector, value: String? = null, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { NoRippleInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = AccentBeige)
            Spacer(Modifier.width(16.dp))
            Text(title, color = AccentBeige, modifier = Modifier.weight(1f))
            value?.let {
                Text(it, color = PrimaryPurple, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
            }
            Icon(Icons.Default.ChevronRight, "Navigate", tint = AccentBeige)
        }
    }
}

/* ---------------- SUB-SCREENS ---------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndSecurityScreen(navController: NavController) {
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Profile & Security", color = AccentBeige) },
                navigationIcon = { BackButton(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("User Details", style = MaterialTheme.typography.titleLarge, color = AccentBeige)
            DetailRow("Username:", "Admin AI NOC")
            DetailRow("Email:", "adminainoc@gmail.com")
            DetailRow("Role:", "Administrator")
            HorizontalDivider(color = CardBackground)
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) { Text("Change Password", color = AccentBeige) }
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) { Text("Manage MFA", color = AccentBeige) }
            HorizontalDivider(color = CardBackground)
            Text("Active Sessions", style = MaterialTheme.typography.titleLarge, color = AccentBeige)
            Text("This Device - Active Now", color = AccentBeige)
            Text("Chrome on Windows - 2 hours ago", color = AccentBeige.copy(alpha = 0.6f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Theme", color = AccentBeige) },
                navigationIcon = { BackButton(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            ThemeSetting.values().forEach { theme ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { NoRippleInteractionSource() },
                            indication = null
                        ) { viewModel.onThemeSelected(theme) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = uiState.selectedTheme == theme,
                        onClick = { viewModel.onThemeSelected(theme) },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryPurple)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(theme.title, color = AccentBeige)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Notifications", color = AccentBeige) },
                navigationIcon = { BackButton(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp)) {
            item {
                NotificationToggleRow("Enable Push Notifications", uiState.notificationsEnabled) { viewModel.onNotificationToggle("master", it) }
                HorizontalDivider(color = CardBackground, modifier = Modifier.padding(vertical = 8.dp))
                NotificationToggleRow("Critical", uiState.criticalNotifications, uiState.notificationsEnabled) { viewModel.onNotificationToggle("critical", it) }
                NotificationToggleRow("High", uiState.highNotifications, uiState.notificationsEnabled) { viewModel.onNotificationToggle("high", it) }
                NotificationToggleRow("Medium", uiState.mediumNotifications, uiState.notificationsEnabled) { viewModel.onNotificationToggle("medium", it) }
                NotificationToggleRow("Low", uiState.lowNotifications, uiState.notificationsEnabled) { viewModel.onNotificationToggle("low", it) }
                HorizontalDivider(color = CardBackground, modifier = Modifier.padding(vertical = 8.dp))
                NotificationToggleRow("Quiet Hours", uiState.quietHoursEnabled) { viewModel.onQuietHoursToggle(it) }
                if (uiState.quietHoursEnabled) {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "From ${uiState.quietHoursStart} to ${uiState.quietHoursEnd}",
                            color = AccentBeige.copy(alpha = 0.8f)
                        )
                        TextButton(onClick = {}) { Text("Edit", color = PrimaryPurple) }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(title: String, isChecked: Boolean, enabled: Boolean = true, onToggle: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(title, modifier = Modifier.weight(1f), color = AccentBeige)
        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            enabled = enabled,
            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryPurple)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiManagementScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            containerColor = CardBackground,
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Recalibration", color = AccentBeige) },
            text = { Text("This will retrain the anomaly detection baseline. This may take a few minutes.", color = AccentBeige.copy(alpha = 0.8f)) },
            confirmButton = { TextButton(onClick = { viewModel.recalibrateAi(); showDialog = false }) { Text("Continue", color = PrimaryPurple) } },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel", color = AccentBeige) } }
        )
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("AI Management", color = AccentBeige) },
                navigationIcon = { BackButton(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Feedback collected for 5 alerts.", color = AccentBeige)
            Button(
                onClick = { showDialog = true },
                enabled = !uiState.isRecalibrating,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                if (uiState.isRecalibrating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AccentBeige, strokeWidth = 2.dp)
                } else {
                    Text("Recalibrate AI Baseline", color = AccentBeige)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceWindowsScreen(navController: NavController) {
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Maintenance Windows", color = AccentBeige) },
                navigationIcon = { BackButton(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}, containerColor = PrimaryPurple) {
                Icon(Icons.Default.Add, "Schedule Maintenance", tint = AccentBeige)
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("Maintenance Scheduling UI Placeholder", color = AccentBeige)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("About AI NOC", color = AccentBeige) },
                navigationIcon = { BackButton(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.ai_noc_logo), contentDescription = "App Logo", modifier = Modifier.size(100.dp))
            Spacer(Modifier.height(16.dp))
            Text("AI NOC", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = AccentBeige)
            Text("v1.0.0", color = AccentBeige.copy(alpha = 0.7f))
            Spacer(Modifier.height(24.dp))
            Text(
                "AI-NOC: Intelligent Network Oversight is a next-generation tool designed for modern network administrators. It leverages machine learning to provide proactive monitoring, intelligent alerting, and deep forensic analysis.",
                textAlign = TextAlign.Center,
                color = AccentBeige
            )
        }
    }
}

@Composable
private fun BackButton(navController: NavController) {
    IconButton(onClick = { navController.popBackStack() }) {
        Icon(Icons.Default.ArrowBack, "Back", tint = AccentBeige)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.padding(vertical = 8.dp)) {
        Text(label, modifier = Modifier.width(150.dp), fontWeight = FontWeight.Bold, color = AccentBeige.copy(alpha = 0.7f))
        Text(value, color = AccentBeige)
    }
}