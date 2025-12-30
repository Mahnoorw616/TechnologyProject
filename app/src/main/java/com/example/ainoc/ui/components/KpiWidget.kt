package com.example.ainoc.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ainoc.ui.theme.*

/**
 * A widget to display Key Performance Indicators (e.g., Uptime, Latency).
 */
@Composable
fun KpiWidget(
    title: String,
    value: String,
    icon: ImageVector,
    trend: String? = null,
    isTrendPositive: Boolean = true, // If true, trend is green/good. If false, red/bad.
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentBeige.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                color = AccentBeige,
                fontWeight = FontWeight.Bold
            )

            if (trend != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isTrendPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (isTrendPositive) HealthyGreen else CriticalRed,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = trend,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isTrendPositive) HealthyGreen else CriticalRed
                    )
                }
            }
        }
    }
}