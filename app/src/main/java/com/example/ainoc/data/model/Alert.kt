package com.example.ainoc.data.model

import androidx.compose.ui.graphics.Color
import com.example.ainoc.ui.theme.*

/**
 * Enums for type-safe alert properties, using your theme's colors.
 */
enum class AlertPriority(val color: Color) {
    CRITICAL(CriticalRed),
    HIGH(WarningYellow),
    MEDIUM(PrimaryPurple),
    LOW(Color.Gray) // Grey for low priority
}

enum class AlertStatus {
    NEW, ACKNOWLEDGED, RESOLVED, FALSE_POSITIVE
}

/**
 * Data for a single alert item in the main list.
 */
data class Alert(
    val id: String,
    val priority: AlertPriority,
    val title: String,
    val device: String,
    val deviceCriticality: Int,
    val timestamp: String,
    val status: AlertStatus
)

/**
 * Comprehensive data for the Alert Details Screen.
 */
data class AlertDetails(
    val baseInfo: Alert,
    val affectedAssetIp: String,
    val alertTime: String,
    val fusionScore: Double,
    val aiInsight: String,
    val timelineEvents: List<TimelineEvent>,
    val performanceData: List<ChartDataPoint>,
    val logEvidence: List<String>,
    val threatIntel: ThreatIntel?,
    val recommendedActions: List<String>,
    val activityLog: List<ActivityLogItem>
)

/**
 * Represents an item in the details screen's activity log.
 */
sealed class ActivityLogItem {
    data class Event(val timestamp: String, val description: String) : ActivityLogItem()
    data class Comment(val author: String, val timestamp: String, val text: String) : ActivityLogItem()
}

/**
 * Represents a single event in the alert's visual timeline.
 */
data class TimelineEvent(
    val timestamp: String,
    val description: String,
    val isFirst: Boolean = false,
    val isLast: Boolean = false
)

/**
 * Represents matched threat intelligence data.
 */
data class ThreatIntel(
    val ipAddress: String,
    val countryOfOrigin: String,
    val threatReputation: String,
    val reportUrl: String
)

/**
 * Represents a single comment in the collaboration section.
 */
data class Comment(
    val author: String,
    val timestamp: String,
    val text: String
)