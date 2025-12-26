package com.example.ainoc.ui.screens.alerts

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
                            containerColor = CardBackground
                        )
                    )
                }
            }
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
            IconButton(onClick = onSearchClick) { Icon(Icons.Default.Search, "Search", tint = AccentBeige) }
            IconButton(onClick = onFilterClick) { Icon(Icons.Default.FilterList, "Filter", tint = AccentBeige) }
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
                placeholder = { Text("Search by title or device...", color = AccentBeige.copy(alpha = 0.7f)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = AccentBeige,
                    unfocusedTextColor = AccentBeige,
                    cursorColor = PrimaryPurple,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        },
        navigationIcon = { Icon(Icons.Default.Search, "Search", tint = AccentBeige, modifier = Modifier.padding(start = 16.dp)) },
        actions = {
            IconButton(onClick = {
                if (query.isNotEmpty()) onQueryChange("") else { focusManager.clearFocus(); onClose() }
            }) { Icon(Icons.Default.Close, "Close Search", tint = AccentBeige) }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
    )
}

@Composable
fun AlertCard(alert: Alert, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row {
            Box(modifier = Modifier.width(6.dp).fillMaxHeight().background(alert.priority.color))
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(alert.title, fontWeight = FontWeight.Bold, color = AccentBeige, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text("Target: ${alert.device} (Criticality: ${alert.deviceCriticality}/10)", style = MaterialTheme.typography.bodyMedium, color = AccentBeige.copy(alpha = 0.8f))
                    Spacer(Modifier.height(8.dp))
                    Text(alert.timestamp, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
    Text(
        text = status.name,
        modifier = modifier.clip(RoundedCornerShape(50)).background(color).padding(horizontal = 10.dp, vertical = 4.dp),
        color = textColor,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold
    )
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
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Advanced Filters", color = AccentBeige) },
                    navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close", tint = AccentBeige) } },
                    actions = { TextButton(onClick = onReset) { Text("Reset", color = PrimaryPurple) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
                )
            },
            bottomBar = {
                Button(onClick = onApply, modifier = Modifier.fillMaxWidth().padding(16.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)) {
                    Text("Apply Filters")
                }
            }
        ) { padding ->
            LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(16.dp)) {
                item {
                    Text("Priority", style = MaterialTheme.typography.titleMedium, color = AccentBeige)
                    AlertPriority.values().forEach { priority ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onPriorityChange(priority, priority !in filterState.priorities) }) {
                            Checkbox(checked = priority in filterState.priorities, onCheckedChange = { onPriorityChange(priority, it) })
                            Text(priority.name, color = AccentBeige)
                        }
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
                item {
                    Text("Status", style = MaterialTheme.typography.titleMedium, color = AccentBeige)
                    AlertStatus.values().forEach { status ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onStatusChange(status, status !in filterState.statuses) }) {
                            Checkbox(checked = status in filterState.statuses, onCheckedChange = { onStatusChange(status, it) })
                            Text(status.name, color = AccentBeige)
                        }
                    }
                }
            }
        }
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
                title = { Text(details?.baseInfo?.title ?: "Alert Details", color = AccentBeige) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back", tint = AccentBeige) } },
                actions = {
                    details?.let {
                        Box {
                            StatusTag(status = it.baseInfo.status, modifier = Modifier.clickable { statusDropdownExpanded = true })
                            DropdownMenu(expanded = statusDropdownExpanded, onDismissRequest = { statusDropdownExpanded = false }) {
                                DropdownMenuItem(text = { Text("Acknowledge") }, onClick = { viewModel.updateAlertStatus(AlertStatus.ACKNOWLEDGED); statusDropdownExpanded = false })
                                DropdownMenuItem(text = { Text("Resolve") }, onClick = { viewModel.updateAlertStatus(AlertStatus.RESOLVED); statusDropdownExpanded = false })
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { paddingValues ->
        details?.let { d ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = CardBackground)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Affected Asset: ${d.baseInfo.device} (IP: ${d.affectedAssetIp})", color = AccentBeige)
                            Text("Alert Time: ${d.alertTime}", color = AccentBeige)
                            Text("Fusion Engine Score: ${d.fusionScore} (Critical)", color = CriticalRed, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = {}, modifier = Modifier.weight(1f)) { Text("Assign") }
                                Button(onClick = { viewModel.updateAlertStatus(AlertStatus.ACKNOWLEDGED) }, modifier = Modifier.weight(1f)) { Text("Acknowledge") }
                                Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = WarningYellow)) { Text("False Positive") }
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
                            d.recommendedActions.forEachIndexed { index, action -> Text("${index + 1}. $action", color = AccentBeige) }
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
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryPurple) }
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
        TabRow(selectedTabIndex = pagerState.currentPage, containerColor = BackgroundDark) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) },
                    selectedContentColor = PrimaryPurple,
                    unselectedContentColor = AccentBeige.copy(alpha = 0.7f)
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(150.dp)
        ) { page ->
            Box(Modifier.fillMaxSize().background(CardBackground).padding(16.dp), contentAlignment = Alignment.Center) {
                when (tabs.getOrNull(page)) {
                    "Performance" -> Text("Chart placeholder", color = AccentBeige)
                    "Logs" -> Text(details.logEvidence.joinToString("\n"), color = AccentBeige)
                    "Threat Intel" -> details.threatIntel?.let { Text("IP: ${it.ipAddress}\nReputation: ${it.threatReputation}", color = AccentBeige) }
                }
            }
        }
    }
}