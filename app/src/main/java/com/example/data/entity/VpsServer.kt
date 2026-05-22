package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vps_servers")
data class VpsServer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val ipAddress: String,
    val port: Int = 22,
    val username: String = "root",
    val status: String = "ONLINE", // ONLINE, OFFLINE, REBOOTING, EXPIRING
    val cpuUsage: Float = 0f,
    val ramUsage: Float = 0f,
    val diskUsage: Float = 0f,
    val bandwidthIn: Double = 0.0,
    val bandwidthOut: Double = 0.0,
    val location: String = "Germany, Frankfurt",
    val os: String = "Ubuntu 24.04 LTS",
    val ramGb: Int = 4,
    val vCpu: Int = 2,
    val diskGb: Int = 80,
    val createdAt: Long = System.currentTimeMillis()
)
