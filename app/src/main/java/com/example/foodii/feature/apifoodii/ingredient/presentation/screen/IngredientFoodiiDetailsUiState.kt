package com.example.foodii.feature.apifoodii.ingredient.presentation.screen

import com.example.foodii.feature.apifoodii.ingredient.domain.entity.CalculateCaloriesResponse
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient


data class IngredientFoodiiDetailsUiState (
    val isLoading: Boolean = false,
    val ingredients: List<Ingredient> = emptyList(),
    val calculation: CalculateCaloriesResponse? = null,
    val error: String? = null
)