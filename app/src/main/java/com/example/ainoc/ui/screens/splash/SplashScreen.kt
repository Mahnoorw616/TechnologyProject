package com.example.ainoc.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ainoc.R
import com.example.ainoc.ui.components.GlowingProgressBar
import com.example.ainoc.ui.navigation.Screen
import com.example.ainoc.ui.theme.*
import kotlinx.coroutines.delay

private const val SPLASH_DELAY_MS = 3500L
private const val FADE_IN_DURATION = 1500
private const val PROGRESS_ANIMATION_DURATION = 3000

@Composable
fun SplashScreen(navController: NavController) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f, animationSpec = tween(FADE_IN_DURATION), label = "alpha")
    val progressAnim by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f, animationSpec = tween(PROGRESS_ANIMATION_DURATION, delayMillis = 200), label = "progress")

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(SPLASH_DELAY_MS)
        navController.navigate(Screen.Login.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(SplashGradientStart, SplashGradientEnd))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp)
                .alpha(alphaAnim),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box(contentAlignment = Alignment.Center) {
                Spacer(
                    modifier = Modifier
                        .size(180.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(PrimaryPurple.copy(alpha = 0.4f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )
                Image(
                    painter = painterResource(id = R.drawable.ai_noc_logo),
                    contentDescription = "AI-NOC Logo",
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("AI-NOC", color = AccentBeige, fontSize = 44.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Intelligent Network Oversight", color = AccentBeige.copy(alpha = 0.8f), fontSize = 18.sp, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.weight(1.5f))
            GlowingProgressBar(progress = progressAnim, modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}