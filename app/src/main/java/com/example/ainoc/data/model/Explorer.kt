package com.example.ainoc.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.graphics.vector.ImageVector

// This defines the basic health state of a specific machine.
// We use this to put a colored dot (Green/Yellow/Red) next to devices so users can spot broken hardware quickly.
enum class DeviceHealth {
    NORMAL, WARNING, CRITICAL
}

// This defines the category of the hardware.
// We use this to automatically show the correct icon (like a box for a Server or a circle for a Router) in the list.
enum class DeviceType(val icon: ImageVector) {
    SERVER(Icons.Default.Dns),
    ROUTER(Icons.Default.Router),
    FIREWALL(Icons.Default.Security)
}

// This holds the summary info for a single machine in the inventory list.
// Updated: Added 'isCritical' to allow users to tag important assets.
data class Device(
    val id: String,
    val name: String,
    val ipAddress: String,
    val type: DeviceType,
    val tags: List<String>,
    val health: DeviceHealth,
    val cpuTrend: List<Float>, // A list of values (0.0 to 1.0) for the sparkline
    val isCritical: Boolean = false // New: Tracks if the user tagged this as a Critical Asset
)

// This holds the full profile of a machine.
// We use this when a user taps a device to show its complete history, role, and maintenance schedule.
data class DeviceDetails(
    val baseInfo: Device,
    val function: String,
    val criticality: Int,
    val maintenance: String?,
    val historicalData: Map<String, List<ChartDataPoint>>, // Key: "1H", "6H", etc.
    val recentActivity: List<Alert> // Reusing the Alert data class from Alert.kt
)