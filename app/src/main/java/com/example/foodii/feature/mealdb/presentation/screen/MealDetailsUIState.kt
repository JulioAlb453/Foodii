package com.example.foodii.feature.mealdb.presentation.screen

import com.example.foodii.feature.mealdb.domain.entity.MealDetail

data class MealDetailsUIState(
    val isLoading: Boolean = false,
    val meals: List<MealDetail> = emptyList(),
    val error: String? = null
)