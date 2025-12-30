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
import com.example.ainoc.ui.theme.ThemeSetting // <--- CORRECTED IMPORT
import com.example.ainoc.util.NoRippleInteractionSource
import com.example.ainoc.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    contentNavController: NavController,
    mainNavController: NavController
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out?", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("Are you sure you want to log out of your session?", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
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
                TextButton(
                    onClick = { showLogoutDialog = false },
                    interactionSource = remember { NoRippleInteractionSource() }
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AA", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Admin AI NOC", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Administrator", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    }
                    Icon(Icons.Default.ChevronRight, "Navigate", tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        item { SettingsSection("PREFERENCES") }
        item { SettingsRow("Theme", Icons.Default.DarkMode, value = null) { contentNavController.navigate(Screen.ThemeSettings.route) } }
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
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.width(16.dp))
            Text(title, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            value?.let {
                Text(it, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
            }
            Icon(Icons.Default.ChevronRight, "Navigate", tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

/* ---------------- SUB-SCREENS ---------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndSecurityScreen(navController: NavController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Profile & Security", color = MaterialTheme.colorScheme.onBackground) },
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
            Text("User Details", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            DetailRow("Username:", "Admin AI NOC")
            DetailRow("Email:", "adminainoc@gmail.com")
            DetailRow("Role:", "Administrator")
            HorizontalDivider(color = MaterialTheme.colorScheme.surface)
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                interactionSource = remember { NoRippleInteractionSource() }
            ) { Text("Change Password", color = MaterialTheme.colorScheme.onPrimary) }
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                interactionSource = remember { NoRippleInteractionSource() }
            ) { Text("Manage MFA", color = MaterialTheme.colorScheme.onPrimary) }
            HorizontalDivider(color = MaterialTheme.colorScheme.surface)
            Text("Active Sessions", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            Text("This Device - Active Now", color = MaterialTheme.colorScheme.onBackground)
            Text("Chrome on Windows - 2 hours ago", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Theme", color = MaterialTheme.colorScheme.onBackground) },
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
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary),
                        interactionSource = remember { NoRippleInteractionSource() }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(theme.title, color = MaterialTheme.colorScheme.onBackground)
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Notifications", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = { BackButton(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp)) {
            item {
                NotificationToggleRow("Enable Push Notifications", uiState.notificationsEnabled) { viewModel.onNotificationToggle("master", it) }
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(vertical = 8.dp))
                NotificationToggleRow("Critical", uiState.criticalNotifications, uiState.notificationsEnabled) { viewModel.onNotificationToggle("critical", it) }
                NotificationToggleRow("High", uiState.highNotifications, uiState.notificationsEnabled) { viewModel.onNotificationToggle("high", it) }
                NotificationToggleRow("Medium", uiState.mediumNotifications, uiState.notificationsEnabled) { viewModel.onNotificationToggle("medium", it) }
                NotificationToggleRow("Low", uiState.lowNotifications, uiState.notificationsEnabled) { viewModel.onNotificationToggle("low", it) }
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(vertical = 8.dp))
                NotificationToggleRow("Quiet Hours", uiState.quietHoursEnabled) { viewModel.onQuietHoursToggle(it) }
                if (uiState.quietHoursEnabled) {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "From ${uiState.quietHoursStart} to ${uiState.quietHoursEnd}",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                        TextButton(
                            onClick = {},
                            interactionSource = remember { NoRippleInteractionSource() }
                        ) { Text("Edit", color = MaterialTheme.colorScheme.primary) }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(title, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground)

        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                uncheckedTrackColor = MaterialTheme.colorScheme.surface
            ),
            interactionSource = remember { NoRippleInteractionSource() }
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
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Recalibration", color = MaterialTheme.colorScheme.onBackground) },
            text = { Text("This will retrain the anomaly detection baseline. This may take a few minutes.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.recalibrateAi(); showDialog = false },
                    interactionSource = remember { NoRippleInteractionSource() }
                ) { Text("Continue", color = MaterialTheme.colorScheme.primary) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    interactionSource = remember { NoRippleInteractionSource() }
                ) { Text("Cancel", color = MaterialTheme.colorScheme.onBackground) }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("AI Management", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = { BackButton(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Feedback collected for 5 alerts.", color = MaterialTheme.colorScheme.onBackground)
            Button(
                onClick = { showDialog = true },
                enabled = !uiState.isRecalibrating,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                interactionSource = remember { NoRippleInteractionSource() }
            ) {
                if (uiState.isRecalibrating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Recalibrate AI Baseline", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceWindowsScreen(navController: NavController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Maintenance Windows", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = { BackButton(navController) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = MaterialTheme.colorScheme.primary,
                interactionSource = remember { NoRippleInteractionSource() }
            ) {
                Icon(Icons.Default.Add, "Schedule Maintenance", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("Maintenance Scheduling UI Placeholder", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("About AI NOC", color = MaterialTheme.colorScheme.onBackground) },
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
            Text("AI NOC", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text("v1.0.0", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            Spacer(Modifier.height(24.dp))
            Text(
                "AI-NOC: Intelligent Network Oversight is a next-generation tool designed for modern network administrators. It leverages machine learning to provide proactive monitoring, intelligent alerting, and deep forensic analysis.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun BackButton(navController: NavController) {
    IconButton(
        onClick = { navController.popBackStack() },
        interactionSource = remember { NoRippleInteractionSource() }
    ) {
        Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.padding(vertical = 8.dp)) {
        Text(label, modifier = Modifier.width(150.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        Text(value, color = MaterialTheme.colorScheme.onBackground)
    }
}