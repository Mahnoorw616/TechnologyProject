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

// Dark Theme: Dark BG, Beige Text
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

// Light Theme: Beige BG, Grey/Purple Text
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