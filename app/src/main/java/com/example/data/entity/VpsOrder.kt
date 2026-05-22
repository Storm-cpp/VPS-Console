package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vps_orders")
data class VpsOrder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val planName: String,
    val vCpu: Int,
    val ramGb: Int,
    val diskGb: Int,
    val durationMonths: Int = 1,
    val region: String,
    val osDistribution: String,
    val priceMonthly: Double,
    val orderStatus: String = "PENDING", // PENDING, DEPLOYING, ACTIVE, CANCELLED
    val orderTimestamp: Long = System.currentTimeMillis()
)
