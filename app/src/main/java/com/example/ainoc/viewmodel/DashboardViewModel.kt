package com.example.ainoc.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ainoc.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// This ViewModel is the "brain" for the main Dashboard screen.
// It holds all the live data that the dashboard needs to display, like network health, charts, and alerts.
@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    // Holds the current health status of the network (e.g., Healthy, Critical).
    private val _networkStatus = MutableStateFlow(NetworkStatus.HEALTHY)
    val networkStatus = _networkStatus.asStateFlow()

    // Holds the counts for the alert summary card (e.g., "5 Critical", "2 High").
    private val _alertSummary = MutableStateFlow(AlertSummary(0, 0, 0, 0))
    val alertSummary = _alertSummary.asStateFlow()

    // Holds the list of major problem areas ("Hotspots") to display on the dashboard.
    private val _hotspots = MutableStateFlow<List<CorrelatedHotspot>>(emptyList())
    val hotspots = _hotspots.asStateFlow()

    // Holds the list of recent security threats for the news feed widget.
    private val _threatIntelFeed = MutableStateFlow<List<ThreatIntelItem>>(emptyList())
    val threatIntelFeed = _threatIntelFeed.asStateFlow()

    // Holds the data points for the network traffic chart (the graph).
    private val _trafficData = MutableStateFlow<List<ChartDataPoint>>(emptyList())
    val trafficData = _trafficData.asStateFlow()

    // This block runs automatically when the Dashboard is first opened.
    init {
        loadDummyData()
    }

    // Creates fake example data so we can see what the dashboard looks like without a real server connection.
    private fun loadDummyData() {
        // Sets the big orb to green/healthy.
        _networkStatus.value = NetworkStatus.HEALTHY

        // Sets the alert counts.
        _alertSummary.value = AlertSummary(critical = 1, high = 0, medium = 3, low = 8)

        // Creates a list of devices with issues.
        _hotspots.value = listOf(
            CorrelatedHotspot(
                deviceName = "Primary-DB-Server",
                criticality = 10,
                anomalyScore = 0.85f,
                logScore = 0.91f,
                hasThreatMatch = true
            ),
            CorrelatedHotspot(
                deviceName = "WAN-Router-01",
                criticality = 8,
                anomalyScore = 0.95f,
                logScore = 0.20f,
                hasThreatMatch = false
            ),
            CorrelatedHotspot(
                deviceName = "API-Gateway-EU",
                criticality = 7,
                anomalyScore = 0.40f,
                logScore = 0.75f,
                hasThreatMatch = false
            )
        )

        // Creates a list of fake security threats.
        _threatIntelFeed.value = listOf(
            ThreatIntelItem(
                timestamp = "1 min ago",
                description = "Inbound connection from known C2 Server (IP: 123.45.67.89) was blocked.",
                targetDevice = "Target: Web-Server-04"
            ),
            ThreatIntelItem(
                timestamp = "8 min ago",
                description = "Outbound connection to phishing domain (URL: badsite.net) was blocked.",
                targetDevice = "Target: Workstation-112"
            ),
            ThreatIntelItem(
                timestamp = "25 min ago",
                description = "Connection attempt from TOR exit node (IP: 98.76.54.32) was blocked.",
                targetDevice = "Target: Firewall-Main"
            )
        )

        // Creates fake graph data points.
        _trafficData.value = listOf(
            ChartDataPoint("6:00 PM", 2.5f, 1.8f),
            ChartDataPoint("6:15 PM", 2.2f, 1.5f),
            ChartDataPoint("6:30 PM", 1.9f, 1.2f),
            ChartDataPoint("6:45 PM", 1.8f, 1.1f),
            ChartDataPoint("Now", 1.6f, 0.9f)
        )
    }
}