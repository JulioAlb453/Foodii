package com.example.foodii.feature.apifoodii.ingredient.domain.usecase

import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import javax.inject.Inject

class DeleteIngredientUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(id: String, userId: String): Result<Unit> {
        return ingredientRepository.deleteIngredient(id, userId)
    }
}
