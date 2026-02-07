package com.example.foodii.feature.apifoodii.meal.domain.entity

import java.time.LocalDate

data class FoodiiMeal(
    val id: String,
    val name: String,
    val date: LocalDate,
    val mealTime: FoodiiMealTime,
    val totalCalories: Double,
    val createdBy: String,
    val ingredients: List<FoodiiMealIngredient> = emptyList()
)

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
    val calories: Double
)
