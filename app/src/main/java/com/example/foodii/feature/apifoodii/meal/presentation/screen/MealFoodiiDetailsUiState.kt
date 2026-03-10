package com.example.foodii.feature.apifoodii.meal.presentation.screen

import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.mealdb.domain.entity.MealDetail

data class MealFoodiiDetailsUiState (
    val isLoading: Boolean = false,
    val successData: FoodiiMeal? = null,
    val meals: List<MealDetail> = emptyList(),
    val error: String? = null
    )