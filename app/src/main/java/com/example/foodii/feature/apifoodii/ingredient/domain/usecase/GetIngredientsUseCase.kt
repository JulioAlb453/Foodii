package com.example.foodii.feature.apifoodii.ingredient.domain.usecase

import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import javax.inject.Inject

class GetIngredientsUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository
) {

    suspend operator fun invoke(
        userId: String,
        search: String? = null
    ): Result<List<Ingredient>> {
        return try {
            val ingredients = ingredientRepository.getAllIngredients()

            val filteredIngredients = if (!search.isNullOrBlank()) {
                val searchLower = search.lowercase()
                ingredients.filter { it.name.lowercase().contains(searchLower) }
            } else {
                ingredients
            }

            val sortedIngredients = filteredIngredients.sortedBy { it.name }

            Result.success(sortedIngredients)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}