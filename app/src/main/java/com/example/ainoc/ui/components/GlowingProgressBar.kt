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

@Composable
fun GlowingProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    barHeight: Dp = 12.dp,
    glowRadius: Dp = 20.dp
) {
    val glowRadiusPx = with(LocalDensity.current) { glowRadius.toPx() }

    // Use Theme Colors for Light/Dark mode compatibility
    val primaryColor = MaterialTheme.colorScheme.primary
    val glowColor = primaryColor.copy(alpha = 0.6f)
    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) // Subtle grey track

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight + glowRadius) // Make canvas large enough for the glow
    ) {
        val cornerRadius = CornerRadius(size.height / 2, size.height / 2)
        val progressWidth = size.width * progress

        // Draw the background track
        drawRoundRect(
            color = trackColor,
            topLeft = Offset(0f, glowRadiusPx / 2),
            size = Size(size.width, size.height - glowRadiusPx),
            cornerRadius = cornerRadius
        )

        if (progress > 0) {
            // Draw the glowing part
            drawGlow(
                width = progressWidth,
                height = size.height,
                glowRadius = glowRadiusPx,
                cornerRadius = cornerRadius,
                glowColor = glowColor
            )

            // Draw the solid progress bar on top
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(0f, glowRadiusPx / 2),
                size = Size(progressWidth, size.height - glowRadiusPx),
                cornerRadius = cornerRadius
            )
        }
    }
}

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