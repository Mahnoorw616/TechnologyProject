package com.example.ainoc.ui.screens.alerts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ainoc.data.model.*
import com.example.ainoc.ui.components.CommentItem
import com.example.ainoc.ui.components.EvidenceCard
import com.example.ainoc.ui.navigation.Screen
import com.example.ainoc.ui.theme.*
import com.example.ainoc.util.NoRippleInteractionSource
import com.example.ainoc.viewmodel.AdvancedFilterState
import com.example.ainoc.viewmodel.AlertsViewModel
import kotlinx.coroutines.launch

// This screen shows the main list of all alerts.
// You can search, filter, and tap on an alert to see more details.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsListScreen(navController: NavController, viewModel: AlertsViewModel = hiltViewModel()) {
    // Watches the live list of alerts and filter settings.
    val uiState by viewModel.uiState.collectAsState()
    val smartFilters by remember { mutableStateOf(viewModel.smartFilters) }
    val activeFilter by viewModel.activeSmartFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Shows the advanced filter popup if the user clicked the filter icon.
    if (uiState.isAdvancedFilterVisible) {
        AdvancedFilterModal(
            filterState = uiState.advancedFilterState,
            onDismiss = { viewModel.hideAdvancedFilter() },
            onReset = { viewModel.resetAdvancedFilters() },
            onApply = { viewModel.applyAdvancedFilters() },
            onPriorityChange = { priority, isChecked -> viewModel.updateAdvancedFilterPriorities(priority, isChecked) },
            onStatusChange = { status, isChecked -> viewModel.updateAdvancedFilterStatuses(status, isChecked) }
        )
    }

    Scaffold(
        topBar = {
            // Switches between the regular title bar and the search bar.
            if (uiState.isSearchActive) {
                SearchAppBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    onClose = { viewModel.toggleSearch() }
                )
            } else {
                DefaultAppBar(
                    onSearchClick = { viewModel.toggleSearch() },
                    onFilterClick = { viewModel.showAdvancedFilter() }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // A horizontal scrolling row of filter buttons (e.g., "Critical", "New").
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                smartFilters.forEach { filter ->
                    val isSelected = filter == activeFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onSmartFilterClicked(filter) },
                        label = { Text(filter) },
                        enabled = true,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surface,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        interactionSource = remember { NoRippleInteractionSource() },
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            // The scrolling list of alert cards.
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredAlerts, key = { it.id }) { alert ->
                    AlertCard(alert = alert, onClick = {
                        navController.navigate(Screen.AlertDetails.createRoute(alert.id))
                    })
                }
            }
        }
    }
}

