package com.example.foodii.feature.planner.di

import com.example.foodii.core.di.AppContainer
import com.example.foodii.feature.planner.domain.usecase.GetMealInstructionsUseCase
import com.example.foodii.feature.planner.domain.usecase.PlanMealUseCase
import com.example.foodii.feature.planner.presentation.viewmodel.MealDetailsViewModelFactory

class FoodDetailsModule(private val appContainer: AppContainer) {

    private fun provideGetMealInstructionsUseCase(): GetMealInstructionsUseCase {
        return GetMealInstructionsUseCase(appContainer.melCategoryRepository)
    }

    private fun providePlanMealUseCase(): PlanMealUseCase {
        return PlanMealUseCase(plannerRepository = appContainer.plannerRepository)
    }

    fun provideMealDetailsViewModelFactory(letter: String): MealDetailsViewModelFactory {
        return MealDetailsViewModelFactory(
            getMealInstructionsUseCase = provideGetMealInstructionsUseCase(),
            plannerUseCase = providePlanMealUseCase(),
            letter = letter
        )
    }
}
