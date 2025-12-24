package com.example.ainoc.data.model

import androidx.compose.ui.graphics.Color
import com.example.ainoc.ui.theme.CriticalRed
import com.example.ainoc.ui.theme.HealthyGreen
import com.example.ainoc.ui.theme.WarningYellow

/**
 * Represents the overall health of the network for the Health Orb.
 */
enum class NetworkStatus(val color: Color, val internalText: String, val summaryText: String) {
    HEALTHY(HealthyGreen, "HEALTHY", "Network is stable. No active critical alerts."),
    WARNING(WarningYellow, "WARNING", "Multiple non-critical performance anomalies detected."),
    CRITICAL(CriticalRed, "CRITICAL", "Critical Alert: Suspected Brute-Force attack detected.")
}

/**
 * Represents the data for the dynamic alert counter widget on the dashboard.
 */
data class AlertSummary(
    val critical: Int,
    val high: Int,
    val medium: Int,
    val low: Int
)

/**
 * Represents a single, high-priority correlated event in the "Hotspots" list.
 */
data class CorrelatedHotspot(
    val deviceName: String,
    val criticality: Int,
    val anomalyScore: Float,
    val logScore: Float,
    val hasThreatMatch: Boolean
)

/**
 * Represents a single event in the "Recent Threat Intelligence" feed.
 */
data class ThreatIntelItem(
    val timestamp: String,
    val description: String,
    val targetDevice: String
)

/**
 * Represents a single data point for a chart. This is a non-generic class.
 */
data class ChartDataPoint(
    val timeLabel: String,
    val inbound: Float,
    val outbound: Float
)