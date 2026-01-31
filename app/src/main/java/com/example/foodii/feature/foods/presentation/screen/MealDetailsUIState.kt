package com.example.foodii.feature.foods.presentation.screen

import com.example.foodii.feature.planner.domain.entity.MealDetail

data class MealDetailsUIState(
    val isLoading: Boolean = false,
    val meals: List<MealDetail> = emptyList(),
    val error: String? = null
)