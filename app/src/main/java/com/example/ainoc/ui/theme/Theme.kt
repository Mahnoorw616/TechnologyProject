package com.example.ainoc.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeSetting(val title: String) {
    SYSTEM_DEFAULT("System Default"),
    LIGHT("Light"),
    DARK("Dark")
}

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = AccentBeige,
    secondary = AccentBeige.copy(alpha = 0.7f),
    background = BackgroundDark,
    onBackground = AccentBeige,
    surface = CardBackground,
    onSurface = AccentBeige,
    error = CriticalRed,
    onError = Color.White
)

// Attractive, user-friendly Light Theme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple, // Use the stronger purple for readability/branding
    onPrimary = Color.White,
    secondary = ThistlePurple,
    background = FloralWhite,
    onBackground = DeepCharcoal,
    surface = LightSurface,
    onSurface = DeepCharcoal,
    error = CriticalRed,
    onError = Color.White,
    outline = LightGreyBorder
)

// Helper to detect if the current scheme is dark (based on background color)
val ColorScheme.isDark: Boolean
    get() = this.background == BackgroundDark

@Composable
fun AINOCTheme(
    themeSetting: ThemeSetting = ThemeSetting.DARK,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeSetting) {
        ThemeSetting.LIGHT -> false
        ThemeSetting.DARK -> true
        ThemeSetting.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

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