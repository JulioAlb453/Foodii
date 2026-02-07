package com.example.foodii.feature.apifoodii.meal.domain.entity

data class DailySummary(
    val date: String,
    val totalCalories: Double,
    val meals: List<FoodiiMeal>
)