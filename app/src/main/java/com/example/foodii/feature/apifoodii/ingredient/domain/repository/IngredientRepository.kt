package com.example.foodii.feature.apifoodii.ingredient.domain.repository

import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import kotlinx.coroutines.flow.Flow

interface IngredientRepository {
    suspend fun createIngredient(ingredient: Ingredient): Ingredient
    suspend fun findById(id: String): Ingredient?
    fun findByUser(userId: String): Flow<List<Ingredient>>
    suspend fun deleteIngredient(id: String, userId: String): Boolean
}
