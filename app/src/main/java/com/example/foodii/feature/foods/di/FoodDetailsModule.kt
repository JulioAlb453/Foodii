package com.example.foodii.feature.foods.di

import com.example.foodii.core.di.AppContainer
import com.example.foodii.feature.foods.presentation.viewmodel.GetMealInstructionsUseCase
import com.example.foodii.feature.foods.presentation.viewmodel.MealDetailsViewModelFactory

class FoodDetailsModule(private val appContainer: AppContainer) {

    fun provideMealDetailsViewModelFactory(letter: String): MealDetailsViewModelFactory {
        return MealDetailsViewModelFactory(
            useCase = appContainer.getMealInstructionsUseCase,
            letter = letter
        )
    }
}