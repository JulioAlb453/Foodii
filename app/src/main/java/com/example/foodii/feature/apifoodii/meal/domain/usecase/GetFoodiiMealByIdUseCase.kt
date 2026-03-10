package com.example.foodii.feature.apifoodii.meal.domain.usecase

import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository

class GetFoodiiMealByIdUseCase(
    private val mealRepository: MealFoodiiRepository,
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(id: String, userId: String): Result<FoodiiMeal> {
        return try {
            val meal = mealRepository.getMealById(id, userId)
                ?: return Result.failure(Exception("Platillo no encontrado"))

            val enrichedIngredients = meal.ingredients.map { basicIngredient ->
                val detail = ingredientRepository.findById(basicIngredient.ingredientId, userId)
                
                if (detail != null) {
                    val calories = (detail.caloriesPer100g * basicIngredient.amount) / 100.0
                    basicIngredient.copy(
                        name = detail.name,
                        calories = calories
                    )
                } else {
                    basicIngredient
                }
            }

            val totalCalories = enrichedIngredients.sumOf { it.calories }

            Result.success(
                meal.copy(
                    ingredients = enrichedIngredients,
                    totalCalories = totalCalories
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
