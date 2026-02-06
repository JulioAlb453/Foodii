package com.example.foodii.feature.apifoodii.meal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase

class MealFoodiiViewModelFactory(
    private val saveFoodiiMealUseCase: SaveFoodiiMealUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealFoodiiViewModel::class.java)) {
            return MealFoodiiViewModel(saveFoodiiMealUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}