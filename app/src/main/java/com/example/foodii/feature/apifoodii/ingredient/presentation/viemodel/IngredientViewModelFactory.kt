package com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.*
import com.example.foodii.feature.auth.domain.repository.AuthRepository

class IngredientViewModelFactory(
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val createIngredientUseCase: CreateIngredientUseCase,
    private val updateIngredientUseCase: UpdateIngredientUseCase,
    private val deleteIngredientUseCase: DeleteIngredientUseCase,
    private val calculateCaloriesUseCase: CalculateCaloriesUseCase,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngredientViewModel::class.java)) {
            return IngredientViewModel(
                getIngredientsUseCase,
                createIngredientUseCase,
                updateIngredientUseCase,
                deleteIngredientUseCase,
                calculateCaloriesUseCase,
                authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
