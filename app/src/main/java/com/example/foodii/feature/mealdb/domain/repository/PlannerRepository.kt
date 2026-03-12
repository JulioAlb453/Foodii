package com.example.foodii.feature.mealdb.domain.repository

import com.example.foodii.feature.mealdb.domain.entity.MealDetail
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import kotlinx.coroutines.flow.Flow

interface PlannerRepository {
    suspend fun planMeal(meal: MealDetail, date: Long, userId: String)

    suspend fun getMealsByLetter(letter: String): List<MealDetail>

    fun getPlannedMeals(userId: String): Flow<List<PlannedMealEntity>>

    suspend fun getPlannedMealsForDateRange(userId: String, start: Long, end: Long): List<PlannedMealEntity>

    suspend fun updatePlannedMealDate(id: Int, newDate: Long, userId: String)
}
