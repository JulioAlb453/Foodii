package com.example.foodii.feature.foods.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodii.feature.foods.domain.usecases.GetMelCategoryUseCase

class MealCategoryViewModelFactory(
    private val getMelCategoryUseCase: GetMelCategoryUseCase
): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T{
        if (modelClass.isAssignableFrom(MealCategoryViewModel::class.java)){
             @Suppress("UNCHECKED_CAST")
             return MealCategoryViewModel(getMelCategoryUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}