package com.example.foodii.feature.apifoodii.meal.domain.repository

import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import kotlinx.coroutines.flow.Flow

interface FoodiiMealRepository {
    fun findAll(): Flow<List<FoodiiMeal>>
    fun findByDate(date: String): Flow<List<FoodiiMeal>>
    fun findByDateRange(startDate: String, endDate: String): Flow<List<FoodiiMeal>>
}