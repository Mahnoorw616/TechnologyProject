package com.example.ainoc.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ainoc.R
import com.example.ainoc.data.model.*
import com.example.ainoc.ui.theme.*
import com.example.ainoc.util.NoRippleInteractionSource

// This is the top bar of the dashboard screen.
// It shows the app logo, the title "Dashboard," and a profile icon that you can tap.
@Composable
fun DashboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Displays the app logo image.
        Image(
            painter = painterResource(id = R.drawable.ai_noc_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(32.dp)
        )
        // Displays the main title. The color automatically adjusts based on light/dark mode.
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        // Displays a profile icon that acts as a button for user settings.
        Icon(
            Icons.Default.Person,
            contentDescription = "Profile",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .size(32.dp)
                .clickable(
                    interactionSource = remember { NoRippleInteractionSource() },
                    indication = null
                ) { /* TODO: Show profile menu */ }
        )
    }
}

// This card shows a quick count of all active problems, categorized by severity (Critical, High, etc.).
// It helps the user see at a glance how busy the system is.
@Composable
fun LiveAlertsSummaryCard(summary: AlertSummary) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Active Alerts", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(16.dp))
            // Arranges the four severity buttons in a horizontal row.
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AlertPill("Critical", summary.critical, CriticalRed, Modifier.weight(1f))
                AlertPill("High", summary.high, WarningYellow, Modifier.weight(1f))
                AlertPill("Medium", summary.medium, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                AlertPill("Low", summary.low, Color.Gray, Modifier.weight(1f))
            }
        }
    }
}

// This is a small, colorful button inside the summary card.
// It shows a number (count) and a label (severity level) with a specific background color.
@Composable
private fun AlertPill(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    // Sets how see-through the background color is, depending on if it's dark or light mode.
    val alpha = if (MaterialTheme.colorScheme.isDark) 0.25f else 0.15f

    Button(
        onClick = { },
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (color != Color.Gray) color.copy(alpha = alpha) else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (color != Color.Gray) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        ),
        interactionSource = remember { NoRippleInteractionSource() }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(count.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

// This card displays a graph of network speed (traffic) over the last hour.
// It gives a visual representation of how busy the network is.
@Composable
fun NetworkThroughputCard(data: List<ChartDataPoint>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Overall Network Traffic", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("Last Hour", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            Spacer(Modifier.height(24.dp))
            // The box holds the actual drawing of the chart.
            Box(Modifier.fillMaxWidth().height(180.dp)) {
                if (data.isEmpty()) {
                    Text("No Data Available", modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                } else {
                    TrafficChart(data = data)
                }
            }
        }
    }
}

// This function manually draws the smooth curve chart on the screen.
// It calculates points, draws lines, fills colors, and adds labels.
@OptIn(ExperimentalTextApi::class)
@Composable
private fun TrafficChart(data: List<ChartDataPoint>) {
    val textMeasurer = rememberTextMeasurer()
    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val pointColor = MaterialTheme.colorScheme.surface

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val bottomGutter = 30.dp.toPx()
        val chartHeight = height - bottomGutter

        // Figure out the highest value to scale the chart correctly.
        val maxVal = data.maxOfOrNull { maxOf(it.inbound, it.outbound) } ?: 10f
        val range = maxVal * 1.2f

        // Draw horizontal background lines to make it look like a grid.
        val lines = 4
        for (i in 0..lines) {
            val y = chartHeight * (i.toFloat() / lines)
            drawLine(color = gridColor, start = Offset(0f, y), end = Offset(width, y), strokeWidth = 1.dp.toPx())
        }

        // Calculate x,y coordinates for each data point on the screen.
        val stepX = width / (data.size - 1)
        val points = data.mapIndexed { index, point ->
            val x = index * stepX
            val y = chartHeight - ((point.inbound / range) * chartHeight)
            Offset(x, y)
        }

        // Create the shape for the filled color area under the line.
        val fillPath = Path().apply {
            moveTo(points.first().x, chartHeight)
            lineTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                // Smooth the line using curves between points.
                val p0 = points[i]; val p1 = points[i + 1]
                val cp1 = Offset(p0.x + (p1.x - p0.x) / 2, p0.y)
                val cp2 = Offset(p0.x + (p1.x - p0.x) / 2, p1.y)
                cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, p1.x, p1.y)
            }
            lineTo(points.last().x, chartHeight)
            close()
        }

        // Draw the gradient fill.
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(colors = listOf(lineColor.copy(alpha = 0.2f), Color.Transparent), startY = 0f, endY = chartHeight)
        )

        // Draw the actual solid line on top.
        val strokePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]; val p1 = points[i + 1]
                val cp1 = Offset(p0.x + (p1.x - p0.x) / 2, p0.y)
                val cp2 = Offset(p0.x + (p1.x - p0.x) / 2, p1.y)
                cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, p1.x, p1.y)
            }
        }

        drawPath(path = strokePath, color = lineColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

        // Draw dots and text labels for each point.
        points.forEachIndexed { index, offset ->
            drawCircle(color = pointColor, radius = 6.dp.toPx(), center = offset)
            drawCircle(color = lineColor, radius = 4.dp.toPx(), center = offset)
            val textLayoutResult = textMeasurer.measure(AnnotatedString(data[index].timeLabel), style = TextStyle(color = labelColor, fontSize = 10.sp))
            var textX = offset.x - (textLayoutResult.size.width / 2)
            // Keep text inside screen bounds.
            if (textX < 0) textX = 0f
            if (textX + textLayoutResult.size.width > width) textX = width - textLayoutResult.size.width
            drawText(textLayoutResult, topLeft = Offset(textX, chartHeight + 8.dp.toPx()))
        }
    }
}

