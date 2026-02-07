package com.example.foodii.feature.planner.di

import com.example.foodii.core.di.AppContainer
import com.example.foodii.feature.planner.domain.usecase.GetMealInstructionsUseCase
import com.example.foodii.feature.planner.domain.usecase.PlanMealUseCase
import com.example.foodii.feature.planner.presentation.viewmodel.MealDetailsViewModelFactory

class FoodDetailsModule(private val appContainer: AppContainer) {

    // Nota: Estas referencias fallaban porque se eliminaron melCategoryRepository y plannerRepository
    // Se comentan o se redirigen a los nuevos repositorios de AWS si fuera necesario.
    
    /* 
    private fun provideGetMealInstructionsUseCase(): GetMealInstructionsUseCase {
        return GetMealInstructionsUseCase(appContainer.foodiiRepository) 
    }

    private fun providePlanMealUseCase(): PlanMealUseCase {
        return PlanMealUseCase(plannerRepository = appContainer.foodiiRepository)
    }

    fun provideMealDetailsViewModelFactory(letter: String): MealDetailsViewModelFactory {
        return MealDetailsViewModelFactory(
            getMealInstructionsUseCase = provideGetMealInstructionsUseCase(),
            plannerUseCase = providePlanMealUseCase(),
            letter = letter
        )
    }
    */
}
