package com.example.ainoc.ui.theme

import androidx.compose.ui.graphics.Color

// These colors stay the same in both light and dark modes to indicate status (Good, Warning, Bad).
val HealthyGreen = Color(0xFF2ECC71)
val WarningYellow = Color(0xFFF1C40F)
val CriticalRed = Color(0xFFE74C3C)

// These are the colors used when the app is in Dark Mode.
val BackgroundDark = Color(0xFF1E1E1E)       // Dark Grey Background
val SurfaceDark = Color(0xFF2C2C2E)           // Slightly lighter for Cards
val TextBeige = Color(0xFFF5F5DC)             // Beige Text (High contrast on Dark)
val PrimaryPurpleDark = Color(0xFF9B59B6)     // Deep Lavender

// These are the colors used when the app is in Light Mode.
val BackgroundLight = Color(0xFFFEFBF5)       // Floral White (Beige) Background
val SurfaceLight = Color(0xFFFFFFFF)          // Pure White for Cards (Popping effect)
val TextCharcoal = Color(0xFF424242)          // Dark Grey Text (High contrast on Beige)
val PrimaryPurpleLight = Color(0xFF8E44AD)    // Slightly darker Purple for better visibility on light bg
val BorderLight = Color(0xFFE0E0E0)

// These colors are mixed to create the background gradient effect.
val SplashStartDark = Color(0xFF2D203D)
val SplashEndDark = Color(0xFF1E2A4D)

val SplashStartLight = Color(0xFFFFF8E1)      // Light Beige/Gold
val SplashEndLight = Color(0xFFF3E5F5)        // Light Lavender

// These are backup names for colors to ensure older parts of the code still work correctly.
val AccentBeige = TextBeige
val CardBackground = SurfaceDark
val PrimaryPurple = PrimaryPurpleDark

val ProgressBarColor = PrimaryPurpleDark
val ProgressBarGlow = ProgressBarColor.copy(alpha = 0.6f)

// Standard placeholder colors provided by the system.
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)