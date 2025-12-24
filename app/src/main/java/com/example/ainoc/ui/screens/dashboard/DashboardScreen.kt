package com.example.ainoc.ui.screens.dashboard

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ainoc.ui.components.*
import com.example.ainoc.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Handle permission result if needed */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val networkStatus by viewModel.networkStatus.collectAsState()
    val alertSummary by viewModel.alertSummary.collectAsState()
    val hotspots by viewModel.hotspots.collectAsState()
    val threatIntelFeed by viewModel.threatIntelFeed.collectAsState()
    val trafficData by viewModel.trafficData.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The DashboardHeader() is intentionally removed as the TopAppBar is now
        // handled by the MainScreen composable for a consistent UI.
        item {
            HealthOrb(status = networkStatus)
        }
        item {
            LiveAlertsSummaryCard(summary = alertSummary)
        }
        item {
            NetworkThroughputCard(data = trafficData)
        }
        item {
            CorrelatedHotspotsCard(hotspots = hotspots)
        }
        item {
            RecentThreatIntelCard(feed = threatIntelFeed)
        }
    }
}