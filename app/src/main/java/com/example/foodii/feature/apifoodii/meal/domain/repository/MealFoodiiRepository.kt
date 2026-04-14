package com.example.foodii.feature.apifoodii.meal.domain.repository

import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MealFoodiiRepository {
    fun findAll(userId: String): Flow<List<FoodiiMeal>>
    fun findByDate(date: String, userId: String): Flow<List<FoodiiMeal>>
    fun findByDateRange(startDate: String, endDate: String, userId: String): Flow<List<FoodiiMeal>>
    
    suspend fun saveMeal(meal: FoodiiMeal)
    suspend fun createMealOnServer(
        name: String,
        date: LocalDate,
        mealTime: FoodiiMealTime,
        ingredients: List<Pair<String, Int>>,
        steps: List<String>,
        userId: String,
        image: String?,
        categories: List<String> = emptyList()
    ): Result<FoodiiMeal>

    suspend fun getMealById(id: String, userId: String): FoodiiMeal?
    suspend fun deleteMeal(id: String, userId: String)
}
