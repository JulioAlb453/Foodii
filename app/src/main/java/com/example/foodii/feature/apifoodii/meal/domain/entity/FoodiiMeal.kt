package com.example.foodii.feature.apifoodii.meal.domain.entity

import java.time.LocalDate

data class FoodiiMealStep(
    val stepOrder: Int,
    val description: String,
)

data class FoodiiMeal(
    val id: String,
    val name: String,
    val date: LocalDate,
    val mealTime: FoodiiMealTime,
    val totalCalories: Double,
    val createdBy: String,
    val steps: List<FoodiiMealStep> = emptyList(),
    val image: String? = null,
    val ingredients: List<FoodiiMealIngredient> = emptyList(),
    val categories: List<String> = emptyList(), // Añadido campo categorías
) {
    fun stepsPlainText(): String {
        if (steps.isEmpty()) return ""
        return steps
            .sortedBy { it.stepOrder }
            .joinToString("\n") { "${it.stepOrder}. ${it.description}" }
    }
}

enum class FoodiiMealTime {
    BREAKFAST, LUNCH, DINNER, SNACK;

    companion object {
        fun fromString(value: String): FoodiiMealTime {
            return entries.find { it.name.lowercase() == value.lowercase() } ?: SNACK
        }
    }
}

data class FoodiiMealIngredient(
    val ingredientId: String,
    val name: String,
    val amount: Int,
    val calories: Double,
)
