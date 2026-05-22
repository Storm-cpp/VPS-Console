package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSetting(
    @PrimaryKey val id: Int = 1, // Singleton row setting
    val isDeveloperMode: Boolean = false,
    val isSimulationActive: Boolean = true,
    val lastUpdateNotification: Boolean = true,
    val selectedRegionDefault: String = "Europe Central"
)
