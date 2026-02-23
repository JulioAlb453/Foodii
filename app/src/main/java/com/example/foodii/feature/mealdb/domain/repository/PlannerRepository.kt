package com.example.foodii.feature.mealdb.domain.repository

import com.example.foodii.feature.mealdb.domain.entity.MealDetail
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import kotlinx.coroutines.flow.Flow

interface PlannerRepository {
    suspend fun planMeal(meal: MealDetail, date: Long)

    suspend fun getMealsByLetter(letter: String): List<MealDetail>

    fun getPlannedMeals(): Flow<List<PlannedMealEntity>>
}
