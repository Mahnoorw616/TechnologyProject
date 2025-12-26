package com.example.ainoc.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.ainoc.R
import com.example.ainoc.ui.navigation.BottomNavItem
import com.example.ainoc.ui.navigation.Screen
import com.example.ainoc.ui.screens.alerts.AlertDetailsScreen
import com.example.ainoc.ui.screens.alerts.AlertsListScreen
import com.example.ainoc.ui.screens.dashboard.DashboardScreen
import com.example.ainoc.ui.screens.explorer.DeviceDetailsScreen
import com.example.ainoc.ui.screens.explorer.ExplorerScreen
import com.example.ainoc.ui.screens.settings.*
import com.example.ainoc.ui.theme.*
import com.example.ainoc.util.NoRippleInteractionSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(mainNavController: NavController) {
    val contentNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // ---- LOGOUT DIALOG ----
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
                        mainNavController.navigate(Screen.Login.route) {
                            popUpTo(mainNavController.graph.id) { inclusive = true }
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                contentNavController = contentNavController,
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onLogoutClick = {
                    scope.launch { drawerState.close() }
                    showLogoutDialog = true
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("AI NOC", color = AccentBeige, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = AccentBeige)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            // Navigate to profile via content controller
                            contentNavController.navigate(Screen.ProfileAndSecurity.route)
                        }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Account", tint = AccentBeige)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BackgroundDark.copy(alpha = 0.95f)
                    )
                )
            },
            bottomBar = {
                BottomNavigationBar(navController = contentNavController)
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(SplashGradientStart, SplashGradientEnd)))
            ) {
                MainContentNavHost(
                    contentNavController = contentNavController,
                    mainNavController = mainNavController
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Dashboard", Screen.Dashboard.route, Icons.Filled.Dashboard, Icons.Outlined.Dashboard, false),
        BottomNavItem("Alerts", Screen.Alerts.route, Icons.Filled.Notifications, Icons.Outlined.Notifications, true, 3),
        BottomNavItem("Explorer", Screen.Explorer.route, Icons.Filled.Explore, Icons.Outlined.Explore, false)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = CardBackground) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    BadgedBox(badge = { item.badgeCount?.let { Badge { Text("$it") } } }) {
                        Icon(if (selected) item.selectedIcon else item.unselectedIcon, contentDescription = item.title)
                    }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryPurple,
                    unselectedIconColor = AccentBeige.copy(alpha = 0.6f),
                    indicatorColor = Color.Transparent
                ),
                interactionSource = remember { NoRippleInteractionSource() }
            )
        }
    }
}

@Composable
private fun DrawerContent(
    contentNavController: NavController,
    onCloseDrawer: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ModalDrawerSheet(drawerContainerColor = BackgroundDark) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painterResource(R.drawable.ai_noc_logo), "Logo", Modifier.size(80.dp))
            Spacer(Modifier.height(8.dp))
            Text("Admin AI NOC", fontWeight = FontWeight.Bold, color = AccentBeige)
            Text("adminainoc@gmail.com", color = PrimaryPurple, fontSize = 14.sp)
        }

        HorizontalDivider(Modifier.padding(vertical = 16.dp), color = CardBackground)

        val drawerItems = listOf(
            "Asset Management" to Icons.Outlined.Dns,
            "Maintenance Windows" to Icons.Outlined.CalendarMonth,
            "Reports" to Icons.Outlined.BarChart,
            "AI Management" to Icons.Outlined.AutoAwesome,
            "Settings" to Icons.Outlined.Settings
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(drawerItems) { (title, icon) ->
                val route = when (title) {
                    "Asset Management" -> Screen.Explorer.route
                    "Maintenance Windows" -> Screen.MaintenanceWindows.route
                    "Reports" -> Screen.Alerts.route
                    "AI Management" -> Screen.AiManagement.route
                    else -> Screen.Settings.route
                }

                NavigationDrawerItem(
                    label = { Text(title) },
                    selected = false,
                    onClick = {
                        onCloseDrawer()
                        contentNavController.navigate(route) {
                            popUpTo(contentNavController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(icon, contentDescription = title, tint = PrimaryPurple) },
                    shape = RoundedCornerShape(12.dp),
                    colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = AccentBeige),
                    modifier = Modifier.border(
                        1.dp, Brush.verticalGradient(listOf(PrimaryPurple.copy(alpha = 0.3f), Color.Transparent)),
                        RoundedCornerShape(12.dp)
                    ),
                    interactionSource = remember { NoRippleInteractionSource() }
                )
            }
        }

        HorizontalDivider(Modifier.padding(12.dp), color = CardBackground)

        NavigationDrawerItem(
            label = { Text("Logout") },
            selected = false,
            onClick = onLogoutClick,
            icon = { Icon(Icons.Outlined.Logout, "Logout", tint = PrimaryPurple) },
            colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = AccentBeige),
            interactionSource = remember { NoRippleInteractionSource() }
        )
    }
}

@Composable
private fun MainContentNavHost(
    contentNavController: NavHostController,
    mainNavController: NavController
) {
    NavHost(
        navController = contentNavController,
        startDestination = Screen.Dashboard.route,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) }
    ) {
        val slideSpec = tween<IntOffset>(350)

        composable(Screen.Dashboard.route) { DashboardScreen() }
        composable(Screen.Alerts.route) { AlertsListScreen(contentNavController) }
        composable(Screen.Explorer.route) { ExplorerScreen(contentNavController) }

        // FIX: Pass BOTH nav controllers to Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                contentNavController = contentNavController,
                mainNavController = mainNavController
            )
        }

        composable(Screen.ProfileAndSecurity.route) { ProfileAndSecurityScreen(contentNavController) }
        composable(Screen.ThemeSettings.route) { ThemeSettingsScreen(contentNavController) }
        composable(Screen.NotificationSettings.route) { NotificationSettingsScreen(contentNavController) }
        composable(Screen.AiManagement.route) { AiManagementScreen(contentNavController) }
        composable(Screen.MaintenanceWindows.route) { MaintenanceWindowsScreen(contentNavController) }
        composable(Screen.About.route) { AboutScreen(contentNavController) }

        composable(
            Screen.AlertDetails.route,
            enterTransition = { slideInHorizontally(slideSpec) { it } },
            exitTransition = { slideOutHorizontally(slideSpec) { -it } }
        ) { backStackEntry ->
            val alertId = backStackEntry.arguments?.getString("alertId") ?: "N/A"
            AlertDetailsScreen(alertId, contentNavController)
        }

        composable(
            "device_details/{deviceId}",
            enterTransition = { slideInHorizontally(slideSpec) { it } },
            exitTransition = { slideOutHorizontally(slideSpec) { -it } }
        ) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: "N/A"
            DeviceDetailsScreen(deviceId, contentNavController)
        }
    }
}