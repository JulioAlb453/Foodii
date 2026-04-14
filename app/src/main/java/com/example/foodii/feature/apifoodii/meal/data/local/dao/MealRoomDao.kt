package com.example.foodii.feature.apifoodii.meal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodii.feature.apifoodii.meal.data.local.entity.MealRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealRoomDao {
    @Query("SELECT * FROM meals")
    fun getAllMeals(): Flow<List<MealRoomEntity>>

    @Query("SELECT * FROM meals WHERE createdBy = :userId")
    fun getMealsByUser(userId: String): Flow<List<MealRoomEntity>>

    @Query("SELECT * FROM meals WHERE date = :date")
    fun getMealsByDate(date: String): Flow<List<MealRoomEntity>>

    @Query("SELECT * FROM meals WHERE date BETWEEN :startDate AND :endDate")
    fun getMealsByDateRange(startDate: String, endDate: String): Flow<List<MealRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealRoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<MealRoomEntity>)

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: String): MealRoomEntity?

    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteMeal(id: String)
}
