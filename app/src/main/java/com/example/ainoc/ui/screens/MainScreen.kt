package com.example.ainoc.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    val bottomNavItems = listOf(
        BottomNavItem("Dashboard", Screen.Dashboard.route, Icons.Filled.Dashboard, Icons.Outlined.Dashboard, false),
        BottomNavItem("Alerts", Screen.Alerts.route, Icons.Filled.Notifications, Icons.Outlined.Notifications, true, 3),
        BottomNavItem("Explorer", Screen.Explorer.route, Icons.Filled.Explore, Icons.Outlined.Explore, false)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
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

    NavigationBar(containerColor = CardBackground) {
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
                icon = {
                    BadgedBox(badge = { if (item.badgeCount != null) Badge { Text("${item.badgeCount}") } }) {
                        Icon(if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon, contentDescription = item.title)
                    }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryPurple,
                    selectedTextColor = PrimaryPurple,
                    unselectedIconColor = AccentBeige.copy(alpha = 0.6f),
                    unselectedTextColor = AccentBeige.copy(alpha = 0.6f),
                    indicatorColor = Color.Transparent
                ),
                interactionSource = remember { NoRippleInteractionSource() } // Removes click effect
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
            Text("Admin AI NOC", fontWeight = FontWeight.Bold, color = AccentBeige)
            Text("Administrator", fontSize = 14.sp, color = PrimaryPurple)
        }
        Divider(Modifier.padding(vertical = 16.dp), color = CardBackground)

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
                NavigationDrawerItem(
                    label = { Text(title) },
                    selected = false,
                    onClick = {
                        onCloseDrawer()
                        // Use placeholder navigation for now, navigating to settings
                        val route = when (title) {
                            "Asset Management" -> Screen.Explorer.route
                            else -> Screen.Settings.route
                        }
                        navController.navigate(route) { popUpTo(navController.graph.startDestinationId) }
                    },
                    icon = { Icon(icon, title) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedTextColor = AccentBeige,
                        unselectedIconColor = AccentBeige
                    ),
                    interactionSource = remember { NoRippleInteractionSource() } // Removes click effect
                )
            }
        }

        Divider(Modifier.padding(vertical = 8.dp), color = CardBackground)

        NavigationDrawerItem(
            label = { Text("Logout") },
            selected = false,
            onClick = { /* TODO: Implement Logout Logic with confirmation */ },
            icon = { Icon(Icons.Outlined.Logout, "Logout") },
            colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = AccentBeige, unselectedIconColor = AccentBeige),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            interactionSource = remember { NoRippleInteractionSource() } // Removes click effect
        )
    }
}

@Composable
private fun MainContentNavHost(navController: NavHostController) {
    // This is the standard, modern NavHost. All compiler errors are resolved.
    NavHost(navController, startDestination = Screen.Dashboard.route) {
        val fadeSpec = tween<Float>(300)
        val slideSpec = tween<IntOffset>(350)

        composable(
            route = Screen.Dashboard.route,
            enterTransition = { fadeIn(fadeSpec) },
            exitTransition = { fadeOut(fadeSpec) }
        ) { DashboardScreen() }

        composable(
            route = Screen.Alerts.route,
            enterTransition = { fadeIn(fadeSpec) },
            exitTransition = { fadeOut(fadeSpec) }
        ) { AlertsListScreen(navController) }

        composable(
            route = Screen.Explorer.route,
            enterTransition = { fadeIn(fadeSpec) },
            exitTransition = { fadeOut(fadeSpec) }
        ) { GenericScreen("Explorer") }

        composable(
            route = Screen.Settings.route,
            enterTransition = { fadeIn(fadeSpec) },
            exitTransition = { fadeOut(fadeSpec) }
        ) { GenericScreen("Settings") }

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