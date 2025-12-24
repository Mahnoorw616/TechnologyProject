package com.example.ainoc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ainoc.R
import com.example.ainoc.data.model.*
import com.example.ainoc.ui.theme.*

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
        Text("Dashboard", style = MaterialTheme.typography.titleLarge, color = AccentBeige, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.Person, contentDescription = "Profile", tint = AccentBeige, modifier = Modifier
            .size(32.dp)
            .clickable { /* TODO: Show profile menu */ })
    }
}

@Composable
fun HealthOrb(status: NetworkStatus, modifier: Modifier = Modifier) {
    var isTapped by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val (duration, initialScale) = when (status) {
        NetworkStatus.HEALTHY -> 2500 to 0.98f
        NetworkStatus.WARNING -> 1200 to 0.96f
        NetworkStatus.CRITICAL -> 600 to 0.94f
    }
    val scale by infiniteTransition.animateFloat(initialValue = initialScale, targetValue = 1f, animationSpec = infiniteRepeatable(tween(duration), RepeatMode.Reverse), label = "orb_scale")

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(scale)
                .clickable { isTapped = !isTapped },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 10.dp.toPx()
                drawCircle(brush = Brush.radialGradient(colors = listOf(PrimaryPurple.copy(alpha = 0.3f), Color.Transparent)))
                drawCircle(color = status.color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
            }
            Text(status.internalText, color = AccentBeige, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }
        AnimatedVisibility(visible = isTapped) {
            Text(text = status.summaryText, color = status.color, modifier = Modifier.padding(top = 16.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LiveAlertsSummaryCard(summary: AlertSummary) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground)) {
        Column(Modifier.padding(16.dp)) {
            Text("Active Alerts", style = MaterialTheme.typography.titleMedium, color = AccentBeige)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AlertPill("Critical", summary.critical, CriticalRed, Modifier.weight(1f))
                AlertPill("High", summary.high, WarningYellow, Modifier.weight(1f))
                AlertPill("Medium", summary.medium, PrimaryPurple, Modifier.weight(1f))
                AlertPill("Low", summary.low, Color.Gray, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AlertPill(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Button(
        onClick = { /* TODO: Navigate to pre-filtered alerts */ },
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (color != Color.Gray) color.copy(alpha = 0.25f) else CardBackground,
            contentColor = if (color != Color.Gray) color else AccentBeige.copy(alpha = 0.7f)
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(count.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun NetworkThroughputCard(data: List<ChartDataPoint>) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground)) {
        Column(Modifier.padding(16.dp)) {
            Text("Overall Network Traffic (Last Hour)", style = MaterialTheme.typography.titleMedium, color = AccentBeige)
            Spacer(Modifier.height(16.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp), contentAlignment = Alignment.Center) {
                Text("Chart library would be implemented here", color = AccentBeige.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun CorrelatedHotspotsCard(hotspots: List<CorrelatedHotspot>) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground)) {
        Column(Modifier.padding(16.dp)) {
            Text("Correlated Hotspots", style = MaterialTheme.typography.titleMedium, color = AccentBeige)
            Spacer(Modifier.height(8.dp))
            hotspots.forEach { hotspot -> HotspotItemCard(hotspot) }
        }
    }
}

@Composable
private fun HotspotItemCard(hotspot: CorrelatedHotspot) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* TODO: Navigate to device details */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundDark)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(hotspot.deviceName, fontWeight = FontWeight.Bold, color = AccentBeige)
                Text("Criticality: ${hotspot.criticality}/10", style = MaterialTheme.typography.bodySmall, color = AccentBeige.copy(alpha = 0.7f))
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EvidenceBar("Anomaly", hotspot.anomalyScore, PrimaryPurple)
                EvidenceBar("Logs", hotspot.logScore, AccentBeige)
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
        Text(label, style = MaterialTheme.typography.labelSmall, color = AccentBeige.copy(alpha = 0.7f))
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(progress = score, color = color, trackColor = color.copy(alpha = 0.2f))
    }
}

@Composable
fun RecentThreatIntelCard(feed: List<ThreatIntelItem>) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground)) {
        Column(Modifier.padding(16.dp)) {
            Text("Recent Threat Intel Matches", style = MaterialTheme.typography.titleMedium, color = AccentBeige)
            Spacer(Modifier.height(16.dp))
            feed.forEachIndexed { index, item ->
                ThreatIntelItemRow(item)
                if (index < feed.lastIndex) {
                    Divider(color = BackgroundDark, thickness = 2.dp, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun ThreatIntelItemRow(item: ThreatIntelItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Link to AlienVault */ }
    ) {
        Icon(Icons.Default.Security, contentDescription = "Threat", tint = CriticalRed, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text("${item.timestamp}: ${item.description}", color = AccentBeige, style = MaterialTheme.typography.bodyMedium)
            Text(item.targetDevice, color = AccentBeige.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
        }
    }
}