package com.example.ainoc.data.model

import androidx.compose.ui.graphics.Color
import com.example.ainoc.ui.theme.*

// This defines the specific urgency levels for problems.
// We use this to automatically color-code alerts (like Red for Critical) so the user knows what to fix first.
enum class AlertPriority(val color: Color) {
    CRITICAL(CriticalRed),
    HIGH(WarningYellow),
    MEDIUM(PrimaryPurple),
    LOW(Color.Gray) // Grey for low priority
}

// This tracks the lifecycle of an alert.
// We use this to know if a problem is brand new, if someone is working on it, or if it's already fixed.
enum class AlertStatus {
    NEW, ACKNOWLEDGED, RESOLVED, FALSE_POSITIVE
}

// This holds the summary information for a single row in the main list.
// We use this to show just the headline, time, and severity so the user can quickly scan through many alerts.
data class Alert(
    val id: String,
    val priority: AlertPriority,
    val title: String,
    val device: String,
    val deviceCriticality: Int,
    val timestamp: String,
    val status: AlertStatus
)

// This holds every single piece of information about a specific problem.
// We use this for the "Details Screen" to show graphs, AI analysis, logs, and recommended steps to fix the issue.
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

// This handles different types of history entries in one list.
// We use this to mix computer-generated events (like "Alert started") and human comments (like "I fixed it") in the same timeline.
sealed class ActivityLogItem {
    data class Event(val timestamp: String, val description: String) : ActivityLogItem()
    data class Comment(val author: String, val timestamp: String, val text: String) : ActivityLogItem()
}

// This represents a specific moment in time on a graph.
// We use this to draw the visual timeline so users can see exactly when an attack started and ended.
data class TimelineEvent(
    val timestamp: String,
    val description: String,
    val isFirst: Boolean = false,
    val isLast: Boolean = false
)

// This stores information about external hackers or bad IP addresses.
// We use this to tell the user exactly who is attacking the network and where they are from.
data class ThreatIntel(
    val ipAddress: String,
    val countryOfOrigin: String,
    val threatReputation: String,
    val reportUrl: String
)

// This represents a note written by a team member.
// We use this for collaboration so the team can see who said what about a specific alert.
data class Comment(
    val author: String,
    val timestamp: String,
    val text: String
)