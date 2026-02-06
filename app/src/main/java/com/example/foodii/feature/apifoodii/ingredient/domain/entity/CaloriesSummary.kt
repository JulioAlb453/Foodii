package com.example.foodii.feature.apifoodii.ingredient.domain.entity

data class CaloriesSummary(
    val totalCalories: Int,
    val mealsCount: Int,
    val averageCaloriesPerMeal: Int,
    val mealsByTime: MealsByTime
)

data class MealsByTime(
    val breakfast: Int = 0,
    val lunch: Int = 0,
    val dinner: Int = 0,
    val snack: Int = 0
)