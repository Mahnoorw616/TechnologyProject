package com.example.ainoc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ainoc.data.model.NetworkStatus
import com.example.ainoc.util.NoRippleInteractionSource

@Composable
fun HealthOrb(status: NetworkStatus, modifier: Modifier = Modifier) {
    var isTapped by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "orb_animations")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(8000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (status == NetworkStatus.CRITICAL) 600 else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0f,
        animationSpec = infiniteRepeatable(animation = tween(2500, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "ripple_alpha"
    )

    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(animation = tween(2500, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "ripple_scale"
    )

    // VISIBILITY FIX: Use OnBackground color (Grey in Light, Beige in Dark)
    val textColor = MaterialTheme.colorScheme.onBackground

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .clickable(interactionSource = remember { NoRippleInteractionSource() }, indication = null) { isTapped = !isTapped },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val mainRadius = (size.width / 2) * 0.65f

                // Ripple
                drawCircle(color = status.color.copy(alpha = rippleAlpha * 0.3f), radius = mainRadius * rippleScale, center = center)

                // Rotating Ring
                rotate(degrees = rotation, pivot = center) {
                    drawCircle(
                        brush = Brush.sweepGradient(colors = listOf(Color.Transparent, status.color.copy(alpha = 0.1f), status.color.copy(alpha = 0.8f), Color.Transparent)),
                        radius = mainRadius * 1.15f, center = center, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Inner Dashed Ring
                rotate(degrees = -rotation * 1.5f, pivot = center) {
                    drawCircle(
                        brush = Brush.sweepGradient(colors = listOf(status.color.copy(alpha = 0.5f), Color.Transparent)),
                        radius = mainRadius * 0.9f, center = center, style = Stroke(width = 2.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(20f, 20f)))
                    )
                }

                // Core Orb
                drawCircle(
                    brush = Brush.radialGradient(colors = listOf(status.color.copy(alpha = 0.8f), status.color.copy(alpha = 0.3f), Color.Transparent), center = center, radius = mainRadius * pulseScale),
                    radius = mainRadius * pulseScale, center = center
                )

                drawCircle(color = status.color, radius = mainRadius * pulseScale, center = center, style = Stroke(width = 3.dp.toPx()))
            }

            // Center Text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = status.internalText,
                    color = textColor, // Dynamic Color
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    // Shadow ensures visibility even if the orb color is bright in light mode
                    style = androidx.compose.ui.text.TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(color = Color.Black.copy(alpha = 0.3f), blurRadius = 8f)
                    )
                )
                if(isTapped) {
                    Text(text = "Tap to hide", color = textColor.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        }

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