package com.example.ainoc.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ainoc.data.local.SessionManager
import com.example.ainoc.ui.navigation.AppNavigation
import com.example.ainoc.ui.theme.AINOCTheme
import com.example.ainoc.ui.theme.ThemeSetting
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// This tag tells Hilt (our tool helper) that this Activity needs to receive dependencies automatically.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // We ask Hilt to provide the SessionManager here so we can read the saved theme settings.
    @Inject
    lateinit var sessionManager: SessionManager

    // This function is the starting point where the app launches and sets up the screen.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This connects the Android system to our modern Jetpack Compose UI code.
        setContent {

            // We listen to the saved theme setting. If it changes, the app redraws itself instantly.
            val themeString by sessionManager.appTheme.collectAsState(initial = "DARK")

            // We try to convert the saved text into a ThemeSetting. If the text is bad, we safely default to DARK mode.
            val themeSetting = try {
                ThemeSetting.valueOf(themeString)
            } catch (e: Exception) {
                ThemeSetting.DARK
            }

            // This wrapper applies our specific colors (Light or Dark) to the entire app based on the setting above.
            AINOCTheme(themeSetting = themeSetting) {

                // This creates the background canvas that fills the whole screen with the theme's background color.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // This component handles the navigation logic to decide which screen (Login, Dashboard, etc.) to show.
                    AppNavigation()
                }
            }
        }
    }
}