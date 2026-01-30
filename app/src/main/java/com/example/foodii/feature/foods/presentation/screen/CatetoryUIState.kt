package com.example.foodii.feature.foods.presentation.screen

import com.example.foodii.feature.foods.domain.entity.Category

data class CatetoryUIState (
    val isLoading: Boolean = false,
    val post:List<Category> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false,
)