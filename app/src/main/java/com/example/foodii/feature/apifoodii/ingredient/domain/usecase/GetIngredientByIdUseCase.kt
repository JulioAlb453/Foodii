package com.example.foodii.feature.apifoodii.ingredient.domain.usecase

import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import javax.inject.Inject

class GetIngredientByIdUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(id: String, userId: String): Result<Ingredient> {
        return try {
            val ingredient = ingredientRepository.findById(id, userId)
            if (ingredient != null) {
                Result.success(ingredient)
            } else {
                Result.failure(Exception("Ingrediente no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
