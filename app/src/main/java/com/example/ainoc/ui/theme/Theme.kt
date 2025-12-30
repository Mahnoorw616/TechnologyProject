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

// This list defines the choices the user has for how the app looks (Light, Dark, or System Default).
enum class ThemeSetting(val title: String) {
    SYSTEM_DEFAULT("System Default"),
    LIGHT("Light"),
    DARK("Dark")
}

// This organizes the dark colors and assigns them to specific UI parts (like background vs text).
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurpleDark,
    onPrimary = TextBeige,
    secondary = TextBeige.copy(alpha = 0.7f),
    background = BackgroundDark,
    onBackground = TextBeige,
    surface = SurfaceDark,
    onSurface = TextBeige,
    error = CriticalRed,
    onError = Color.White,
    outline = Color.Gray
)

// This organizes the light colors and assigns them to specific UI parts.
private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurpleLight,
    onPrimary = Color.White,
    secondary = PrimaryPurpleLight.copy(alpha = 0.8f),
    background = BackgroundLight,
    onBackground = TextCharcoal,
    surface = SurfaceLight,
    onSurface = TextCharcoal,
    error = CriticalRed,
    onError = Color.White,
    outline = BorderLight
)

// This is a helper tool to quickly check if the current theme is using the dark background.
val ColorScheme.isDark: Boolean
    get() = this.background == BackgroundDark

// This is the main wrapper that applies the chosen colors and fonts to the whole app.
@Composable
fun AINOCTheme(
    themeSetting: ThemeSetting = ThemeSetting.DARK,
    content: @Composable () -> Unit
) {
    // Decides whether to show dark mode based on the user's choice or the phone's system setting.
    val darkTheme = when (themeSetting) {
        ThemeSetting.LIGHT -> false
        ThemeSetting.DARK -> true
        ThemeSetting.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }

    // Picks the correct set of colors based on the decision above.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        // This ensures the status bar at the very top of the phone matches the app's background color.
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Applies the colors, shapes, and fonts to the content inside.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}