package com.example.ainoc.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ainoc.ui.navigation.AppNavigation
import com.example.ainoc.ui.theme.AINOCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Marks this activity for dependency injection by Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            AINOCTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // AppNavigation is the composable that sets up the NavHost and
                    // controls which screen is currently visible.
                    AppNavigation()
                }
            }
        }
    }
}