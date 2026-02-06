package com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.CalculateCaloriesUseCase
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.GetIngredientsUseCase

class IngredientViewModelFactory(
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val calculateCaloriesUseCase: CalculateCaloriesUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngredientViewModel::class.java)) {
            return IngredientViewModel(
                getIngredientsUseCase,
                calculateCaloriesUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}