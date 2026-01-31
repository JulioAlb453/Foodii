package com.example.foodii.feature.planner.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodii.feature.planner.domain.repository.PlannerRepository
import com.example.foodii.feature.planner.domain.usecase.GetMealInstructionsUseCase

class MealDetailsViewModelFactory(
    private val getMealInstructionsUseCase: GetMealInstructionsUseCase,
    private val plannerRepository: PlannerRepository,
    private val letter: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealDetailsViewModel(getMealInstructionsUseCase, plannerRepository, letter) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}


