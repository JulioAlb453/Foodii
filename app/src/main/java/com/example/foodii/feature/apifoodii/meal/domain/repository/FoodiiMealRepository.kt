package com.example.foodii.feature.apifoodii.meal.domain.repository

import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import kotlinx.coroutines.flow.Flow

interface FoodiiMealRepository {
    suspend fun create(meal: FoodiiMeal): FoodiiMeal
    suspend fun findById(id: String): FoodiiMeal?
    fun findAll(): Flow<List<FoodiiMeal>>
    fun findByDate(date: String): Flow<List<FoodiiMeal>>
    fun findByDateRange(startDate: String, endDate: String): Flow<List<FoodiiMeal>>
    fun findByUserAndDate(userId: String, date: String): Flow<List<FoodiiMeal>>
    fun findByUser(userId: String): Flow<List<FoodiiMeal>>
    suspend fun delete(id: String, userId: String): Boolean
}