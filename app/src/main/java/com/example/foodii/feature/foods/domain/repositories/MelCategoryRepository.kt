package com.example.foodii.feature.foods.domain.repositories

import com.example.foodii.feature.foods.domain.entity.MealDetail
import com.example.foodii.feature.foods.domain.entity.Category

interface MelCategoryRepository {
    suspend fun getCategories(): List<Category>
    suspend fun getMealsByLetter(letter: String): List<MealDetail>
}