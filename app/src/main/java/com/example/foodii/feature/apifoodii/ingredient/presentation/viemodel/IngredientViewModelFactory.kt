package com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.CalculateCaloriesUseCase
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.GetIngredientsUseCase
import com.example.foodii.feature.auth.domain.repository.AuthRepository

class IngredientViewModelFactory(
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val calculateCaloriesUseCase: CalculateCaloriesUseCase,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngredientViewModel::class.java)) {
            return IngredientViewModel(
                getIngredientsUseCase,
                calculateCaloriesUseCase,
                authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}