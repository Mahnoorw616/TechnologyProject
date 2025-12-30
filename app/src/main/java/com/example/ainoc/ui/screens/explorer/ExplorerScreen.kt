package com.example.ainoc.ui.screens.explorer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ainoc.data.model.Alert
import com.example.ainoc.data.model.Device
import com.example.ainoc.data.model.DeviceDetails
import com.example.ainoc.data.model.DeviceHealth
import com.example.ainoc.ui.navigation.Screen
import com.example.ainoc.ui.screens.alerts.AlertCard
import com.example.ainoc.ui.theme.*
import com.example.ainoc.util.NoRippleInteractionSource
import com.example.ainoc.viewmodel.ExplorerViewModel

// This screen allows the user to browse all the devices (servers, routers) in the network.
// It includes a search bar at the top and a scrolling list of devices.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorerScreen(navController: NavController, viewModel: ExplorerViewModel = hiltViewModel()) {
    // These variables watch the data: the list of devices and the current search text.
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column {
        // This is the search bar where the user can type a device name or IP.
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search...", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)) },
            leadingIcon = { Icon(Icons.Default.Search, "Search", tint = MaterialTheme.colorScheme.primary) },
            singleLine = true,
            // Closes the keyboard when the user presses the search button on the keyboard.
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        // This creates the scrolling list of devices.
        // It's efficient because it only draws the devices currently visible on the screen.
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(uiState.filteredDevices, key = { it.id }) { device ->
                // For each device in the list, draw a card.
                DeviceCard(device) {
                    // When clicked, navigate to the detailed view of that device.
                    navController.navigate("device_details/${device.id}")
                }
            }
        }
    }
}

// This draws a single row in the list representing one device.
// It shows an icon, the name, IP address, some tags, and a mini graph.
@Composable
private fun DeviceCard(device: Device, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(interactionSource = remember { NoRippleInteractionSource() }, indication = null, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Draws the icon (Server, Router, etc.).
            Icon(device.type.icon, device.type.name, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(16.dp))

            // This column holds the text info (Name, IP, Tags).
            Column(Modifier.weight(1f)) {
                Text(device.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(device.ipAddress, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))

                // Draws the small tags (e.g., "Production") below the name.
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    device.tags.take(3).forEach { tag ->
                        Text(
                            text = tag,
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }
            Spacer(Modifier.width(16.dp))
            // Draws the tiny line graph showing CPU usage.
            SparklineChart(device.cpuTrend, device.health, Modifier.size(80.dp, 40.dp))
        }
    }
}

// This function manually draws a small line chart (sparkline) using a Canvas.
// It's used on the device card to give a quick visual of health.
@Composable
private fun SparklineChart(data: List<Float>, health: DeviceHealth, modifier: Modifier) {
    // Sets the line color based on health (Red for Critical, text color for Normal).
    val color = when (health) {
        DeviceHealth.NORMAL -> MaterialTheme.colorScheme.onSurface
        DeviceHealth.WARNING -> WarningYellow
        DeviceHealth.CRITICAL -> CriticalRed
    }

    Canvas(modifier) {
        if (data.size < 2) return@Canvas
        val path = Path(); val stepX = size.width / (data.size - 1)
        // Connects the dots to form the line.
        data.forEachIndexed { i, v ->
            val x = i * stepX
            val y = size.height * (1 - v) // Inverts Y because canvas Y grows downwards.
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color, style = Stroke(width = 2.dp.toPx()))
    }
}

// This screen shows the full details of a single device.
// It appears when a user clicks a device in the Explorer list.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailsScreen(deviceId: String, navController: NavController, viewModel: ExplorerViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val details = uiState.deviceDetails
    var selectedTimeRange by remember { mutableStateOf("1H") }

    // Asks the ViewModel to fetch data for this specific device ID.
    LaunchedEffect(deviceId) { viewModel.loadDeviceDetails(deviceId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(details?.baseInfo?.name ?: "Loading...", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }, interactionSource = remember { NoRippleInteractionSource() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    // Shows a colored dot in the top right indicating status.
                    details?.let {
                        Icon(Icons.Filled.Circle, "Status", tint = if (it.baseInfo.health != DeviceHealth.CRITICAL) HealthyGreen else CriticalRed, modifier = Modifier.padding(end = 16.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoadingDetails) {
            // Show a spinner while data loads.
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
        } else {
            // Once data is loaded, show the details.
            details?.let { d ->
                LazyColumn(modifier = Modifier.padding(paddingValues), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    item { AssetInfoCard(d) }
                    item { HistoricalPerformanceCard(selectedTimeRange) { selectedTimeRange = it } }
                    item { RecentActivityCard(d.recentActivity, navController) }
                }
            }
        }
    }
}

// A card showing static details about the device (IP, Type, Role).
@Composable
private fun AssetInfoCard(details: DeviceDetails) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Asset Details", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            DetailRow("IP:", details.baseInfo.ipAddress); DetailRow("Type:", details.baseInfo.type.name); DetailRow("Function:", details.function); DetailRow("Criticality:", "${details.criticality}/10"); DetailRow("Maintenance:", details.maintenance ?: "None")
        }
    }
}

// Helper to draw a label and value row (e.g., "IP: 192.168.1.1").
@Composable
private fun DetailRow(label: String, value: String) {
    Row {
        Text(label, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.width(120.dp))
        Text(value, color = MaterialTheme.colorScheme.onSurface)
    }
}

// A card with buttons to select a time range (1H, 24H) and a placeholder for graphs.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoricalPerformanceCard(selectedTimeRange: String, onRangeSelected: (String) -> Unit) {
    val timeRanges = listOf("1H", "6H", "24H", "7D", "Custom")
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp)) {
            Text("Performance Explorer", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(16.dp))
            // Draws the row of toggle buttons.
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                timeRanges.forEachIndexed { i, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(i, timeRanges.size),
                        onClick = { onRangeSelected(label) },
                        selected = label == selectedTimeRange,
                        interactionSource = remember { NoRippleInteractionSource() },
                        colors = SegmentedButtonDefaults.colors(activeContainerColor = MaterialTheme.colorScheme.primary, activeContentColor = MaterialTheme.colorScheme.onPrimary, inactiveContainerColor = Color.Transparent, inactiveContentColor = MaterialTheme.colorScheme.onSurface)
                    ) { Text(label) }
                }
            }
            Spacer(Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) { Text("Charts Placeholder", color = MaterialTheme.colorScheme.onSurface) }
        }
    }
}

// A card listing recent alerts associated with this specific device.
@Composable
private fun RecentActivityCard(alerts: List<Alert>, navController: NavController) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp)) {
            Text("Recent Activity", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(16.dp))
            if (alerts.isEmpty()) Text("No alerts.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            else Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                alerts.forEach {
                    AlertCard(it) { navController.navigate(Screen.AlertDetails.createRoute(it.id)) }
                }
            }
        }
    }
}