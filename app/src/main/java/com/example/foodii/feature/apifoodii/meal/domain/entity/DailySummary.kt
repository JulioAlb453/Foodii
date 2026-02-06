package com.example.foodii.feature.apifoodii.meal.domain.entity

data class DailySummary(
    val date: String,
    val totalCalories: Int,
    val meals: List<FoodiiMeal>
)