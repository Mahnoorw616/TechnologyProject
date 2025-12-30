package com.example.ainoc.data.model

import androidx.compose.ui.graphics.Color
import com.example.ainoc.ui.theme.CriticalRed
import com.example.ainoc.ui.theme.HealthyGreen
import com.example.ainoc.ui.theme.WarningYellow

// This defines the overall "mood" or health of the entire system.
// We use this to control the big pulsing "Health Orb" on the dashboard (Green=Good, Red=Bad) for an instant status check.
enum class NetworkStatus(val color: Color, val internalText: String, val summaryText: String) {
    HEALTHY(HealthyGreen, "HEALTHY", "Network is stable. No active critical alerts."),
    WARNING(WarningYellow, "WARNING", "Multiple non-critical performance anomalies detected."),
    CRITICAL(CriticalRed, "CRITICAL", "Critical Alert: Suspected Brute-Force attack detected.")
}

// This holds the total counts of current problems.
// We use this for the top badges on the dashboard (e.g., "5 Critical", "2 High") to show the current workload.
data class AlertSummary(
    val critical: Int,
    val high: Int,
    val medium: Int,
    val low: Int
)

// This represents a major issue where multiple small alerts are grouped together.
// We use this to show "Hotspots" so the user can fix one big root cause (like a failing server) instead of chasing 50 small alerts.
data class CorrelatedHotspot(
    val deviceName: String,
    val criticality: Int,
    val anomalyScore: Float,
    val logScore: Float,
    val hasThreatMatch: Boolean
)

// This represents a single item in the live security news feed.
// We use this to show a ticker of blocked attacks or suspicious activity happening right now.
data class ThreatIntelItem(
    val timestamp: String,
    val description: String,
    val targetDevice: String
)

// This represents a single dot on a graph.
// We use this to draw the lines on the charts, plotting time against internet speed (inbound/outbound).
data class ChartDataPoint(
    val timeLabel: String,
    val inbound: Float,
    val outbound: Float
)