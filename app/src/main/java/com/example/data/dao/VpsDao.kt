package com.example.data.dao

import androidx.room.*
import com.example.data.entity.AppSetting
import com.example.data.entity.VpsOrder
import com.example.data.entity.VpsServer
import kotlinx.coroutines.flow.Flow

@Dao
interface VpsDao {

    // Servers
    @Query("SELECT * FROM vps_servers ORDER BY createdAt DESC")
    fun getAllServers(): Flow<List<VpsServer>>

    @Query("SELECT * FROM vps_servers WHERE id = :id")
    suspend fun getServerById(id: Int): VpsServer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: VpsServer)

    @Update
    suspend fun updateServer(server: VpsServer)

    @Delete
    suspend fun deleteServer(server: VpsServer)

    @Query("DELETE FROM vps_servers WHERE id = :id")
    suspend fun deleteServerById(id: Int)

    // Orders
    @Query("SELECT * FROM vps_orders ORDER BY orderTimestamp DESC")
    fun getAllOrders(): Flow<List<VpsOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: VpsOrder)

    @Update
    suspend fun updateOrder(order: VpsOrder)

    @Query("DELETE FROM vps_orders WHERE id = :id")
    suspend fun deleteOrderById(id: Int)

    // Settings
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AppSetting?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSetting(setting: AppSetting)
}
