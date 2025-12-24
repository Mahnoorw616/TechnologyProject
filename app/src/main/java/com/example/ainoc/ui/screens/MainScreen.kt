package com.example.ainoc.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.example.ainoc.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(mainNavController: NavController) {
    val contentNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val bottomNavItems = listOf(
        BottomNavItem("Dashboard", Screen.Dashboard.route, Icons.Filled.Dashboard, Icons.Outlined.Dashboard, false),
        BottomNavItem("Alerts", Screen.Alerts.route, Icons.Filled.Notifications, Icons.Outlined.Notifications, true, 3),
        BottomNavItem("Explorer", Screen.Explorer.route, Icons.Filled.Explore, Icons.Outlined.Explore, false)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController = contentNavController,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("AI NOC", color = AccentBeige, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu", tint = AccentBeige)
                        }
                    },
                    actions = {
                        IconButton(onClick = { mainNavController.navigate(Screen.AccountDetails.route) }) {
                            Icon(Icons.Default.AccountCircle, "Account", tint = AccentBeige)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark.copy(alpha = 0.95f))
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    items = bottomNavItems,
                    navController = contentNavController
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(SplashGradientStart, SplashGradientEnd)))
            ) {
                MainContentNavHost(navController = contentNavController)
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(items: List<BottomNavItem>, navController: NavController) {
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // This ensures the selected item is always in sync with the navigation state
    LaunchedEffect(currentRoute) {
        selectedItemIndex = items.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
    }

    NavigationBar(containerColor = BackgroundDark.copy(alpha = 0.95f)) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    if (selectedItemIndex != index) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = { Text(item.title) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryPurple,
                    unselectedIconColor = AccentBeige.copy(alpha = 0.6f),
                    indicatorColor = PrimaryPurple.copy(alpha = 0.15f)
                ),
                icon = {
                    BadgedBox(badge = { if (item.badgeCount != null) Badge { Text("${item.badgeCount}") } }) {
                        Icon(if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon, contentDescription = item.title)
                    }
                }
            )
        }
    }
}

@Composable
private fun DrawerContent(navController: NavController, onCloseDrawer: () -> Unit) {
    ModalDrawerSheet(drawerContainerColor = BackgroundDark) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painterResource(R.drawable.ai_noc_logo), "Logo", modifier = Modifier.size(80.dp))
            Spacer(Modifier.height(8.dp))
            Text("Network Admin", fontWeight = FontWeight.Bold, color = AccentBeige)
            Text("administrator@company.com", fontSize = 14.sp, color = AccentBeige.copy(alpha = 0.7f))
        }
        Divider(Modifier.padding(vertical = 16.dp), color = CardBackground)

        val drawerItems = listOf(
            "Asset Management" to Screen.Explorer.route, // Example route
            "Maintenance" to Screen.Settings.route, // Example route
            "Reports" to Screen.Settings.route, // Example route
            "AI Management" to Screen.Settings.route, // Example route
            "Settings" to Screen.Settings.route
        )

        drawerItems.forEach { (title, route) ->
            NavigationDrawerItem(
                label = { Text(title) },
                selected = false,
                onClick = {
                    onCloseDrawer()
                    navController.navigate(route) { popUpTo(navController.graph.startDestinationId) }
                },
                icon = {
                    val icon = when (route) {
                        Screen.Explorer.route -> Icons.Outlined.Dns
                        Screen.Settings.route -> Icons.Outlined.Settings
                        else -> Icons.Outlined.Info
                    }
                    Icon(icon, title)
                },
                colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = AccentBeige, unselectedIconColor = AccentBeige)
            )
        }
        Spacer(Modifier.weight(1f))
        NavigationDrawerItem(
            label = { Text("Logout") },
            selected = false,
            onClick = { /* TODO: Implement Logout Logic */ },
            icon = { Icon(Icons.Outlined.Logout, "Logout") },
            colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = AccentBeige, unselectedIconColor = AccentBeige)
        )
    }
}

@Composable
private fun MainContentNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Dashboard.route) {
        val fadeSpec = tween<Float>(300)
        val slideSpec = tween<IntOffset>(350)

        composable(Screen.Dashboard.route, enterTransition = { fadeIn(fadeSpec) }, exitTransition = { fadeOut(fadeSpec) }) { DashboardScreen() }
        composable(Screen.Alerts.route, enterTransition = { fadeIn(fadeSpec) }, exitTransition = { fadeOut(fadeSpec) }) { AlertsListScreen(navController) }
        composable(Screen.Explorer.route, enterTransition = { fadeIn(fadeSpec) }, exitTransition = { fadeOut(fadeSpec) }) { GenericScreen("Explorer") }
        composable(Screen.Settings.route, enterTransition = { fadeIn(fadeSpec) }, exitTransition = { fadeOut(fadeSpec) }) { GenericScreen("Settings") }

        composable(
            route = Screen.AlertDetails.route,
            enterTransition = { slideInHorizontally(slideSpec) { it } },
            exitTransition = { slideOutHorizontally(slideSpec) { -it } },
            popEnterTransition = { slideInHorizontally(slideSpec) { -it } },
            popExitTransition = { slideOutHorizontally(slideSpec) { it } }
        ) { backStackEntry ->
            val alertId = backStackEntry.arguments?.getString("alertId") ?: "N/A"
            AlertDetailsScreen(alertId, navController)
        }
    }
}

@Composable
fun GenericScreen(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$name Screen", style = MaterialTheme.typography.headlineMedium, color = AccentBeige)
    }
}