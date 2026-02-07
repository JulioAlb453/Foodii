package com.example.foodii.feature.apifoodii.meal.domain.repository

import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import kotlinx.coroutines.flow.Flow

interface MealFoodiiRepository {
    fun findAll(userId: String): Flow<List<FoodiiMeal>>
    fun findByDate(date: String, userId: String): Flow<List<FoodiiMeal>>
    fun findByDateRange(startDate: String, endDate: String, userId: String): Flow<List<FoodiiMeal>>
    
    suspend fun saveMeal(meal: FoodiiMeal)
    suspend fun getMealById(id: String, userId: String): FoodiiMeal?
    suspend fun deleteMeal(id: String, userId: String)
}
