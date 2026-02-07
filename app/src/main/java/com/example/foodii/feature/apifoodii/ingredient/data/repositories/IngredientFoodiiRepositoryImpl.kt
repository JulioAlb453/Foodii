package com.example.foodii.feature.apifoodii.ingredient.data.repositories

import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.mapper.toDomain
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import javax.inject.Inject

class IngredientFoodiiRepositoryImpl @Inject constructor(
    private val api: FoodiiAPI
) : IngredientRepository {

    override suspend fun getAllIngredients(userId: String): List<Ingredient> {
        return try {
            val response = api.getAllIngredientsAPI(userId = userId)
            response.ingredients?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun findById(id: String, userId: String): Ingredient? {
        return try {
            val response = api.getIngredientByIdAPI(id = id)
            response.ingredient?.toDomain()
        } catch (e: Exception) {
            null
        }
    }
}
