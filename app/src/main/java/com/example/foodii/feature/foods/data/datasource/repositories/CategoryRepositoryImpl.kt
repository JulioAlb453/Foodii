package com.example.foodii.feature.foods.data.datasource.repositories

import com.example.foodii.core.network.MealApi
import com.example.foodii.feature.foods.data.datasource.remote.mapper.toDomain
import com.example.foodii.feature.foods.data.datasource.remote.mapper.toMealDetail
import com.example.foodii.feature.foods.domain.entity.MealDetail
import com.example.foodii.feature.foods.domain.entity.Category
import com.example.foodii.feature.foods.domain.repositories.MelCategoryRepository

class CategoryRepositoryImpl(
    private val api: MealApi):
    MelCategoryRepository {
    override suspend fun getCategories(): List<Category> {
        val response = api.getCategories()
        return response.meals.map { it.toDomain() }
    }

    override suspend fun getMealsByLetter(letter: String): List<MealDetail>{
        val response = api.searchMealsByFirstLetter(letter)
        return response.meals.map { it.toMealDetail() }

    }
}