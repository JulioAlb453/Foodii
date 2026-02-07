package com.example.foodii.feature.apifoodii.meal.domain.usecase

import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository

class GetFoodiiMealByIdUseCase(
    private val mealRepository: MealFoodiiRepository,
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(id: String, userId: String): Result<FoodiiMeal> {
        val meal = mealRepository.getMealById(id, userId)
            ?: return Result.failure(Exception("Comida no encontrada"))

        val enrichedIngredients = meal.ingredients.map { basicIngredient ->
            val detail = ingredientRepository.findById(basicIngredient.ingredientId)
            
            if (detail != null) {
                val calories = ((detail.caloriesPer100g * basicIngredient.amount) / 100)
                basicIngredient.copy(
                    name = detail.name,
                    calories = calories
                )
            } else {
                basicIngredient
            }
        }

        return Result.success(
            meal.copy(ingredients = enrichedIngredients)
        )
    }
}
