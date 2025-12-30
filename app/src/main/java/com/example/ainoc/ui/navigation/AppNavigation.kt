package com.example.ainoc.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ainoc.ui.screens.AccountDetailsScreen
import com.example.ainoc.ui.screens.MainScreen
import com.example.ainoc.ui.screens.login.*
import com.example.ainoc.ui.screens.splash.SplashScreen

// This function acts as the traffic controller for the entire app.
// It decides which screen to show based on where the user wants to go.
@Composable
fun AppNavigation() {
    // We create a controller to remember where we are and where we've been.
    val navController = rememberNavController()

    // This is the container that holds the current screen.
    // It watches the controller and swaps the screen content when the destination changes.
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route // We start the app on the Splash screen.
    ) {
        // These settings control how fast the slide and fade animations play.
        val slideSpec = tween<IntOffset>(350)
        val fadeSpec = tween<Float>(300)

        // This defines the Splash screen.
        // When leaving this screen, it fades out gently.
        composable(
            route = Screen.Splash.route,
            exitTransition = { fadeOut(fadeSpec) }
        ) {
            SplashScreen(navController = navController)
        }

        // This defines the Login screen.
        // It fades in when appearing.
        composable(
            route = Screen.Login.route,
            enterTransition = { fadeIn(fadeSpec) }
        ) {
            LoginScreen(navController = navController)
        }

        // This defines the "Enter Email" screen for multi-factor authentication.
        // It slides in from the side like a card.
        composable(
            route = Screen.MfaEmail.route,
            enterTransition = { slideInHorizontally(slideSpec) { it } },
            exitTransition = { slideOutHorizontally(slideSpec) { -it } },
            popEnterTransition = { slideInHorizontally(slideSpec) { -it } },
            popExitTransition = { slideOutHorizontally(slideSpec) { it } }
        ) {
            MfaEmailScreen(navController = navController)
        }

        // This defines the "Enter Code" screen.
        // It also slides in/out to feel like a continuous flow.
        composable(
            route = Screen.MfaCode.route,
            enterTransition = { slideInHorizontally(slideSpec) { it } },
            exitTransition = { slideOutHorizontally(slideSpec) { -it } },
            popEnterTransition = { slideInHorizontally(slideSpec) { -it } },
            popExitTransition = { slideOutHorizontally(slideSpec) { it } }
        ) {
            MfaCodeScreen(navController = navController)
        }

        // This defines the Password Reset screen.
        composable(
            route = Screen.ResetPassword.route,
            enterTransition = { slideInHorizontally(slideSpec) { it } },
            exitTransition = { slideOutHorizontally(slideSpec) { -it } },
            popEnterTransition = { slideInHorizontally(slideSpec) { -it } },
            popExitTransition = { slideOutHorizontally(slideSpec) { it } }
        ) {
            ResetPasswordScreen(navController = navController)
        }

        // This defines the Main App screen (Dashboard, Alerts, Explorer).
        // Once logged in, this is the main container the user sees.
        composable(
            route = Screen.Main.route,
            enterTransition = { fadeIn(fadeSpec) }
        ) {
            MainScreen(mainNavController = navController)
        }

        // This defines the Account Details screen.
        composable(
            route = Screen.AccountDetails.route,
            enterTransition = { slideInHorizontally(slideSpec) { it } },
            exitTransition = { slideOutHorizontally(slideSpec) { -it } },
            popEnterTransition = { slideInHorizontally(slideSpec) { -it } },
            popExitTransition = { slideOutHorizontally(slideSpec) { it } }
        ) {
            AccountDetailsScreen(navController = navController)
        }
    }
}