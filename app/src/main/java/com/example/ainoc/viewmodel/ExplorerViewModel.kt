package com.example.ainoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainoc.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

// Holds the current state of the Explorer screen UI.
// This includes the filtered list of devices, the search text, and details for a selected device.
data class ExplorerUiState(
    val filteredDevices: List<Device> = emptyList(),
    val searchQuery: String = "",
    val deviceDetails: DeviceDetails? = null,
    val isLoadingDetails: Boolean = false
)

// This ViewModel manages the list of network devices.
// It handles searching, filtering, tagging critical assets, and loading details.
@HiltViewModel
class ExplorerViewModel @Inject constructor() : ViewModel() {

    // Holds the complete list of all devices (hidden from the UI).
    private val _allDevices = MutableStateFlow<List<Device>>(emptyList())
    // Holds the text currently typed in the search bar.
    private val _searchQuery = MutableStateFlow("")

    // The public state that the UI observes to know what to draw.
    private val _uiState = MutableStateFlow(ExplorerUiState())
    val uiState = _uiState.asStateFlow()

    // A list of full details for every device (used for the details screen).
    private var fullDeviceDetailsList: List<DeviceDetails>

    // Runs when the screen is first created.
    init {
        // Loads fake device data.
        fullDeviceDetailsList = createDummyDevices()
        // Extracts just the summary info for the main list.
        _allDevices.value = fullDeviceDetailsList.map { it.baseInfo }

        // This logic handles the search bar filtering.
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Waits for the user to stop typing for 300ms before searching.
                .combine(_allDevices) { query, devices ->
                    if (query.isBlank()) {
                        // If search is empty, show everything.
                        devices
                    } else {
                        // Otherwise, filter the list by name, IP, or tags.
                        devices.filter {
                            it.name.contains(query, ignoreCase = true) ||
                                    it.ipAddress.contains(query, ignoreCase = true) ||
                                    it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
                        }
                    }
                }
                .collect { filtered ->
                    // Updates the UI with the filtered results.
                    _uiState.update { it.copy(filteredDevices = filtered) }
                }
        }
    }

    // Call this when the user types in the search bar.
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    // New Function: Toggles the "Critical Asset" status (Star icon) for a device.
    fun toggleDeviceCriticality(deviceId: String) {
        val updatedList = _allDevices.value.map { device ->
            if (device.id == deviceId) {
                device.copy(isCritical = !device.isCritical)
            } else {
                device
            }
        }
        _allDevices.value = updatedList

        // Also update the details view if it's currently open
        if (_uiState.value.deviceDetails?.baseInfo?.id == deviceId) {
            val currentDetails = _uiState.value.deviceDetails
            if (currentDetails != null) {
                val updatedDetails = currentDetails.copy(
                    baseInfo = currentDetails.baseInfo.copy(isCritical = !currentDetails.baseInfo.isCritical)
                )
                _uiState.update { it.copy(deviceDetails = updatedDetails) }
            }
        }
    }

    // Call this to fetch details for a specific device ID (simulates a network call).
    fun loadDeviceDetails(deviceId: String) {
        viewModelScope.launch {
            // Show loading spinner.
            _uiState.update { it.copy(isLoadingDetails = true, deviceDetails = null) }
            kotlinx.coroutines.delay(500) // Fake delay.

            // Find the device in our local list to ensure we get the latest "isCritical" status
            val deviceSummary = _allDevices.value.find { it.id == deviceId }
            val staticDetails = fullDeviceDetailsList.find { d -> d.baseInfo.id == deviceId }

            // Merge the live summary state with the static details
            val finalDetails = if (deviceSummary != null && staticDetails != null) {
                staticDetails.copy(baseInfo = deviceSummary)
            } else {
                staticDetails
            }

            // Update UI with the found device details.
            _uiState.update {
                it.copy(
                    isLoadingDetails = false,
                    deviceDetails = finalDetails
                )
            }
        }
    }

    // Generates fake data for testing the app.
    private fun createDummyDevices(): List<DeviceDetails> {
        val recentAlerts = listOf(
            Alert("CRITICAL-001", AlertPriority.CRITICAL, "Suspected Brute-Force Attack", "Primary-DB-Server", 10, "2m ago", AlertStatus.NEW),
            Alert("HIGH-001", AlertPriority.HIGH, "Anomalous Outbound Traffic", "Web-Server-03", 8, "15m ago", AlertStatus.NEW)
        )

        // Creates fake graph data for the device details page.
        val chartData = mapOf(
            "1H" to List(12) { ChartDataPoint("${60 - it * 5}m", Random.nextFloat(), Random.nextFloat()) },
            "6H" to List(12) { ChartDataPoint("${6 - it * 0.5}h", Random.nextFloat(), Random.nextFloat()) },
            "24H" to List(24) { ChartDataPoint("${24 - it}h", Random.nextFloat(), Random.nextFloat()) }
        )

        // Returns a list of detailed devices.
        return listOf(
            DeviceDetails(
                baseInfo = Device("1", "Primary-DB-Server", "10.0.0.50", DeviceType.SERVER, listOf("Mission-Critical", "Production", "PCI"), DeviceHealth.CRITICAL, List(20) { Random.nextFloat() }, isCritical = true),
                function = "Primary Database", criticality = 10, maintenance = null, historicalData = chartData, recentActivity = recentAlerts
            ),
            DeviceDetails(
                baseInfo = Device("2", "WAN-Router-01", "192.168.1.1", DeviceType.ROUTER, listOf("Production", "Core-Infra"), DeviceHealth.WARNING, List(20) { Random.nextFloat() }),
                function = "Main WAN Gateway", criticality = 8, maintenance = "Active until 4:00 AM", historicalData = chartData, recentActivity = emptyList()
            ),
            DeviceDetails(
                baseInfo = Device("3", "Web-Server-03", "10.0.1.20", DeviceType.SERVER, listOf("Production", "Web-Farm"), DeviceHealth.NORMAL, List(20) { Random.nextFloat() }),
                function = "Public Web Server", criticality = 7, maintenance = null, historicalData = chartData, recentActivity = recentAlerts.drop(1)
            ),
            DeviceDetails(
                baseInfo = Device("4", "Corp-Firewall-HQ", "10.1.0.1", DeviceType.FIREWALL, listOf("Core-Infra", "Security"), DeviceHealth.NORMAL, List(20) { Random.nextFloat() }),
                function = "Corporate Headquarters Firewall", criticality = 9, maintenance = null, historicalData = chartData, recentActivity = emptyList()
            )
        )
    }
}