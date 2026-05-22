package com.example.data

import com.example.data.dao.VpsDao
import com.example.data.entity.AppSetting
import com.example.data.entity.VpsOrder
import com.example.data.entity.VpsServer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.random.Random

class VpsRepository(private val vpsDao: VpsDao) {

    val allServers: Flow<List<VpsServer>> = vpsDao.getAllServers()
    val allOrders: Flow<List<VpsOrder>> = vpsDao.getAllOrders()
    val appSettings: Flow<AppSetting?> = vpsDao.getSettings()

    suspend fun getServerById(id: Int): VpsServer? = vpsDao.getServerById(id)

    suspend fun addServer(server: VpsServer) {
        vpsDao.insertServer(server)
    }

    suspend fun updateServer(server: VpsServer) {
        vpsDao.updateServer(server)
    }

    suspend fun deleteServer(server: VpsServer) {
        vpsDao.updateServer(server.copy(status = "OFFLINE"))
        vpsDao.deleteServer(server)
    }

    suspend fun deleteServerById(id: Int) {
        vpsDao.deleteServerById(id)
    }

    suspend fun createOrder(order: VpsOrder) {
        vpsDao.insertOrder(order)
    }

    suspend fun updateOrder(order: VpsOrder) {
        vpsDao.updateOrder(order)
    }

    suspend fun deleteOrder(id: Int) {
        vpsDao.deleteOrderById(id)
    }

    suspend fun updateSettings(setting: AppSetting) {
        vpsDao.insertOrUpdateSetting(setting)
    }

    // Advanced Simulator Action: Fluctuate CPU/RAM/Bandwidth randomly
    suspend fun simulateServerMetrics() {
        val servers = allServers.first()
        for (server in servers) {
            if (server.status == "ONLINE") {
                val cpuDelta = Random.nextFloat() * 10f - 5f // +/- 5%
                val ramDelta = Random.nextFloat() * 4f - 2f // +/- 2%
                val trafficDeltaIn = Random.nextDouble() * 1.5 // MBs
                val trafficDeltaOut = Random.nextDouble() * 2.0 // MBs

                val newCpu = (server.cpuUsage + cpuDelta).coerceIn(5f, 98f)
                val newRam = (server.ramUsage + ramDelta).coerceIn(15f, 92f)
                val newBandwidthIn = (server.bandwidthIn + trafficDeltaIn).coerceAtLeast(0.1)
                val newBandwidthOut = (server.bandwidthOut + trafficDeltaOut).coerceAtLeast(0.1)

                vpsDao.updateServer(
                    server.copy(
                        cpuUsage = newCpu,
                        ramUsage = newRam,
                        bandwidthIn = newBandwidthIn,
                        bandwidthOut = newBandwidthOut
                    )
                )
            }
        }
    }

    // Advanced Simulator Action: Auto-process pending orders and build real servers from them
    suspend fun processPendingOrders() {
        val orders = allOrders.first()
        for (order in orders) {
            when (order.orderStatus) {
                "PENDING" -> {
                    // Update state to Deploying
                    vpsDao.insertOrder(order.copy(orderStatus = "DEPLOYING"))
                }
                "DEPLOYING" -> {
                    // Turn deploying order into an active connected server, mark order active!
                    val ip = "185.${Random.nextInt(10, 254)}.${Random.nextInt(5, 254)}.${Random.nextInt(1, 254)}"
                    val newServer = VpsServer(
                        name = "${order.planName} (${order.region.substringBefore(",")})",
                        ipAddress = ip,
                        username = "root",
                        status = "ONLINE",
                        cpuUsage = Random.nextFloat() * 15f + 10f,
                        ramUsage = Random.nextFloat() * 20f + 15f,
                        diskUsage = Random.nextFloat() * 5f + 10f,
                        bandwidthIn = Random.nextDouble() * 5.0 + 1.0,
                        bandwidthOut = Random.nextDouble() * 5.0 + 1.0,
                        location = order.region,
                        os = order.osDistribution,
                        ramGb = order.ramGb,
                        vCpu = order.vCpu,
                        diskGb = order.diskGb
                    )
                    vpsDao.insertServer(newServer)
                    vpsDao.insertOrder(order.copy(orderStatus = "ACTIVE"))
                }
            }
        }
    }

    // Seed default servers and settings if database is empty
    suspend fun seedDefaultsIfNeeded() {
        val currentServers = allServers.first()
        val currentSettings = appSettings.first()

        if (currentSettings == null) {
            vpsDao.insertOrUpdateSetting(AppSetting())
        }

        if (currentServers.isEmpty()) {
            val defaultServers = listOf(
                VpsServer(
                    name = "Primary Kubernetes Master",
                    ipAddress = "172.105.48.23",
                    port = 22,
                    username = "kube-admin",
                    status = "ONLINE",
                    cpuUsage = 42.4f,
                    ramUsage = 68.1f,
                    diskUsage = 52.3f,
                    bandwidthIn = 124.5,
                    bandwidthOut = 289.1,
                    location = "Europe Central (Frankfurt)",
                    os = "Ubuntu 24.04 LTS",
                    ramGb = 8,
                    vCpu = 4,
                    diskGb = 160
                ),
                VpsServer(
                    name = "MySQL Replication Slave",
                    ipAddress = "139.162.110.144",
                    port = 10022,
                    username = "db-operator",
                    status = "ONLINE",
                    cpuUsage = 12.8f,
                    ramUsage = 84.5f,
                    diskUsage = 79.2f,
                    bandwidthIn = 3452.1,
                    bandwidthOut = 125.8,
                    location = "North America West (Oregon)",
                    os = "CentOS Stream 9",
                    ramGb = 16,
                    vCpu = 8,
                    diskGb = 500
                ),
                VpsServer(
                    name = "Edge Proxy Router",
                    ipAddress = "45.79.18.52",
                    port = 22,
                    username = "root",
                    status = "OFFLINE",
                    cpuUsage = 0f,
                    ramUsage = 0f,
                    diskUsage = 34.1f,
                    bandwidthIn = 0.0,
                    bandwidthOut = 0.0,
                    location = "Asia Pacific East (Tokyo)",
                    os = "Debian 12 Bookworm",
                    ramGb = 2,
                    vCpu = 1,
                    diskGb = 40
                )
            )
            for (server in defaultServers) {
                vpsDao.insertServer(server)
            }
        }
    }
}
