package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.VpsDatabase
import com.example.data.VpsRepository
import com.example.data.entity.AppSetting
import com.example.data.entity.VpsOrder
import com.example.data.entity.VpsServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class VpsViewModel(
    application: Application,
    private val repository: VpsRepository
) : AndroidViewModel(application) {

    // UI State for live items
    val servers: StateFlow<List<VpsServer>> = repository.allServers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<VpsOrder>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<AppSetting> = repository.appSettings
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSetting())

    // Active screen selection or connection states
    private val _selectedServerId = MutableStateFlow<Int?>(null)
    val selectedServerId: StateFlow<Int?> = _selectedServerId.asStateFlow()

    // Shell command logs or mock outputs
    private val _terminalLogs = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList()) // Pair of raw text, isError
    val terminalLogs: StateFlow<List<Pair<String, Boolean>>> = _terminalLogs.asStateFlow()

    init {
        // Seed defaults on start
        viewModelScope.launch {
            repository.seedDefaultsIfNeeded()
        }

        // Start live VPS telemetry simulator
        viewModelScope.launch {
            while (true) {
                delay(3000)
                val currentSettings = settings.value
                if (currentSettings.isSimulationActive) {
                    repository.simulateServerMetrics()
                    repository.processPendingOrders()
                }
            }
        }
    }

    // Actions
    fun selectServer(id: Int?) {
        _selectedServerId.value = id
        _terminalLogs.value = listOf(
            "Establishing connection to virtual shell..." to false,
            "ECDSA key fingerprint SHA256:f0p9AByK16F082Mvnq1a1E+T/zQe+0M... matches host." to false,
            "Connected as ${if (id != null) "admin" else "root"}. Active shell initialized!" to false
        )
    }

    fun addManualServer(name: String, ipAddress: String, username: String, os: String, location: String) {
        viewModelScope.launch {
            val s = VpsServer(
                name = name.ifBlank { "Remote Box" },
                ipAddress = ipAddress.ifBlank { "192.168.1.50" },
                username = username.ifBlank { "root" },
                status = "ONLINE",
                cpuUsage = Random.nextFloat() * 10f + 5f,
                ramUsage = Random.nextFloat() * 12f + 8f,
                diskUsage = Random.nextFloat() * 20f + 5f,
                bandwidthIn = 0.5,
                bandwidthOut = 0.8,
                location = location,
                os = os,
                ramGb = listOf(2, 4, 8, 16).random(),
                vCpu = listOf(1, 2, 4).random(),
                diskGb = listOf(40, 80, 160).random()
            )
            repository.addServer(s)
        }
    }

    fun deleteServer(server: VpsServer) {
        viewModelScope.launch {
            repository.deleteServer(server)
            if (_selectedServerId.value == server.id) {
                _selectedServerId.value = null
            }
        }
    }

    fun submitOrder(planName: String, vCpu: Int, ramGb: Int, diskGb: Int, duration: Int, region: String, os: String, price: Double) {
        viewModelScope.launch {
            val order = VpsOrder(
                planName = planName,
                vCpu = vCpu,
                ramGb = ramGb,
                diskGb = diskGb,
                durationMonths = duration,
                region = region,
                osDistribution = os,
                priceMonthly = price,
                orderStatus = "PENDING"
            )
            repository.createOrder(order)
        }
    }

    fun updateDeveloperMode(isEnabled: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            repository.updateSettings(current.copy(isDeveloperMode = isEnabled))
        }
    }

    fun updateSimulationActive(isActive: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            repository.updateSettings(current.copy(isSimulationActive = isActive))
        }
    }

    fun executeTerminalCommand(cmd: String) {
        if (cmd.isBlank()) return
        val currentLogs = _terminalLogs.value.toMutableList()
        currentLogs.add("$ root@vps-console:~# $cmd" to false)

        viewModelScope.launch {
            delay(400) // fake ping
            val output = when (cmd.lowercase().trim()) {
                "help" -> listOf(
                    "Standard commands: status, top, df -h, reboot, clear, systemctl restart vps-agent" to false
                )
                "status" -> listOf(
                    "● vps-agent.service - Live Remote Agent Daemon" to false,
                    "   Loaded: loaded (/etc/systemd/system/vps-agent.service; enabled; vendor preset: enabled)" to false,
                    "   Active: active (running) since Fri 2026-05-22 10:54:08 UTC; 2h ago" to false,
                    "   Tasks: 4 (limit: 4915)" to false
                )
                "top" -> {
                    val cpu = Random.nextInt(2, 95)
                    val ram = Random.nextInt(10, 85)
                    listOf(
                        "top - 10:54:12 up 2 days, 16:34,  1 user,  load average: 0.15, 0.08, 0.02" to false,
                        "Tasks: 92 total,   1 running,  91 sleeping" to false,
                        "%Cpu(s): $cpu.3 us,  2.1 sy,  0.0 ni, 95.1 id" to false,
                        "MiB Mem : 4096.0 total,  2158.4 free,  $ram.5% usage" to false
                    )
                }
                "df -h" -> listOf(
                    "Filesystem      Size  Used Avail Use% Mounted on" to false,
                    "/dev/sda1        78G   32G   46G  41% /" to false,
                    "tmpfs           2.0G     0  2.0G   0% /dev/shm" to false
                )
                "reboot" -> {
                    triggerReboot()
                    listOf(
                        "System reboot has been initiated successfully." to false,
                        "Connection closed by foreign host." to true
                    )
                }
                "clear" -> {
                    _terminalLogs.value = emptyList()
                    return@launch
                }
                "systemctl restart vps-agent" -> {
                    listOf(
                        "Stopping vps-agent.service..." to false,
                        "Starting vps-agent.service..." to false,
                        "vps-agent restarted successfully. New API sockets established." to false
                    )
                }
                else -> listOf(
                    "bash: $cmd: command not found" to true
                )
            }
            _terminalLogs.value = currentLogs + output
        }
    }

    private fun triggerReboot() {
        val serverId = _selectedServerId.value ?: return
        viewModelScope.launch {
            val serverList = servers.value
            val match = serverList.firstOrNull { it.id == serverId }
            if (match != null) {
                repository.updateServer(
                    match.copy(
                        status = "REBOOTING",
                        cpuUsage = 0f,
                        ramUsage = 0f,
                        bandwidthIn = 0.0,
                        bandwidthOut = 0.0
                    )
                )
                // wait 6 seconds and restore online
                delay(6000)
                val refreshed = repository.getServerById(serverId)
                if (refreshed != null && refreshed.status == "REBOOTING") {
                    repository.updateServer(
                        refreshed.copy(
                            status = "ONLINE",
                            cpuUsage = Random.nextFloat() * 12f + 8f,
                            ramUsage = Random.nextFloat() * 10f + 15f
                        )
                    )
                }
            }
        }
    }
}

class VpsViewModelFactory(
    private val application: Application,
    private val repository: VpsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VpsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VpsViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
