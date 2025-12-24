package com.example.ainoc.ui.navigation

sealed class Screen(val route: String) {
    // Onboarding Flow
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object MfaEmail : Screen("mfa_email_screen")
    object MfaCode : Screen("mfa_code_screen")
    object ResetPassword : Screen("reset_password_screen")

    // Main App Shell (Hosts the UI with Drawer/BottomNav)
    object Main : Screen("main_screen")

    // Destinations for the Bottom Navigation Bar
    object Dashboard : Screen("dashboard_screen")
    object Alerts : Screen("alerts_screen")
    object Explorer : Screen("explorer_screen")
    object Settings : Screen("settings_screen")

    // Standalone Stack Screens
    object AccountDetails : Screen("account_details_screen") // New route for account page
    object AlertDetails : Screen("alert_details/{alertId}") {
        fun createRoute(alertId: String) = "alert_details/$alertId"
    }
}