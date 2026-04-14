package com.example.foodii.feature.apifoodii.meal.domain.usecase

import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import javax.inject.Inject

class GetFoodiiMealByIdUseCase @Inject constructor(
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
                    val calculatedCalories = (detail.caloriesPer100g * basicIngredient.amount) / 100.0
                    // Si el ingrediente ya tiene calorías de la API, las usamos, si no usamos el cálculo
                    basicIngredient.copy(
                        name = detail.name,
                        calories = if (basicIngredient.calories > 0) basicIngredient.calories else calculatedCalories
                    )
                } else {
                    basicIngredient
                }
            }

            val finalTotalCalories = if (meal.totalCalories > 0) {
                meal.totalCalories
            } else {
                enrichedIngredients.sumOf { it.calories }
            }

            Result.success(
                meal.copy(
                    ingredients = enrichedIngredients,
                    totalCalories = finalTotalCalories
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
