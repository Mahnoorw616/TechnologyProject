package com.example.ainoc.ui.theme

import androidx.compose.ui.graphics.Color

// --- PRIMARY THEME PALETTE ---
val BackgroundDark = Color(0xFF1E1E1E)
val PrimaryPurple = Color(0xFF9B59B6)
val AccentBeige = Color(0xFFF5F5DC)
val CardBackground = Color(0xFF2C2C2E)

// --- STATUS COLORS ---
val HealthyGreen = Color(0xFF2ECC71)
val WarningYellow = Color(0xFFF1C40F)
val CriticalRed = Color(0xFFE74C3C)

// --- SPLASH SCREEN SPECIFIC COLORS ---
val SplashGradientStart = Color(0xFF2D203D)
val SplashGradientEnd = Color(0xFF1E2A4D)
val ProgressBarColor = AccentBeige
// --- This is the variable that needs to be imported ---
val ProgressBarGlow = ProgressBarColor.copy(alpha = 0.6f) // Defined dynamically for consistency

// Standard Material 3 Placeholders
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)