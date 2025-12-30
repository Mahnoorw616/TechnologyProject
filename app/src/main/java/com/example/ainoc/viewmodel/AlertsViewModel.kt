package com.example.ainoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainoc.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdvancedFilterState(
    val priorities: Set<AlertPriority> = emptySet(),
    val statuses: Set<AlertStatus> = emptySet()
)

data class AlertsUiState(
    val filteredAlerts: List<Alert> = emptyList(),
    val isSearchActive: Boolean = false,
    val isAdvancedFilterVisible: Boolean = false,
    val advancedFilterState: AdvancedFilterState = AdvancedFilterState(),
    val alertDetails: AlertDetails? = null
)

@HiltViewModel
class AlertsViewModel @Inject constructor() : ViewModel() {

    private val _allAlerts = MutableStateFlow<List<Alert>>(emptyList())
    private val _activeSmartFilter = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")
    private val _advancedFilterState = MutableStateFlow(AdvancedFilterState())

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState = _uiState.asStateFlow()
    val smartFilters = listOf("All", "New", "Critical", "High")
    val activeSmartFilter = _activeSmartFilter.asStateFlow()
    val searchQuery = _searchQuery.asStateFlow()

    private val fullAlertDetailsList: List<AlertDetails>

    init {
        fullAlertDetailsList = createDummyAlerts()
        val baseAlerts = fullAlertDetailsList.map { it.baseInfo }
        _allAlerts.value = baseAlerts

        viewModelScope.launch {
            combine(_allAlerts, _activeSmartFilter, _searchQuery, _advancedFilterState) { alerts, smartFilter, query, advancedFilters ->
                var filteredList = alerts

                // 1. Apply Smart Filter
                if (smartFilter != "All") {
                    when (smartFilter) {
                        "New" -> filteredList = filteredList.filter { it.status == AlertStatus.NEW }
                        "Critical" -> filteredList = filteredList.filter { it.priority == AlertPriority.CRITICAL }
                        "High" -> filteredList = filteredList.filter { it.priority == AlertPriority.HIGH }
                    }
                }

                // 2. Apply Search Query
                if (query.isNotBlank()) {
                    filteredList = filteredList.filter {
                        it.title.contains(query, ignoreCase = true) || it.device.contains(query, ignoreCase = true)
                    }
                }

                // 3. Apply Advanced Filters
                // Logic: If the set is NOT empty, we filter. If empty, we show all (effectively ignored).
                if (advancedFilters.priorities.isNotEmpty()) {
                    filteredList = filteredList.filter { it.priority in advancedFilters.priorities }
                }
                if (advancedFilters.statuses.isNotEmpty()) {
                    filteredList = filteredList.filter { it.status in advancedFilters.statuses }
                }

                _uiState.update { it.copy(filteredAlerts = filteredList, advancedFilterState = advancedFilters) }
            }.collect()
        }
    }

