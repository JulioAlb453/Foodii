package com.example.foodii.feature.planner.domain.repository

import com.example.foodii.feature.planner.domain.entity.MealDetail
import com.example.foodii.feature.planner.data.local.entity.PlannedMealEntity
import kotlinx.coroutines.flow.Flow

interface PlannerRepository {
    suspend fun planMeal(meal: MealDetail, date: Long)

    fun getPlannedMeals(): Flow<List<PlannedMealEntity>>
}