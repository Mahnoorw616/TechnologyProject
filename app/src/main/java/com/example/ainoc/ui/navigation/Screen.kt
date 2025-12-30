package com.example.ainoc.ui.navigation

// This is the master list of every screen in the app.
// It assigns a unique text ID (route) to each screen so the navigation system can find them.
sealed class Screen(val route: String) {
    // Screens used during the startup and login process.
    object Splash : Screen("splash_screen")
    object Login : Screen("login_screen")
    object MfaEmail : Screen("mfa_email_screen")
    object MfaCode : Screen("mfa_code_screen")
    object ResetPassword : Screen("reset_password_screen")

    // The container for the main app content after login.
    object Main : Screen("main_screen")

    // The three main tabs visible on the bottom bar.
    object Dashboard : Screen("dashboard_screen")
    object Alerts : Screen("alerts_screen")
    object Explorer : Screen("explorer_screen")
    object Settings : Screen("settings_screen")

    // Other screens that open on top of the main content.
    object AccountDetails : Screen("account_details_screen")
    object ProfileAndSecurity : Screen("profile_security_screen")
    object ThemeSettings : Screen("theme_settings_screen")
    object NotificationSettings : Screen("notification_settings_screen")
    object AiManagement : Screen("ai_management_screen")
    object MaintenanceWindows : Screen("maintenance_windows_screen")
    object About : Screen("about_screen")

    // A special screen definition that accepts a specific Alert ID.
    // This allows us to open the details for *one specific* alert.
    object AlertDetails : Screen("alert_details/{alertId}") {
        fun createRoute(alertId: String) = "alert_details/$alertId"
    }
}