    fun onSmartFilterClicked(filter: String) {
        _activeSmartFilter.value = filter
        _advancedFilterState.value = AdvancedFilterState() // Reset advanced filters on smart filter click
    }

    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }

    fun toggleSearch() {
        if (_uiState.value.isSearchActive) _searchQuery.value = ""
        _uiState.update { it.copy(isSearchActive = !it.isSearchActive) }
    }

    fun showAdvancedFilter() { _uiState.update { it.copy(isAdvancedFilterVisible = true) } }
    fun hideAdvancedFilter() { _uiState.update { it.copy(isAdvancedFilterVisible = false) } }

    fun updateAdvancedFilterPriorities(priority: AlertPriority, isChecked: Boolean) {
        _advancedFilterState.update { current ->
            val newPriorities = if (isChecked) current.priorities + priority else current.priorities - priority
            current.copy(priorities = newPriorities)
        }
    }

    fun updateAdvancedFilterStatuses(status: AlertStatus, isChecked: Boolean) {
        _advancedFilterState.update { current ->
            val newStatuses = if (isChecked) current.statuses + status else current.statuses - status
            current.copy(statuses = newStatuses)
        }
    }

    fun resetAdvancedFilters() { _advancedFilterState.value = AdvancedFilterState() }

    fun applyAdvancedFilters() {
        // When applying advanced filters, we reset the smart filter to "All" to avoid confusion
        _activeSmartFilter.value = "All"
        hideAdvancedFilter()
    }

    fun loadAlertDetails(alertId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(alertDetails = null) }
            kotlinx.coroutines.delay(300)
            _uiState.update { it.copy(alertDetails = fullAlertDetailsList.find { a -> a.baseInfo.id == alertId }) }
        }
    }

    fun updateAlertStatus(newStatus: AlertStatus) {
        _uiState.update { state ->
            state.alertDetails?.copy(
                baseInfo = state.alertDetails.baseInfo.copy(status = newStatus),
                activityLog = state.alertDetails.activityLog + ActivityLogItem.Event("Now", "Status changed to ${newStatus.name}")
            )?.let { updatedDetails -> state.copy(alertDetails = updatedDetails) } ?: state
        }
    }

    private fun createDummyAlerts(): List<AlertDetails> {
        // Dummy data generation from original file
        return listOf(
            AlertDetails(
                baseInfo = Alert("CRITICAL-001", AlertPriority.CRITICAL, "Suspected Brute-Force Attack", "Primary-DB-Server", 10, "2m ago", AlertStatus.NEW),
                affectedAssetIp = "10.0.0.50", alertTime = "Dec 21, 2025, 7:17 PM", fusionScore = 22.55,
                aiInsight = "AI Analysis: A high-volume Failed Login event was detected, immediately followed by an anomalous CPU spike. This pattern strongly indicates an active brute-force attack.",
                timelineEvents = listOf(
                    TimelineEvent("7:15 PM", "First failed login attempt.", isFirst = true),
                    TimelineEvent("7:16 PM", "Log anomaly score increased."),
                    TimelineEvent("7:17 PM", "Performance anomaly detected.", isLast = true)
                ),
                performanceData = listOf(ChartDataPoint("7:10 PM", 0.1f, 0.1f)),
                logEvidence = listOf("Failed password for user 'admin' from 198.51.100.200", "Failed password for user 'root' from 198.51.100.200"),
                threatIntel = ThreatIntel("198.51.100.200", "Russia", "Known Botnet C2", "https://www.alienvault.com/"),
                recommendedActions = listOf("Immediately block source IP (198.51.100.200) at firewall.", "Investigate 'admin' account compromise.", "Review SSH firewall rules."),
                activityLog = listOf(ActivityLogItem.Event("7:17 PM", "Alert Generated"))
            ),
            AlertDetails(
                baseInfo = Alert("HIGH-001", AlertPriority.HIGH, "Anomalous Outbound Traffic", "Web-Server-03", 8, "15m ago", AlertStatus.NEW),
                affectedAssetIp = "10.0.1.20", alertTime = "Dec 21, 2025, 7:04 PM", fusionScore = 18.2,
                aiInsight = "This server initiated an outbound connection to a non-standard port, coinciding with a spike in egress traffic. The destination IP is not on a known threat list, but the behavior is anomalous.",
                timelineEvents = listOf(TimelineEvent("7:04 PM", "Outbound traffic anomaly.", isFirst = true, isLast = true)),
                performanceData = emptyList(), logEvidence = emptyList(), threatIntel = null,
                recommendedActions = listOf("Verify the process responsible for the connection.", "Ensure no unauthorized software is running."),
                activityLog = listOf(ActivityLogItem.Event("7:04 PM", "Alert Generated"))
            ),
            AlertDetails(
                baseInfo = Alert("MEDIUM-012", AlertPriority.MEDIUM, "High Network Latency", "WAN-Router-01", 7, "28m ago", AlertStatus.ACKNOWLEDGED),
                affectedAssetIp = "192.168.1.1", alertTime = "Dec 21, 2025, 6:50 PM", fusionScore = 12.4,
                aiInsight = "Network latency is outside of normal operational parameters for this time of day.",
                timelineEvents = listOf(TimelineEvent("6:50 PM", "Round-trip time to gateway exceeded 150ms.", isFirst = true, isLast = true)),
                performanceData = emptyList(), logEvidence = emptyList(), threatIntel = null,
                recommendedActions = listOf("Monitor device for further latency spikes.", "Contact ISP if issue persists."),
                activityLog = listOf(
                    ActivityLogItem.Event("6:50 PM", "Alert Generated"),
                    ActivityLogItem.Event("6:55 PM", "Acknowledged by Network Admin"),
                    ActivityLogItem.Comment("Network Admin", "6:58 PM", "Investigating potential ISP issue. Pings are stable now.")
                )
            ),
            AlertDetails(
                baseInfo = Alert("LOW-105", AlertPriority.LOW, "New Device on Guest VLAN", "Wifi-AP-Lobby", 3, "1h ago", AlertStatus.RESOLVED),
                affectedAssetIp = "192.168.10.112", alertTime = "Dec 21, 2025, 6:15 PM", fusionScore = 4.1,
                aiInsight = "A new, previously unseen device has joined the network.",
                timelineEvents = listOf(TimelineEvent("6:15 PM", "MAC Address AA:BB:CC:11:22:33 connected.", isFirst = true, isLast = true)),
                performanceData = emptyList(), logEvidence = emptyList(), threatIntel = null,
                recommendedActions = listOf("No action required if device is expected."),
                activityLog = listOf(
                    ActivityLogItem.Event("6:15 PM", "Alert Generated"),
                    ActivityLogItem.Comment("Sys Admin", "6:20 PM", "Confirmed it's a guest's new laptop. Marked as resolved.")
                )
            ),
            AlertDetails(
                baseInfo = Alert("CRITICAL-002", AlertPriority.CRITICAL, "Potential Ransomware Activity", "File-Server-01", 10, "2h ago", AlertStatus.ACKNOWLEDGED),
                affectedAssetIp = "10.0.2.15", alertTime = "Dec 21, 2025, 5:22 PM", fusionScore = 25.8,
                aiInsight = "AI Analysis: A high velocity of file read/write/rename operations was detected, consistent with ransomware behavior. A known malicious process 'crypt.exe' was also identified.",
                timelineEvents = listOf(TimelineEvent("5:22 PM", "Anomalous file system activity detected.", isFirst = true, isLast = true)),
                performanceData = emptyList(), logEvidence = listOf("Process 'crypt.exe' accessed C:\\Shares\\Finance"),
                threatIntel = null,
                recommendedActions = listOf("ISOLATE THE HOST FROM THE NETWORK IMMEDIATELY.", "Initiate incident response protocol.", "Do not power off the machine."),
                activityLog = listOf(
                    ActivityLogItem.Event("5:22 PM", "Alert Generated"),
                    ActivityLogItem.Event("5:23 PM", "Acknowledged by Security Officer")
                )
            )
        )
    }
}