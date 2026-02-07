package com.example.foodii.feature.apifoodii.ingredient.domain.entity

data class CalculateCaloriesResponse(
    val ingredientId: String,
    val ingredientName: String,
    val amount: Int,
    val caloriesPer100g: Int,
    val calculatedCalories: Int
)