package com.example.foodii.feature.apifoodii.ingredient.domain.repository

import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient

interface IngredientRepository {
    suspend fun getAllIngredients(): List<Ingredient>
    suspend fun findById(id: String): Ingredient?
}
