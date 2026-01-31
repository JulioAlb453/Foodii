package com.example.foodii.feature.planner.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.foodii.feature.planner.data.local.dao.PlannedMealDao
import com.example.foodii.feature.planner.data.local.entity.PlannedMealEntity

@Database(entities = [PlannedMealEntity::class], version = 1, exportSchema = false)
abstract class FoodiiDatabase : RoomDatabase() {

    abstract fun plannedMealDao(): PlannedMealDao

    companion object {
        @Volatile
        private var Instance: FoodiiDatabase? = null

        fun getDatabase(context: Context): FoodiiDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    FoodiiDatabase::class.java,
                    "foodii_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}