package com.example.ainoc.ui.theme

import androidx.compose.ui.graphics.Color

// --- DARK THEME PALETTE ---
val BackgroundDark = Color(0xFF1E1E1E)
val PrimaryPurple = Color(0xFF9B59B6) // Deep Lavender
val AccentBeige = Color(0xFFF5F5DC)
val CardBackground = Color(0xFF2C2C2E)

// --- LIGHT THEME PALETTE (Beige / Light Purple / Grey) ---
val FloralWhite = Color(0xFFFEFBF5) // Warm Beige Background
val LightSurface = Color(0xFFFFFFFF) // White cards
val ThistlePurple = Color(0xFFB39DDB) // Soft Light Purple (Primary)
val DeepCharcoal = Color(0xFF424242) // Text Color (Soft Black)
val LightGreyBorder = Color(0xFFE0E0E0)

// --- STATUS COLORS ---
val HealthyGreen = Color(0xFF2ECC71)
val WarningYellow = Color(0xFFF1C40F)
val CriticalRed = Color(0xFFE74C3C)

// --- SPLASH & BACKGROUND GRADIENTS ---
// Dark
val SplashGradientStart = Color(0xFF2D203D)
val SplashGradientEnd = Color(0xFF1E2A4D)

// Light (Attractive Soft Gradient: Beige -> Pale Lavender)
val LightSplashGradientStart = Color(0xFFFFF8E1) // Very light beige/gold hint
val LightSplashGradientEnd = Color(0xFFF3E5F5)   // Very light purple/lavender

val ProgressBarColor = AccentBeige
val ProgressBarGlow = ProgressBarColor.copy(alpha = 0.6f)

// Standard Material 3 Placeholders
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)