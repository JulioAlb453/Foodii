package com.example.foodii.feature.apifoodii.domain.repository

import com.example.foodii.feature.apifoodii.domain.entity.FoodiiMeal
import kotlinx.coroutines.flow.Flow

interface FoodiiRepository {
    suspend fun saveMeal(meal: FoodiiMeal)
    fun getMeals(): Flow<List<FoodiiMeal>>
    suspend fun getMealById(id: String): FoodiiMeal?
    suspend fun deleteMeal(id: String)
}
