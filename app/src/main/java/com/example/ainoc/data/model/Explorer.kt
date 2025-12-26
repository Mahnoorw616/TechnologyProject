package com.example.ainoc.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Enums for type-safe properties related to devices in the Explorer screen.
 */
enum class DeviceHealth {
    NORMAL, WARNING, CRITICAL
}

enum class DeviceType(val icon: ImageVector) {
    SERVER(Icons.Default.Dns),
    ROUTER(Icons.Default.Router),
    FIREWALL(Icons.Default.Security)
}

/**
 * Represents a single device in the main explorer list.
 */
data class Device(
    val id: String,
    val name: String,
    val ipAddress: String,
    val type: DeviceType,
    val tags: List<String>,
    val health: DeviceHealth,
    val cpuTrend: List<Float> // A list of values (0.0 to 1.0) for the sparkline
)

/**
 * Represents the full details for a single device, shown on the Device Details Screen.
 */
data class DeviceDetails(
    val baseInfo: Device,
    val function: String,
    val criticality: Int,
    val maintenance: String?,
    val historicalData: Map<String, List<ChartDataPoint>>, // Key: "1H", "6H", etc.
    val recentActivity: List<Alert> // Reusing the Alert data class from Alert.kt
)