package com.example.foodii.feature.mealdb.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.foodii.feature.apifoodii.ingredient.data.local.dao.IngredientRoomDao
import com.example.foodii.feature.apifoodii.ingredient.data.local.entity.IngredientRoomEntity
import com.example.foodii.feature.apifoodii.meal.data.local.dao.MealRoomDao
import com.example.foodii.feature.apifoodii.meal.data.local.entity.MealRoomEntity
import com.example.foodii.feature.mealdb.data.local.dao.PlannedMealDao
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity

@Database(
    entities = [
        PlannedMealEntity::class,
        MealRoomEntity::class,
        IngredientRoomEntity::class
    ],
    version = 8, // Incrementado por eliminación de food_categories
    exportSchema = false
)

@Database(entities = [
    PlannedMealEntity::class,
    MealRoomEntity::class,
    IngredientRoomEntity::class],
    version = 7,
    exportSchema = false)

abstract class FoodiiDatabase : RoomDatabase() {

    abstract fun plannedMealDao(): PlannedMealDao
    abstract fun mealDao(): MealRoomDao
    abstract fun ingredientDao(): IngredientRoomDao

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