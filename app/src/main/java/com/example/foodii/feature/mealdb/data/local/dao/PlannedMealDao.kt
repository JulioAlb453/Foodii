package com.example.foodii.feature.mealdb.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannedMealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlannedMeal(meal: PlannedMealEntity)

    @Update
    suspend fun updatePlannedMeal(meal: PlannedMealEntity)

    @Query("UPDATE planned_meals SET date = :newDate WHERE id = :id AND userId = :userId")
    suspend fun updateMealDate(id: Int, newDate: Long, userId: String)

    @Query("SELECT * FROM planned_meals WHERE userId = :userId ORDER BY date ASC")
    fun getAllPlannedMeals(userId: String): Flow<List<PlannedMealEntity>>

    @Query("SELECT * FROM planned_meals WHERE userId = :userId AND date >= :start AND date <= :end")
    suspend fun getPlannedMealsInRange(userId: String, start: Long, end: Long): List<PlannedMealEntity>

    @Query("SELECT * FROM planned_meals WHERE id = :id AND userId = :userId")
    suspend fun getPlannedMealById(id: Int, userId: String): PlannedMealEntity?

    @Delete
    suspend fun deleteMeal(meal: PlannedMealEntity)
}
