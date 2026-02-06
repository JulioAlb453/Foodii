package com.example.foodii.feature.apifoodii.meal.di

import com.example.foodii.feature.apifoodii.meal.domain.repository.FoodiiRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModelFactory

class FoodiiFeatureModule(
    private val mealRepository: FoodiiRepository,
    private val ingredientRepository: IngredientRepository
) {

    private fun provideSaveFoodiiMealUseCase(): SaveFoodiiMealUseCase {
        return SaveFoodiiMealUseCase(
            mealRepository = mealRepository,
            ingredientRepository = ingredientRepository
        )
    }


    fun provideMealViewModelFactory(): MealFoodiiViewModelFactory {
        return MealFoodiiViewModelFactory(
            saveFoodiiMealUseCase = provideSaveFoodiiMealUseCase()
        )
    }
}