package com.example.foodii.feature.planner.di

import com.example.foodii.core.di.AppContainer
import com.example.foodii.feature.planner.domain.usecase.GetMealInstructionsUseCase
import com.example.foodii.feature.planner.presentation.viewmodel.MealDetailsViewModelFactory

class FoodDetailsModule(private val appContainer: AppContainer) {

    private fun provideGetMealInstructionsUseCase(): GetMealInstructionsUseCase {
        return GetMealInstructionsUseCase(appContainer.melCategoryRepository)
    }

    fun provideMealDetailsViewModelFactory(letter: String): MealDetailsViewModelFactory {
        return MealDetailsViewModelFactory(
            getMealInstructionsUseCase = provideGetMealInstructionsUseCase(),
            plannerRepository = appContainer.plannerRepository,
            letter = letter
        )
    }
}