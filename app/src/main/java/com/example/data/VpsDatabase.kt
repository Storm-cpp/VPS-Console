package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.VpsDao
import com.example.data.entity.AppSetting
import com.example.data.entity.VpsOrder
import com.example.data.entity.VpsServer

@Database(
    entities = [VpsServer::class, VpsOrder::class, AppSetting::class],
    version = 1,
    exportSchema = false
)
abstract class VpsDatabase : RoomDatabase() {

    abstract fun vpsDao(): VpsDao

    companion object {
        @Volatile
        private var INSTANCE: VpsDatabase? = null

        fun getDatabase(context: Context): VpsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VpsDatabase::class.java,
                    "vps_console_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
