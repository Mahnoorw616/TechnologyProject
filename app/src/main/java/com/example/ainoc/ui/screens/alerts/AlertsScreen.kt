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
import androidx.compose.ui.graphics.Brush
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsListScreen(navController: NavController, viewModel: AlertsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val smartFilters by remember { mutableStateOf(viewModel.smartFilters) }
    val activeFilter by viewModel.activeSmartFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

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
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Smart Filters Row
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
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryPurple,
                            containerColor = CardBackground,
                            selectedLabelColor = AccentBeige,
                            labelColor = AccentBeige
                        ),
                        interactionSource = remember { NoRippleInteractionSource() },
                        border = null
                    )
                }
            }

            // Alerts List
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultAppBar(onSearchClick: () -> Unit, onFilterClick: () -> Unit) {
    TopAppBar(
        title = { Text("Alerts", color = AccentBeige, fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(
                onClick = onSearchClick,
                interactionSource = remember { NoRippleInteractionSource() }
            ) {
                Icon(Icons.Default.Search, "Search", tint = AccentBeige)
            }
            IconButton(
                onClick = onFilterClick,
                interactionSource = remember { NoRippleInteractionSource() }
            ) {
                Icon(Icons.Default.FilterList, "Filter", tint = AccentBeige)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
    )
}

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
                placeholder = { Text("Search...", color = AccentBeige.copy(alpha = 0.7f)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = AccentBeige,
                    unfocusedTextColor = AccentBeige,
                    cursorColor = PrimaryPurple,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    containerColor = Color.Transparent
                )
            )
        },
        navigationIcon = {
            Icon(Icons.Default.Search, "Search", tint = AccentBeige, modifier = Modifier.padding(start = 16.dp))
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
                Icon(Icons.Default.Close, "Close Search", tint = AccentBeige)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
    )
}

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
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Priority Strip
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(alert.priority.color)
            )
            // Content
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
                        color = AccentBeige,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Target: ${alert.device}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentBeige.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        alert.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.width(8.dp))
                StatusTag(status = alert.status)
            }
        }
    }
}

