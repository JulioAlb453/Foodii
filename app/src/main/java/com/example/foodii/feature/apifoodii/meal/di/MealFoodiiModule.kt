package com.example.foodii.feature.apifoodii.meal.di

import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsByDateRangeUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModelFactory

class FoodiiFeatureModule(
    private val mealRepository: MealFoodiiRepository,
    private val ingredientRepository: IngredientRepository
) {

    private fun provideGetMealsUseCase(): GetMealsUseCase {
        return GetMealsUseCase(mealRepository)
    }

    private fun provideSaveFoodiiMealUseCase(): SaveFoodiiMealUseCase {
        return SaveFoodiiMealUseCase(
            mealRepository = mealRepository,
            ingredientRepository = ingredientRepository
        )
    }

    private fun provideGetMealsByDateRangeUseCase(): GetMealsByDateRangeUseCase {
        return GetMealsByDateRangeUseCase(mealRepository)
    }

    fun provideMealViewModelFactory(): MealFoodiiViewModelFactory {
        return MealFoodiiViewModelFactory(
            saveFoodiiMealUseCase = provideSaveFoodiiMealUseCase(),
            getMealsByDateRangeUseCase = provideGetMealsByDateRangeUseCase(),
            getMealsUseCase = provideGetMealsUseCase()
        )
    }
}