// This card lists specific devices or areas ("Hotspots") that are having major issues.
// It groups related alerts together so you can fix the root cause.
@Composable
fun CorrelatedHotspotsCard(hotspots: List<CorrelatedHotspot>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Correlated Hotspots", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            hotspots.forEach { hotspot -> HotspotItemCard(hotspot) }
        }
    }
}

// This is the individual row for each hotspot item.
// It shows the device name, score, and small bars indicating why it's a problem (Logs vs Anomaly).
@Composable
private fun HotspotItemCard(hotspot: CorrelatedHotspot) {
    // Nested card color slightly different for visibility
    val cardColor = if (MaterialTheme.colorScheme.isDark) BackgroundDark else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable(interactionSource = remember { NoRippleInteractionSource() }, indication = null) { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(hotspot.deviceName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Criticality: ${hotspot.criticality}/10", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EvidenceBar("Anomaly", hotspot.anomalyScore, MaterialTheme.colorScheme.primary)
                EvidenceBar("Logs", hotspot.logScore, MaterialTheme.colorScheme.onSurface)
                Icon(
                    if (hotspot.hasThreatMatch) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                    contentDescription = "Threat Intel",
                    tint = if (hotspot.hasThreatMatch) CriticalRed else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Draws a small progress bar with a label, used inside the Hotspot card.
@Composable
private fun RowScope.EvidenceBar(label: String, score: Float, color: Color) {
    Column(Modifier.weight(1f)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(progress = score, color = color, trackColor = color.copy(alpha = 0.2f))
    }
}

// This card shows a live feed of external security threats detected on the network.
@Composable
fun RecentThreatIntelCard(feed: List<ThreatIntelItem>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Recent Threat Intel Matches", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(16.dp))
            feed.forEachIndexed { index, item ->
                ThreatIntelItemRow(item)
                if (index < feed.lastIndex) {
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

// Displays a single row in the threat feed with an icon, time, and description.
@Composable
private fun ThreatIntelItemRow(item: ThreatIntelItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable(interactionSource = remember { NoRippleInteractionSource() }, indication = null) { }
    ) {
        Icon(Icons.Default.Security, contentDescription = "Threat", tint = CriticalRed, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text("${item.timestamp}: ${item.description}", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
            Text(item.targetDevice, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
        }
    }
}