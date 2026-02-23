package com.example.foodii.feature.mealdb.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannedMealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlannedMeal(meal: PlannedMealEntity)

    @Query("SELECT * FROM planned_meals ORDER BY date ASC")
    fun getAllPlannedMeals(): Flow<List<PlannedMealEntity>>

    @Delete
    suspend fun deleteMeal(meal: PlannedMealEntity)
}