package com.example.ainoc.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
// Correct imports for the theme colors
import com.example.ainoc.ui.theme.ProgressBarColor
import com.example.ainoc.ui.theme.ProgressBarGlow

@Composable
fun GlowingProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    barHeight: Dp = 12.dp,
    glowRadius: Dp = 20.dp
) {
    val glowRadiusPx = with(LocalDensity.current) { glowRadius.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight + glowRadius) // Make canvas large enough for the glow
    ) {
        val cornerRadius = CornerRadius(size.height / 2, size.height / 2)
        val progressWidth = size.width * progress

        // Draw the grey background track
        drawRoundRect(
            color = Color.Gray.copy(alpha = 0.3f),
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
                cornerRadius = cornerRadius
            )

            // Draw the solid progress bar on top
            drawRoundRect(
                color = ProgressBarColor,
                topLeft = Offset(0f, glowRadiusPx / 2),
                size = Size(progressWidth, size.height - glowRadiusPx),
                cornerRadius = cornerRadius
            )
        }
    }
}

private fun DrawScope.drawGlow(width: Float, height: Float, glowRadius: Float, cornerRadius: CornerRadius) {
    val glowBrush = Brush.radialGradient(
        colors = listOf(ProgressBarGlow, Color.Transparent),
        center = Offset(width / 2, height / 2),
        radius = glowRadius
    )
    drawRoundRect(
        brush = glowBrush,
        size = Size(width, height),
        cornerRadius = cornerRadius
    )
}