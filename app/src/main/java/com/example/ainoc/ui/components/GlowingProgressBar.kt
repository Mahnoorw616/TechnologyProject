package com.example.ainoc.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// This is a custom-made progress bar that has a glowing effect.
// It's used on the Splash Screen (and potentially elsewhere) to show loading status in a stylish way.
@Composable
fun GlowingProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    barHeight: Dp = 12.dp,
    glowRadius: Dp = 20.dp
) {
    val glowRadiusPx = with(LocalDensity.current) { glowRadius.toPx() }

    // Grab colors from the current Theme so it works in both Light and Dark modes.
    val primaryColor = MaterialTheme.colorScheme.primary
    val glowColor = primaryColor.copy(alpha = 0.6f)
    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) // Subtle grey track

    // Canvas allows us to draw custom shapes directly on the screen.
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight + glowRadius) // Make canvas large enough for the glow
    ) {
        val cornerRadius = CornerRadius(size.height / 2, size.height / 2)
        val progressWidth = size.width * progress

        // Draw the grey background track first.
        drawRoundRect(
            color = trackColor,
            topLeft = Offset(0f, glowRadiusPx / 2),
            size = Size(size.width, size.height - glowRadiusPx),
            cornerRadius = cornerRadius
        )

        // If there is progress, draw the glowing colored part.
        if (progress > 0) {
            // Draw the fuzzy glowing part behind the bar.
            drawGlow(
                width = progressWidth,
                height = size.height,
                glowRadius = glowRadiusPx,
                cornerRadius = cornerRadius,
                glowColor = glowColor
            )

            // Draw the solid colored bar on top.
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(0f, glowRadiusPx / 2),
                size = Size(progressWidth, size.height - glowRadiusPx),
                cornerRadius = cornerRadius
            )
        }
    }
}

// Helper function to draw the soft radial gradient that simulates a light glow.
private fun DrawScope.drawGlow(
    width: Float,
    height: Float,
    glowRadius: Float,
    cornerRadius: CornerRadius,
    glowColor: Color
) {
    val glowBrush = Brush.radialGradient(
        colors = listOf(glowColor, Color.Transparent),
        center = Offset(width / 2, height / 2),
        radius = glowRadius
    )
    drawRoundRect(
        brush = glowBrush,
        size = Size(width, height),
        cornerRadius = cornerRadius
    )
}