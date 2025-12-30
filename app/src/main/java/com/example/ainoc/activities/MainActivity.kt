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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Observe the theme from SessionManager
            val themeString by sessionManager.appTheme.collectAsState(initial = "DARK")
            val themeSetting = try {
                ThemeSetting.valueOf(themeString)
            } catch (e: Exception) {
                ThemeSetting.DARK
            }

            AINOCTheme(themeSetting = themeSetting) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}