package com.example.foodii.feature.apifoodii.meal.domain.usecase

import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealIngredient
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import java.time.LocalDate
import java.util.UUID

class SaveFoodiiMealUseCase(
    private val mealRepository: MealFoodiiRepository,
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(
        name: String,
        date: LocalDate,
        mealTime: FoodiiMealTime,
        ingredientsRequest: List<Pair<String, Int>>,
        userId: String
    ): Result<FoodiiMeal> {

        if (name.trim().length < 2) {
            return Result.failure(Exception("El nombre de la comida debe tener al menos 2 caracteres"))
        }

        if (ingredientsRequest.isEmpty()) {
            return Result.failure(Exception("La comida debe tener al menos un ingrediente"))
        }

        val ingredientDetails = mutableListOf<FoodiiMealIngredient>()
        var totalCalories = 0

        for (item in ingredientsRequest) {
            val (ingredientId, amount) = item
            
            // Ya no pasamos el token aquí, el interceptor lo maneja
            val ingredient = ingredientRepository.findById(id = ingredientId)
                ?: return Result.failure(Exception("Ingrediente no encontrado"))

            // Mantenemos la vinculación con el usuario
            if (ingredient.createdBy != userId) {
                return Result.failure(Exception("No puedes usar ingredientes de otros usuarios"))
            }

            val calories = ((ingredient.caloriesPer100g * amount) / 100)

            ingredientDetails.add(
                FoodiiMealIngredient(
                    ingredientId = ingredientId,
                    name = ingredient.name,
                    amount = amount,
                    calories = calories
                )
            )
            totalCalories += calories
        }

        val meal = FoodiiMeal(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            date = date,
            mealTime = mealTime,
            totalCalories = totalCalories,
            createdBy = userId,
            ingredients = ingredientDetails
        )

        return try {
            mealRepository.saveMeal(meal)
            Result.success(meal)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
