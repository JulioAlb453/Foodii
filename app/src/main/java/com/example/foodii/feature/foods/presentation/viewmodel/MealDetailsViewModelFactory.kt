package com.example.foodii.feature.foods.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MealDetailsViewModelFactory(
    private val useCase: GetMealInstructionsUseCase,
    private val letter: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealDetailsViewModel(useCase, letter) as T
        }
        throw IllegalArgumentException("Unknown ViewModel clas ${modelClass.name}")
    }
}
