package com.example.ainoc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainoc.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class ExplorerUiState(
    val filteredDevices: List<Device> = emptyList(),
    val searchQuery: String = "",
    val deviceDetails: DeviceDetails? = null,
    val isLoadingDetails: Boolean = false
)

@HiltViewModel
class ExplorerViewModel @Inject constructor() : ViewModel() {

    private val _allDevices = MutableStateFlow<List<Device>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    private val _uiState = MutableStateFlow(ExplorerUiState())
    val uiState = _uiState.asStateFlow()

    private val fullDeviceDetailsList: List<DeviceDetails>

    init {
        fullDeviceDetailsList = createDummyDevices()
        _allDevices.value = fullDeviceDetailsList.map { it.baseInfo }

        // Reactive pipeline for search
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Debounce to avoid filtering on every keystroke
                .combine(_allDevices) { query, devices ->
                    if (query.isBlank()) {
                        devices
                    } else {
                        devices.filter {
                            it.name.contains(query, ignoreCase = true) ||
                                    it.ipAddress.contains(query, ignoreCase = true) ||
                                    it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
                        }
                    }
                }
                .collect { filtered ->
                    _uiState.update { it.copy(filteredDevices = filtered) }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun loadDeviceDetails(deviceId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetails = true, deviceDetails = null) }
            kotlinx.coroutines.delay(500) // Simulate network load
            _uiState.update {
                it.copy(
                    isLoadingDetails = false,
                    deviceDetails = fullDeviceDetailsList.find { d -> d.baseInfo.id == deviceId }
                )
            }
        }
    }

    private fun createDummyDevices(): List<DeviceDetails> {
        val recentAlerts = listOf(
            Alert("CRITICAL-001", AlertPriority.CRITICAL, "Suspected Brute-Force Attack", "Primary-DB-Server", 10, "2m ago", AlertStatus.NEW),
            Alert("HIGH-001", AlertPriority.HIGH, "Anomalous Outbound Traffic", "Web-Server-03", 8, "15m ago", AlertStatus.NEW)
        )

        val chartData = mapOf(
            "1H" to List(12) { ChartDataPoint("${60 - it * 5}m", Random.nextFloat(), Random.nextFloat()) },
            "6H" to List(12) { ChartDataPoint("${6 - it * 0.5}h", Random.nextFloat(), Random.nextFloat()) },
            "24H" to List(24) { ChartDataPoint("${24 - it}h", Random.nextFloat(), Random.nextFloat()) }
        )

        return listOf(
            DeviceDetails(
                baseInfo = Device("1", "Primary-DB-Server", "10.0.0.50", DeviceType.SERVER, listOf("Mission-Critical", "Production", "PCI"), DeviceHealth.CRITICAL, List(20) { Random.nextFloat() }),
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