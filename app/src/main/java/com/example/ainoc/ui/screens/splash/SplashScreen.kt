package com.example.ainoc.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ainoc.ui.navigation.Screen
import com.example.ainoc.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val NODE_COUNT = 12
private const val CONNECTION_SPEED_MS = 3000

@Composable
fun SplashScreen(navController: NavController) {
    val mainProgress = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }

    // Dynamic gradient based on Theme
    val isDark = MaterialTheme.colorScheme.isDark
    val gradientColors = if (isDark) {
        // Fix: Use correct variable names from Color.kt
        listOf(SplashStartDark, SplashEndDark)
    } else {
        // Fix: Use correct variable names from Color.kt
        listOf(SplashStartLight, SplashEndLight)
    }

    // Dynamic text color
    val textColor = MaterialTheme.colorScheme.onBackground

    LaunchedEffect(key1 = true) {
        contentAlpha.animateTo(1f, animationSpec = tween(1000))
        mainProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = CONNECTION_SPEED_MS, easing = LinearEasing)
        )
        delay(500)
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(300.dp)
            ) {
                // Pass text color to animation for consistency
                NeuralNetworkAnimation(progress = mainProgress.value, baseColor = MaterialTheme.colorScheme.primary)

                Text(
                    text = "AI",
                    color = textColor.copy(alpha = contentAlpha.value),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = MaterialTheme.colorScheme.primary,
                            blurRadius = 20f
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 40.dp)
            ) {
                Text(
                    text = "AI-NOC",
                    color = textColor.copy(alpha = contentAlpha.value),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Establishing Neural Uplink...",
                    color = textColor.copy(alpha = contentAlpha.value * 0.7f),
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

// ... Data class NeuronNode remains same ...
private data class NeuronNode(
    val id: Int,
    val initialAngle: Float,
    val distance: Float,
    val speed: Float
)

@Composable
fun NeuralNetworkAnimation(progress: Float, baseColor: Color) {
    val nodes = remember {
        List(NODE_COUNT) { i ->
            NeuronNode(
                id = i,
                initialAngle = (i * (360f / NODE_COUNT)) + Random.nextFloat() * 20f,
                distance = 150f + Random.nextFloat() * 100f,
                speed = 0.5f + Random.nextFloat()
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "drift")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Theme-aware colors
    val secondaryColor = MaterialTheme.colorScheme.onBackground

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxRadius = size.minDimension / 2

        val currentPositions = nodes.map { node ->
            val currentAngle = Math.toRadians((node.initialAngle + (time * 20 * node.speed)).toDouble())
            val currentDist = (maxRadius * 0.8f) - (progress * 50f)
            Offset(
                x = centerX + (cos(currentAngle) * currentDist).toFloat(),
                y = centerY + (sin(currentAngle) * currentDist).toFloat()
            )
        }

        val totalConnections = nodes.size
        val progressIndex = (progress * totalConnections).toInt()
        val progressRemainder = (progress * totalConnections) - progressIndex

        for (i in 0 until totalConnections) {
            val startNodeIndex = i
            val endNodeIndex = (i + 1) % totalConnections

            if (i < progressIndex) {
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(baseColor.copy(alpha = 0.6f), secondaryColor.copy(alpha = 0.6f)),
                        start = currentPositions[startNodeIndex],
                        end = currentPositions[endNodeIndex]
                    ),
                    start = currentPositions[startNodeIndex],
                    end = currentPositions[endNodeIndex],
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            } else if (i == progressIndex) {
                val start = currentPositions[startNodeIndex]
                val end = currentPositions[endNodeIndex]
                val currentX = start.x + (end.x - start.x) * progressRemainder
                val currentY = start.y + (end.y - start.y) * progressRemainder
                val currentPos = Offset(currentX, currentY)

                drawLine(
                    color = baseColor,
                    start = start,
                    end = currentPos,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )

                drawCircle(color = secondaryColor, radius = 4.dp.toPx(), center = currentPos)
            }
        }

        if (progress > 0.2f) {
            val meshAlpha = (progress - 0.2f).coerceIn(0f, 0.3f)
            for (i in 0 until nodes.size) {
                val connectTo = (i + 5) % nodes.size
                drawLine(
                    color = baseColor.copy(alpha = meshAlpha),
                    start = currentPositions[i],
                    end = currentPositions[connectTo],
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        currentPositions.forEachIndexed { index, pos ->
            val isActive = index <= progressIndex
            val pulse = sin(time + index).toFloat() * 2f

            if (isActive) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(secondaryColor, baseColor, Color.Transparent),
                        center = pos,
                        radius = 12.dp.toPx()
                    ),
                    center = pos,
                    radius = 6.dp.toPx() + pulse
                )
            } else {
                drawCircle(
                    color = baseColor.copy(alpha = 0.3f),
                    center = pos,
                    radius = 3.dp.toPx()
                )
            }
        }
    }
}