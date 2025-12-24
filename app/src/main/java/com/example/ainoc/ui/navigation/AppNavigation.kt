package com.example.ainoc.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ainoc.ui.screens.AccountDetailsScreen
import com.example.ainoc.ui.screens.MainScreen
import com.example.ainoc.ui.screens.login.*
import com.example.ainoc.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        val slideIn = slideInHorizontally(animationSpec = tween(400)) { it }
        val slideOut = slideOutHorizontally(animationSpec = tween(400)) { -it }
        val popSlideIn = slideInHorizontally(animationSpec = tween(400)) { -it }
        val popSlideOut = slideOutHorizontally(animationSpec = tween(400)) { it }

        // --- Onboarding Flow ---
        composable(Screen.Splash.route) { SplashScreen(navController = navController) }
        composable(Screen.Login.route) { LoginScreen(navController = navController) }
        composable(Screen.MfaEmail.route, enterTransition = { slideIn }, exitTransition = { slideOut }, popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }) { MfaEmailScreen(navController = navController) }
        composable(Screen.MfaCode.route, enterTransition = { slideIn }, exitTransition = { slideOut }, popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }) { MfaCodeScreen(navController = navController) }
        composable(Screen.ResetPassword.route, enterTransition = { slideIn }, exitTransition = { slideOut }, popEnterTransition = { popSlideIn }, popExitTransition = { popSlideOut }) { ResetPasswordScreen(navController = navController) }

        // --- Main App ---
        composable(
            route = Screen.Main.route,
            enterTransition = { fadeIn(animationSpec = tween(500)) }
        ) {
            // We pass the main NavController here to handle navigation to top-level screens like Account
            MainScreen(mainNavController = navController)
        }

        // --- Standalone Stack Screens ---
        composable(
            route = Screen.AccountDetails.route, // New destination for the account screen
            enterTransition = { slideIn },
            exitTransition = { slideOut },
            popEnterTransition = { popSlideIn },
            popExitTransition = { popSlideOut }
        ) {
            AccountDetailsScreen(navController = navController)
        }
    }
}