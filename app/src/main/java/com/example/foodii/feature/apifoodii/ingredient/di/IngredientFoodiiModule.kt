package com.example.foodii.feature.apifoodii.ingredient.di

import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.CalculateCaloriesUseCase
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.GetIngredientsUseCase
import com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel.IngredientViewModelFactory
import com.example.foodii.feature.apifoodii.meal.domain.repository.FoodiiRepository
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModelFactory

class IngredientFoodiiModule(
    private val mealRepository: FoodiiRepository,
    private val ingredientRepository: IngredientRepository
) {

    private fun provideSaveFoodiiMealUseCase(): SaveFoodiiMealUseCase {
        return SaveFoodiiMealUseCase(mealRepository, ingredientRepository)
    }

    private fun provideGetIngredientsUseCase(): GetIngredientsUseCase {
        return GetIngredientsUseCase(ingredientRepository)
    }

    private fun provideCalculateCaloriesUseCase(): CalculateCaloriesUseCase {
        return CalculateCaloriesUseCase(ingredientRepository)
    }

    fun provideMealViewModelFactory(): MealFoodiiViewModelFactory {
        return MealFoodiiViewModelFactory(
            saveFoodiiMealUseCase = provideSaveFoodiiMealUseCase()
        )
    }

    fun provideIngredientViewModelFactory(): IngredientViewModelFactory {
        return IngredientViewModelFactory(
            getIngredientsUseCase = provideGetIngredientsUseCase(),
            calculateCaloriesUseCase = provideCalculateCaloriesUseCase()
        )
    }
}