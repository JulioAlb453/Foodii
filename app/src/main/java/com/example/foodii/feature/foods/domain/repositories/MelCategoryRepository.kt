package com.example.foodii.feature.foods.domain.repositories

import com.example.foodii.feature.planner.domain.entity.MealDetail
import com.example.foodii.feature.foods.domain.entity.Category
import com.example.foodii.feature.planner.data.local.entity.PlannedMealEntity
import kotlinx.coroutines.flow.Flow

interface MelCategoryRepository {
    suspend fun getCategories(): List<Category>
    suspend fun getMealsByLetter(letter: String): List<MealDetail>

}