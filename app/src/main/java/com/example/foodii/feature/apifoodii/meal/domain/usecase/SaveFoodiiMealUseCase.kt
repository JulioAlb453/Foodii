package com.example.foodii.feature.apifoodii.meal.domain.usecase

import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import java.time.LocalDate

class SaveFoodiiMealUseCase(
    private val mealRepository: MealFoodiiRepository,
    private val ingredientRepository: IngredientRepository,
) {
    suspend operator fun invoke(
        name: String,
        date: LocalDate,
        mealTime: FoodiiMealTime,
        ingredientsRequest: List<Pair<String, Int>>,
        steps: List<String>,
        userId: String,
        image: String? = null,
        categories: List<String> = emptyList() // Añadido soporte para categorías
    ) = runCatching {
        if (name.trim().length < 2) {
            error("El nombre de la comida debe tener al menos 2 caracteres")
        }
        if (ingredientsRequest.isEmpty()) {
            error("La comida debe tener al menos un ingrediente")
        }

        for (item in ingredientsRequest) {
            val (ingredientId, amount) = item
            if (amount <= 0) {
                error("La cantidad debe ser mayor que 0")
            }
            ingredientRepository.findById(id = ingredientId, userId = userId)
                ?: error("Ingrediente no encontrado")
        }

        mealRepository.createMealOnServer(
            name = name,
            date = date,
            mealTime = mealTime,
            ingredients = ingredientsRequest,
            steps = steps,
            userId = userId,
            image = image,
            categories = categories // Pasar categorías al repositorio
        ).getOrElse { throw it }
    }
}
