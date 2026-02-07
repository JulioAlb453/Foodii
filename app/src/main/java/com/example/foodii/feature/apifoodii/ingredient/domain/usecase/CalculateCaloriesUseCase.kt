package com.example.foodii.feature.apifoodii.ingredient.domain.usecase

import com.example.foodii.feature.apifoodii.ingredient.domain.entity.CalculateCaloriesResponse
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import javax.inject.Inject

class CalculateCaloriesUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(
        ingredientId: String,
        amount: Int,
        userId: String,
    ): Result<CalculateCaloriesResponse> {
        return try {
            if (amount <= 0) {
                return Result.failure(Exception("La cantidad debe ser mayor que 0"))
            }

            // Pasamos el token al repositorio según el nuevo contrato
            val ingredient = ingredientRepository.findById(ingredientId)
                ?: return Result.failure(Exception("Ingrediente no encontrado"))

            if (ingredient.createdBy != userId) {
                return Result.failure(Exception("No tienes permiso para usar este ingrediente"))
            }

            val calculatedCalories = (ingredient.caloriesPer100g * amount) / 100

            Result.success(
                CalculateCaloriesResponse(
                    ingredientId = ingredient.id,
                    ingredientName = ingredient.name,
                    amount = amount,
                    caloriesPer100g = ingredient.caloriesPer100g,
                    calculatedCalories = calculatedCalories
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
