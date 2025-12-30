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

@Composable
fun DashboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.ai_noc_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(32.dp)
        )
        // VISIBILITY FIX: Uses OnBackground (Grey in Light, Beige in Dark)
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
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
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AlertPill("Critical", summary.critical, CriticalRed, Modifier.weight(1f))
                AlertPill("High", summary.high, WarningYellow, Modifier.weight(1f))
                AlertPill("Medium", summary.medium, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                AlertPill("Low", summary.low, Color.Gray, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AlertPill(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    // VISIBILITY FIX: Adjusts container opacity based on theme lightness
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

        val maxVal = data.maxOfOrNull { maxOf(it.inbound, it.outbound) } ?: 10f
        val range = maxVal * 1.2f

        val lines = 4
        for (i in 0..lines) {
            val y = chartHeight * (i.toFloat() / lines)
            drawLine(color = gridColor, start = Offset(0f, y), end = Offset(width, y), strokeWidth = 1.dp.toPx())
        }

        val stepX = width / (data.size - 1)
        val points = data.mapIndexed { index, point ->
            val x = index * stepX
            val y = chartHeight - ((point.inbound / range) * chartHeight)
            Offset(x, y)
        }

        val fillPath = Path().apply {
            moveTo(points.first().x, chartHeight)
            lineTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]; val p1 = points[i + 1]
                val cp1 = Offset(p0.x + (p1.x - p0.x) / 2, p0.y)
                val cp2 = Offset(p0.x + (p1.x - p0.x) / 2, p1.y)
                cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, p1.x, p1.y)
            }
            lineTo(points.last().x, chartHeight)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(colors = listOf(lineColor.copy(alpha = 0.2f), Color.Transparent), startY = 0f, endY = chartHeight)
        )

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

        points.forEachIndexed { index, offset ->
            drawCircle(color = pointColor, radius = 6.dp.toPx(), center = offset)
            drawCircle(color = lineColor, radius = 4.dp.toPx(), center = offset)
            val textLayoutResult = textMeasurer.measure(AnnotatedString(data[index].timeLabel), style = TextStyle(color = labelColor, fontSize = 10.sp))
            var textX = offset.x - (textLayoutResult.size.width / 2)
            if (textX < 0) textX = 0f
            if (textX + textLayoutResult.size.width > width) textX = width - textLayoutResult.size.width
            drawText(textLayoutResult, topLeft = Offset(textX, chartHeight + 8.dp.toPx()))
        }
    }
}

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

@Composable
private fun RowScope.EvidenceBar(label: String, score: Float, color: Color) {
    Column(Modifier.weight(1f)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(progress = score, color = color, trackColor = color.copy(alpha = 0.2f))
    }
}

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