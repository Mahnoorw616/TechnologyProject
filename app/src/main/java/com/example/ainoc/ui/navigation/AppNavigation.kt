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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        val slideSpec = tween<IntOffset>(350)
        val fadeSpec = tween<Float>(300)

        composable(
            route = Screen.Splash.route,
            exitTransition = { fadeOut(fadeSpec) }
        ) {
            SplashScreen(navController = navController)
        }
        composable(
            route = Screen.Login.route,
            enterTransition = { fadeIn(fadeSpec) }
        ) {
            LoginScreen(navController = navController)
        }
        composable(
            route = Screen.MfaEmail.route,
            enterTransition = { slideInHorizontally(slideSpec) { it } },
            exitTransition = { slideOutHorizontally(slideSpec) { -it } },
            popEnterTransition = { slideInHorizontally(slideSpec) { -it } },
            popExitTransition = { slideOutHorizontally(slideSpec) { it } }
        ) {
            MfaEmailScreen(navController = navController)
        }
        composable(
            route = Screen.MfaCode.route,
            enterTransition = { slideInHorizontally(slideSpec) { it } },
            exitTransition = { slideOutHorizontally(slideSpec) { -it } },
            popEnterTransition = { slideInHorizontally(slideSpec) { -it } },
            popExitTransition = { slideOutHorizontally(slideSpec) { it } }
        ) {
            MfaCodeScreen(navController = navController)
        }
        composable(
            route = Screen.ResetPassword.route,
            enterTransition = { slideInHorizontally(slideSpec) { it } },
            exitTransition = { slideOutHorizontally(slideSpec) { -it } },
            popEnterTransition = { slideInHorizontally(slideSpec) { -it } },
            popExitTransition = { slideOutHorizontally(slideSpec) { it } }
        ) {
            ResetPasswordScreen(navController = navController)
        }

        composable(
            route = Screen.Main.route,
            enterTransition = { fadeIn(fadeSpec) }
        ) {
            MainScreen(mainNavController = navController)
        }

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