@Composable
private fun StatusTag(status: AlertStatus, modifier: Modifier = Modifier) {
    val (color, textColor) = when (status) {
        AlertStatus.NEW -> PrimaryPurple to AccentBeige
        AlertStatus.ACKNOWLEDGED -> Color.Gray to AccentBeige
        AlertStatus.RESOLVED -> HealthyGreen to BackgroundDark
        else -> CardBackground to AccentBeige
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
                    title = { Text("Advanced Filters", color = AccentBeige) },
                    navigationIcon = {
                        IconButton(
                            onClick = onDismiss,
                            interactionSource = remember { NoRippleInteractionSource() }
                        ) {
                            Icon(Icons.Default.Close, "Close", tint = AccentBeige)
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = onReset,
                            interactionSource = remember { NoRippleInteractionSource() }
                        ) {
                            Text("Reset", color = PrimaryPurple)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
                )
            },
            bottomBar = {
                Button(
                    onClick = onApply,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    interactionSource = remember { NoRippleInteractionSource() }
                ) {
                    Text("Apply Filters", color = AccentBeige)
                }
            },
            containerColor = BackgroundDark
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text("Priority", style = MaterialTheme.typography.titleMedium, color = AccentBeige)
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
                    Text("Status", style = MaterialTheme.typography.titleMedium, color = AccentBeige)
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
                checkedColor = PrimaryPurple,
                uncheckedColor = AccentBeige.copy(alpha = 0.6f),
                checkmarkColor = AccentBeige
            ),
            interactionSource = remember { NoRippleInteractionSource() }
        )
        Spacer(Modifier.width(8.dp))
        Text(text, color = AccentBeige)
    }
}

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
                title = { Text(details?.baseInfo?.title ?: "Alert Details", color = AccentBeige, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        interactionSource = remember { NoRippleInteractionSource() }
                    ) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = AccentBeige)
                    }
                },
                actions = {
                    details?.let {
                        Box {
                            StatusTag(
                                status = it.baseInfo.status,
                                modifier = Modifier.clickable(
                                    interactionSource = remember { NoRippleInteractionSource() },
                                    indication = null
                                ) { statusDropdownExpanded = true }
                            )
                            DropdownMenu(
                                expanded = statusDropdownExpanded,
                                onDismissRequest = { statusDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Acknowledge", color = AccentBeige) },
                                    onClick = {
                                        viewModel.updateAlertStatus(AlertStatus.ACKNOWLEDGED)
                                        statusDropdownExpanded = false
                                    },
                                    interactionSource = remember { NoRippleInteractionSource() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Resolve", color = AccentBeige) },
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { paddingValues ->
        details?.let { d ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Info Card
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = CardBackground)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Affected Asset: ${d.baseInfo.device} (IP: ${d.affectedAssetIp})", color = AccentBeige)
                            Text("Alert Time: ${d.alertTime}", color = AccentBeige)
                            Text("Fusion Engine Score: ${d.fusionScore} (Critical)", color = CriticalRed, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(16.dp))

                            // Buttons Row with Adjusted Alignment
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = {},
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                    contentPadding = PaddingValues(horizontal = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    interactionSource = remember { NoRippleInteractionSource() }
                                ) {
                                    Text("Assign", color = AccentBeige, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                                }
                                Button(
                                    onClick = { viewModel.updateAlertStatus(AlertStatus.ACKNOWLEDGED) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                                    contentPadding = PaddingValues(horizontal = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    interactionSource = remember { NoRippleInteractionSource() }
                                ) {
                                    Text("Acknowledge", color = AccentBeige, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                                }
                                Button(
                                    onClick = {},
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = WarningYellow),
                                    contentPadding = PaddingValues(horizontal = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    interactionSource = remember { NoRippleInteractionSource() }
                                ) {
                                    Text("False Positive", color = BackgroundDark, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }

                item {
                    EvidenceCard("AI Insight", { Icon(Icons.Default.Lightbulb, null, tint = PrimaryPurple) }) {
                        Text(d.aiInsight, color = AccentBeige)
                    }
                }

                item {
                    UnifiedEvidenceSection(details = d)
                }

                item {
                    EvidenceCard("Recommended Response Plan", { Icon(Icons.Default.Checklist, null, tint = PrimaryPurple) }) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            d.recommendedActions.forEachIndexed { index, action ->
                                Text("${index + 1}. $action", color = AccentBeige)
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text("Activity & Comments", style = MaterialTheme.typography.titleMedium, color = AccentBeige)
                        Spacer(Modifier.height(16.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            d.activityLog.forEach { logItem ->
                                when (logItem) {
                                    is ActivityLogItem.Event -> Text("${logItem.timestamp} - ${logItem.description}", color = AccentBeige.copy(alpha = 0.7f))
                                    is ActivityLogItem.Comment -> CommentItem(comment = Comment(logItem.author, logItem.timestamp, logItem.text))
                                }
                            }
                        }
                    }
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryPurple)
        }
    }
}

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
            containerColor = BackgroundDark,
            contentColor = PrimaryPurple,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = PrimaryPurple
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
                    selectedContentColor = PrimaryPurple,
                    unselectedContentColor = AccentBeige.copy(alpha = 0.7f),
                    interactionSource = remember { NoRippleInteractionSource() }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(200.dp) // Height increased for chart
        ) { page ->
            Box(
                Modifier
                    .fillMaxSize()
                    .background(CardBackground)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (tabs.getOrNull(page)) {
                    "Performance" -> PerformanceChart(data = details.performanceData)
                    "Logs" -> Text(details.logEvidence.joinToString("\n"), color = AccentBeige)
                    "Threat Intel" -> details.threatIntel?.let {
                        Text("IP: ${it.ipAddress}\nReputation: ${it.threatReputation}", color = AccentBeige)
                    }
                }
            }
        }
    }
}

/**
 * Custom dummy chart for Alert Details "Performance" tab.
 */
@Composable
fun PerformanceChart(data: List<ChartDataPoint>) {
    if (data.isEmpty()) {
        Text("No Data", color = AccentBeige)
        return
    }

    val lineColor = PrimaryPurple
    val gridColor = Color.Gray.copy(alpha = 0.3f)

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