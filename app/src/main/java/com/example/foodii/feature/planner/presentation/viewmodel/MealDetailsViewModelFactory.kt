package com.example.foodii.feature.planner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodii.feature.planner.domain.repository.PlannerRepository
import com.example.foodii.feature.planner.domain.usecase.GetMealInstructionsUseCase
import com.example.foodii.feature.planner.domain.usecase.PlanMealUseCase

class MealDetailsViewModelFactory(
    private val getMealInstructionsUseCase: GetMealInstructionsUseCase,
    private val plannerUseCase: PlanMealUseCase,
    private val letter: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealDetailsViewModel(getMealInstructionsUseCase, plannerUseCase, letter) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}


