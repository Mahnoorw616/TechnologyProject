package com.example.ainoc.ui.screens.explorer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorerScreen(navController: NavController, viewModel: ExplorerViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column {
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search by name, IP, or tag...", color = AccentBeige.copy(alpha = 0.7f)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryPurple) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = CardBackground,
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = AccentBeige,
                unfocusedTextColor = AccentBeige
            )
        )

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.filteredDevices, key = { it.id }) { device ->
                DeviceCard(device = device, onClick = {
                    navController.navigate("device_details/${device.id}")
                })
            }
        }
    }
}

@Composable
private fun DeviceCard(device: Device, onClick: () -> Unit) {
    val scale by animateFloatAsState(targetValue = 1f, label = "scale")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = remember { NoRippleInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(device.type.icon, contentDescription = device.type.name, tint = PrimaryPurple, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(device.name, fontWeight = FontWeight.Bold, color = AccentBeige)
                Text(device.ipAddress, color = AccentBeige.copy(alpha = 0.8f))
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    device.tags.take(3).forEach { tag ->
                        Text(
                            text = tag,
                            modifier = Modifier
                                .border(1.dp, PrimaryPurple, RoundedCornerShape(50))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            color = AccentBeige,
                            fontSize = 10.sp
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }
            Spacer(Modifier.width(16.dp))
            SparklineChart(
                data = device.cpuTrend,
                health = device.health,
                modifier = Modifier.size(width = 80.dp, height = 40.dp)
            )
        }
    }
}

@Composable
private fun SparklineChart(data: List<Float>, health: DeviceHealth, modifier: Modifier) {
    val color = when (health) {
        DeviceHealth.NORMAL -> AccentBeige
        DeviceHealth.WARNING -> WarningYellow
        DeviceHealth.CRITICAL -> CriticalRed
    }
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas
        val path = Path()
        val stepX = size.width / (data.size - 1)
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height * (1 - value)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color = color, style = Stroke(width = 2.dp.toPx()))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailsScreen(deviceId: String, navController: NavController, viewModel: ExplorerViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val details = uiState.deviceDetails
    var selectedTimeRange by remember { mutableStateOf("1H") }

    LaunchedEffect(deviceId) {
        viewModel.loadDeviceDetails(deviceId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(details?.baseInfo?.name ?: "Loading...", color = AccentBeige) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = AccentBeige)
                    }
                },
                actions = {
                    details?.let {
                        Icon(
                            Icons.Filled.Circle,
                            contentDescription = "Status",
                            tint = if (it.baseInfo.health != DeviceHealth.CRITICAL) HealthyGreen else CriticalRed,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoadingDetails) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryPurple)
            }
        } else {
            details?.let { d ->
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item { AssetInfoCard(details = d) }
                    item { HistoricalPerformanceCard(selectedTimeRange, onRangeSelected = { selectedTimeRange = it }) }
                    item { RecentActivityCard(alerts = d.recentActivity, navController = navController) }
                }
            }
        }
    }
}

@Composable
private fun AssetInfoCard(details: DeviceDetails) {
    Card(colors = CardDefaults.cardColors(containerColor = CardBackground)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Asset Details", style = MaterialTheme.typography.titleLarge, color = AccentBeige)
            Divider(color = BackgroundDark)
            DetailRow("IP Address:", details.baseInfo.ipAddress)
            DetailRow("Device Type:", details.baseInfo.type.name)
            DetailRow("Function:", details.function)
            DetailRow("Criticality:", "${details.criticality} / 10")
            DetailRow("Maintenance:", details.maintenance ?: "None Scheduled")
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row {
        Text(label, fontWeight = FontWeight.Bold, color = AccentBeige.copy(alpha = 0.7f), modifier = Modifier.width(120.dp))
        Text(value, color = AccentBeige)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoricalPerformanceCard(selectedTimeRange: String, onRangeSelected: (String) -> Unit) {
    val timeRanges = listOf("1H", "6H", "24H", "7D", "Custom")
    Card(colors = CardDefaults.cardColors(containerColor = CardBackground)) {
        Column(Modifier.padding(16.dp)) {
            Text("Performance Explorer", style = MaterialTheme.typography.titleLarge, color = AccentBeige)
            Spacer(Modifier.height(16.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                timeRanges.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = timeRanges.size),
                        onClick = { onRangeSelected(label) },
                        selected = label == selectedTimeRange,
                        interactionSource = remember { NoRippleInteractionSource() }
                    ) {
                        Text(label)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("CPU Utilization (%) - Chart Placeholder", color = AccentBeige)
                Text("Memory Usage (%) - Chart Placeholder", color = AccentBeige)
                Text("Network Throughput (Mbps) - Chart Placeholder", color = AccentBeige)
            }
        }
    }
}

@Composable
private fun RecentActivityCard(alerts: List<Alert>, navController: NavController) {
    Card(colors = CardDefaults.cardColors(containerColor = CardBackground)) {
        Column(Modifier.padding(16.dp)) {
            Text("Recent Activity", style = MaterialTheme.typography.titleLarge, color = AccentBeige)
            Spacer(Modifier.height(16.dp))
            if (alerts.isEmpty()) {
                Text("No recent alerts for this device.", color = AccentBeige.copy(alpha = 0.7f))
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    alerts.forEach { alert ->
                        AlertCard(alert = alert, onClick = {
                            navController.navigate(Screen.AlertDetails.createRoute(alert.id))
                        })
                    }
                }
            }
        }
    }
}