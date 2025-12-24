package com.example.ainoc.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// This scheme enforces your brand colors throughout the entire app.
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = AccentBeige,
    secondary = AccentBeige.copy(alpha = 0.7f), // Muted beige for secondary text
    background = BackgroundDark,
    onBackground = AccentBeige,
    surface = CardBackground, // Used for Cards, TextFields, etc.
    onSurface = AccentBeige,
    error = CriticalRed,
    onError = Color.White
)

@Composable
fun AINOCTheme(
    darkTheme: Boolean = true, // App is dark by default
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}