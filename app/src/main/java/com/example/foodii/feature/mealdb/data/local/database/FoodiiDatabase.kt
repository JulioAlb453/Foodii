package com.example.foodii.feature.mealdb.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.foodii.feature.mealdb.data.local.dao.PlannedMealDao
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity

@Database(entities = [PlannedMealEntity::class], version = 2, exportSchema = false) // Incrementado a versión 2
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
                    .fallbackToDestructiveMigration() // Esto borrará los datos antiguos y creará la nueva tabla
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
