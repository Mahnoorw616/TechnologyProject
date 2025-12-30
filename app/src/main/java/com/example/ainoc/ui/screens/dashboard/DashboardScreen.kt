package com.example.ainoc.ui.screens.dashboard

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ainoc.ui.components.*
import com.example.ainoc.viewmodel.DashboardViewModel

// This is the main home screen of the app.
// It connects to the data source (ViewModel) and organizes all the widgets (charts, orbs, lists) into a scrolling page.
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {

    // This creates a tool to ask the user for permission to show notifications.
    // We need this because Android requires explicit permission for alerts.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Handle permission result */ }

    // This block runs once when the screen opens.
    // It checks if the phone is running Android 13 or newer, and if so, asks for notification permission.
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // These variables watch the live data from the ViewModel.
    // If the network health, alerts, or traffic data changes, the screen automatically updates to show the new info.
    val networkStatus by viewModel.networkStatus.collectAsState()
    val alertSummary by viewModel.alertSummary.collectAsState()
    val hotspots by viewModel.hotspots.collectAsState()
    val threatIntelFeed by viewModel.threatIntelFeed.collectAsState()
    val trafficData by viewModel.trafficData.collectAsState()

    // This creates a scrolling list layout.
    // We use this because the dashboard content might be taller than the phone screen.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Adds the big animated pulsing circle (Health Orb) to the top of the list.
        item {
            HealthOrb(status = networkStatus)
        }

        // Adds a row of three small cards (KPIs) showing Uptime, Latency, and Packet speed.
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Shows the Uptime percentage.
                KpiWidget(
                    title = "Uptime",
                    value = "99.99%",
                    icon = Icons.Default.Wifi,
                    trend = "Stable",
                    isTrendPositive = true,
                    modifier = Modifier.weight(1f)
                )
                // Shows the average delay (latency).
                KpiWidget(
                    title = "Avg Latency",
                    value = "24ms",
                    icon = Icons.Default.Timer,
                    trend = "-2ms",
                    isTrendPositive = true,
                    modifier = Modifier.weight(1f)
                )
                // Shows the data speed.
                KpiWidget(
                    title = "Packets",
                    value = "12Gb/s",
                    icon = Icons.Default.Speed,
                    trend = "+15%",
                    isTrendPositive = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Adds the card that counts how many Critical, High, Medium, and Low alerts exist.
        item {
            LiveAlertsSummaryCard(summary = alertSummary)
        }

        // Adds the graph showing network traffic over time.
        item {
            NetworkThroughputCard(data = trafficData)
        }

        // Adds the list of devices that are having multiple problems ("Hotspots").
        item {
            CorrelatedHotspotsCard(hotspots = hotspots)
        }

        // Adds the news feed of external security threats.
        item {
            RecentThreatIntelCard(feed = threatIntelFeed)
        }
    }
}