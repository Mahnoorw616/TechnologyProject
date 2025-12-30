package com.example.ainoc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ainoc.data.model.NetworkStatus
import com.example.ainoc.ui.theme.AccentBeige
import com.example.ainoc.util.NoRippleInteractionSource

/**
 * A central dashboard component that visualizes the overall network status.
 * Features advanced Canvas drawing with rotating gradients and multi-layered pulsing.
 */
@Composable
fun HealthOrb(status: NetworkStatus, modifier: Modifier = Modifier) {
    var isTapped by remember { mutableStateOf(false) }

    // --- Animation Definitions ---
    val infiniteTransition = rememberInfiniteTransition(label = "orb_animations")

    // 1. Rotation for the outer ring
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // 2. Pulse for the core "Heartbeat"
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (status == NetworkStatus.CRITICAL) 600 else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    // 3. Ripple Alpha for the "Shockwave" effect
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_alpha"
    )

    // 4. Ripple Scale
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_scale"
    )

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(240.dp) // Slightly larger container for effects
                .clickable(
                    interactionSource = remember { NoRippleInteractionSource() },
                    indication = null
                ) { isTapped = !isTapped },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val mainRadius = (size.width / 2) * 0.65f

                // --- Layer 1: Expanding Ripple (Shockwave) ---
                drawCircle(
                    color = status.color.copy(alpha = rippleAlpha * 0.3f),
                    radius = mainRadius * rippleScale,
                    center = center
                )

                // --- Layer 2: Rotating Outer Ring ---
                // We use a SweepGradient to create a "tail" effect on the ring
                rotate(degrees = rotation, pivot = center) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Transparent,
                                status.color.copy(alpha = 0.1f),
                                status.color.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        ),
                        radius = mainRadius * 1.15f,
                        center = center,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // --- Layer 3: Rotating Inner Dashed Ring (Counter-Rotating) ---
                rotate(degrees = -rotation * 1.5f, pivot = center) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(status.color.copy(alpha = 0.5f), Color.Transparent)
                        ),
                        radius = mainRadius * 0.9f,
                        center = center,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
                        )
                    )
                }

                // --- Layer 4: The Main Core Orb ---
                // Radial gradient to make it look 3D sphere-like
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            status.color.copy(alpha = 0.8f), // Center bright
                            status.color.copy(alpha = 0.3f), // Edge fade
                            Color.Transparent // Outside
                        ),
                        center = center,
                        radius = mainRadius * pulseScale
                    ),
                    radius = mainRadius * pulseScale,
                    center = center
                )

                // --- Layer 5: Solid Border for sharpness ---
                drawCircle(
                    color = status.color,
                    radius = mainRadius * pulseScale,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Center Text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = status.internalText,
                    color = AccentBeige,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            blurRadius = 10f
                        )
                    )
                )
                if(isTapped) {
                    Text(
                        text = "Tap to hide",
                        color = AccentBeige.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Detailed summary appearing below on click
        AnimatedVisibility(visible = isTapped) {
            Text(
                text = status.summaryText,
                color = status.color,
                modifier = Modifier.padding(top = 16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}