// Shows the standard title "Alerts" and the Search/Filter buttons.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultAppBar(onSearchClick: () -> Unit, onFilterClick: () -> Unit) {
    TopAppBar(
        title = { Text("Alerts", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(
                onClick = onSearchClick,
                interactionSource = remember { NoRippleInteractionSource() }
            ) {
                Icon(Icons.Default.Search, "Search", tint = MaterialTheme.colorScheme.onBackground)
            }
            IconButton(
                onClick = onFilterClick,
                interactionSource = remember { NoRippleInteractionSource() }
            ) {
                Icon(Icons.Default.FilterList, "Filter", tint = MaterialTheme.colorScheme.onBackground)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

// Shows the search bar with a text input field.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAppBar(query: String, onQueryChange: (String) -> Unit, onClose: () -> Unit) {
    val focusManager = LocalFocusManager.current
    TopAppBar(
        title = {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search...", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    containerColor = Color.Transparent
                )
            )
        },
        navigationIcon = {
            Icon(Icons.Default.Search, "Search", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(start = 16.dp))
        },
        actions = {
            IconButton(
                onClick = {
                    if (query.isNotEmpty()) onQueryChange("") else {
                        focusManager.clearFocus()
                        onClose()
                    }
                },
                interactionSource = remember { NoRippleInteractionSource() }
            ) {
                Icon(Icons.Default.Close, "Close", tint = MaterialTheme.colorScheme.onBackground)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

// Draws a single alert card in the list.
@Composable
fun AlertCard(alert: Alert, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { NoRippleInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Colored priority strip on the left edge.
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(alert.priority.color)
            )
            // Main content of the card.
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        alert.title,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Target: ${alert.device}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        alert.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                Spacer(Modifier.width(8.dp))
                // Colored status badge.
                StatusTag(status = alert.status)
            }
        }
    }
}

// Draws the small colored status pill (e.g., "New", "Resolved").
@Composable
private fun StatusTag(status: AlertStatus, modifier: Modifier = Modifier) {
    val (color, textColor) = when (status) {
        AlertStatus.NEW -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        AlertStatus.ACKNOWLEDGED -> Color.Gray to Color.White
        AlertStatus.RESOLVED -> HealthyGreen to Color.White
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurface
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.name,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

// Shows the advanced filter popup dialog.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvancedFilterModal(
    filterState: AdvancedFilterState,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
    onPriorityChange: (AlertPriority, Boolean) -> Unit,
    onStatusChange: (AlertStatus, Boolean) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Advanced Filters", color = MaterialTheme.colorScheme.onBackground) },
                    navigationIcon = {
                        IconButton(
                            onClick = onDismiss,
                            interactionSource = remember { NoRippleInteractionSource() }
                        ) {
                            Icon(Icons.Default.Close, "Close", tint = MaterialTheme.colorScheme.onBackground)
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = onReset,
                            interactionSource = remember { NoRippleInteractionSource() }
                        ) {
                            Text("Reset", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            bottomBar = {
                Button(
                    onClick = onApply,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    interactionSource = remember { NoRippleInteractionSource() }
                ) {
                    Text("Apply Filters", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text("Priority", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                    AlertPriority.values().forEach { priority ->
                        FilterCheckboxRow(
                            text = priority.name,
                            checked = priority in filterState.priorities,
                            onCheckedChange = { onPriorityChange(priority, it) }
                        )
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
                item {
                    Text("Status", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                    AlertStatus.values().forEach { status ->
                        FilterCheckboxRow(
                            text = status.name,
                            checked = status in filterState.statuses,
                            onCheckedChange = { onStatusChange(status, it) }
                        )
                    }
                }
            }
        }
    }
}

// Draws a single checkbox row in the filter dialog.
@Composable
private fun FilterCheckboxRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { NoRippleInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
            .padding(vertical = 8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            ),
            interactionSource = remember { NoRippleInteractionSource() }
        )
        Spacer(Modifier.width(8.dp))
        Text(text, color = MaterialTheme.colorScheme.onBackground)
    }
}

// This screen shows the full details of a specific alert.
// It includes actions like Assign, Acknowledge, and a log of events.
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AlertDetailsScreen(alertId: String, navController: NavController, viewModel: AlertsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val details = uiState.alertDetails
    var statusDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(alertId) {
        viewModel.loadAlertDetails(alertId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(details?.baseInfo?.title ?: "Alert Details", color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        interactionSource = remember { NoRippleInteractionSource() }
                    ) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    details?.let {
                        Box {
                            // Status tag that opens a dropdown menu.
                            StatusTag(
                                status = it.baseInfo.status,
                                modifier = Modifier.clickable(
                                    interactionSource = remember { NoRippleInteractionSource() },
                                    indication = null
                                ) { statusDropdownExpanded = true }
                            )
                            DropdownMenu(
                                expanded = statusDropdownExpanded,
                                onDismissRequest = { statusDropdownExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Acknowledge", color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        viewModel.updateAlertStatus(AlertStatus.ACKNOWLEDGED)
                                        statusDropdownExpanded = false
                                    },
                                    interactionSource = remember { NoRippleInteractionSource() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Resolve", color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        viewModel.updateAlertStatus(AlertStatus.RESOLVED)
                                        statusDropdownExpanded = false
                                    },
                                    interactionSource = remember { NoRippleInteractionSource() }
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        details?.let { d ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Info Card with buttons.
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Affected Asset: ${d.baseInfo.device} (IP: ${d.affectedAssetIp})", color = MaterialTheme.colorScheme.onSurface)
                            Text("Alert Time: ${d.alertTime}", color = MaterialTheme.colorScheme.onSurface)
                            Text("Fusion Engine Score: ${d.fusionScore} (Critical)", color = CriticalRed, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(16.dp))

                            // Action Buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                // Assign Button
                                Button(
                                    onClick = { viewModel.assignAlert() },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    contentPadding = PaddingValues(horizontal = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    interactionSource = remember { NoRippleInteractionSource() }
                                ) {
                                    Text("Assign", color = MaterialTheme.colorScheme.onPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                                }
                                // Acknowledge Button
                                Button(
                                    onClick = { viewModel.updateAlertStatus(AlertStatus.ACKNOWLEDGED) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    contentPadding = PaddingValues(horizontal = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    interactionSource = remember { NoRippleInteractionSource() }
                                ) {
                                    Text("Acknowledge", color = MaterialTheme.colorScheme.onPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                                }
                                // False Positive Button
                                Button(
                                    onClick = { viewModel.updateAlertStatus(AlertStatus.FALSE_POSITIVE) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = WarningYellow),
                                    contentPadding = PaddingValues(horizontal = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    interactionSource = remember { NoRippleInteractionSource() }
                                ) {
                                    Text("False Positive", color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }

                item {
                    EvidenceCard("AI Insight", { Icon(Icons.Default.Lightbulb, null, tint = MaterialTheme.colorScheme.primary) }) {
                        Text(d.aiInsight, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                item {
                    UnifiedEvidenceSection(details = d)
                }

                item {
                    EvidenceCard("Recommended Response Plan", { Icon(Icons.Default.Checklist, null, tint = MaterialTheme.colorScheme.primary) }) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            d.recommendedActions.forEachIndexed { index, action ->
                                Text("${index + 1}. $action", color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text("Activity & Comments", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.height(16.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            d.activityLog.forEach { logItem ->
                                when (logItem) {
                                    is ActivityLogItem.Event -> Text("${logItem.timestamp} - ${logItem.description}", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                                    is ActivityLogItem.Comment -> CommentItem(comment = Comment(logItem.author, logItem.timestamp, logItem.text))
                                }
                            }
                        }
                    }
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

// Shows tabs for graphs and logs.
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UnifiedEvidenceSection(details: AlertDetails) {
    val tabs = remember {
        listOfNotNull(
            "Performance".takeIf { details.performanceData.isNotEmpty() },
            "Logs".takeIf { details.logEvidence.isNotEmpty() },
            "Threat Intel".takeIf { details.threatIntel != null }
        )
    }
    if (tabs.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    // FIX: Ensure text stays on one line
                    text = { Text(title, maxLines = 1, overflow = TextOverflow.Visible, softWrap = false) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    interactionSource = remember { NoRippleInteractionSource() }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        ) { page ->
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (tabs.getOrNull(page)) {
                    "Performance" -> PerformanceChart(data = details.performanceData)
                    "Logs" -> Text(details.logEvidence.joinToString("\n"), color = MaterialTheme.colorScheme.onSurface)
                    "Threat Intel" -> details.threatIntel?.let {
                        Text("IP: ${it.ipAddress}\nReputation: ${it.threatReputation}", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

// Draws a manual chart for performance data.
@Composable
fun PerformanceChart(data: List<ChartDataPoint>) {
    if (data.isEmpty()) {
        Text("No Data", color = MaterialTheme.colorScheme.onSurface)
        return
    }

    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

    Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        val width = size.width
        val height = size.height

        // Draw basic grid lines
        drawLine(gridColor, Offset(0f, height), Offset(width, height))
        drawLine(gridColor, Offset(0f, height * 0.5f), Offset(width, height * 0.5f))
        drawLine(gridColor, Offset(0f, 0f), Offset(width, 0f))

        // Basic Line Chart Logic
        val points = data.mapIndexed { index, point ->
            val x = (width / (data.size - 1).coerceAtLeast(1)) * index
            val y = height - (point.inbound * height) // Dummy normalization, assumes value 0-1
            Offset(x, y)
        }

        if (points.isNotEmpty()) {
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.forEach { lineTo(it.x, it.y) }
            }
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw points
            points.forEach { offset ->
                drawCircle(Color.White, radius = 3.dp.toPx(), center = offset)
            }
        }
    }
}