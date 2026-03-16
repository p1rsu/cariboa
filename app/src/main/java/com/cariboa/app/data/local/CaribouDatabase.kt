package com.cariboa.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cariboa.app.data.local.dao.TripDao
import com.cariboa.app.data.local.entity.TripEntity

@Database(entities = [TripEntity::class], version = 1, exportSchema = false)
abstract class CaribouDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}
