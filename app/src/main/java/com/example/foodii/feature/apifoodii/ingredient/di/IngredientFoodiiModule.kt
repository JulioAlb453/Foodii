package com.example.foodii.feature.apifoodii.ingredient.di

import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.CalculateCaloriesUseCase
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.GetIngredientsUseCase
import com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel.IngredientViewModelFactory
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsByDateRangeUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModelFactory
import com.example.foodii.feature.auth.domain.repository.AuthRepository

class IngredientFoodiiModule(
    private val mealRepository: MealFoodiiRepository,
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository
) {

    private fun provideSaveFoodiiMealUseCase(): SaveFoodiiMealUseCase {
        return SaveFoodiiMealUseCase(mealRepository, ingredientRepository)
    }

    private fun provideGetMealsByDateRangeUseCase(): GetMealsByDateRangeUseCase {
        return GetMealsByDateRangeUseCase(mealRepository)
    }

    private fun provideGetMealsUseCase(): GetMealsUseCase {
        return GetMealsUseCase(mealRepository)
    }

    private fun provideGetIngredientsUseCase(): GetIngredientsUseCase {
        return GetIngredientsUseCase(ingredientRepository)
    }

    private fun provideCalculateCaloriesUseCase(): CalculateCaloriesUseCase {
        return CalculateCaloriesUseCase(ingredientRepository)
    }

    fun provideMealViewModelFactory(): MealFoodiiViewModelFactory {
        return MealFoodiiViewModelFactory(
            saveFoodiiMealUseCase = provideSaveFoodiiMealUseCase(),
            getMealsByDateRangeUseCase = provideGetMealsByDateRangeUseCase(),
            getMealsUseCase = provideGetMealsUseCase(),
        )
    }

    fun provideIngredientViewModelFactory(): IngredientViewModelFactory {
        return IngredientViewModelFactory(
            getIngredientsUseCase = provideGetIngredientsUseCase(),
            calculateCaloriesUseCase = provideCalculateCaloriesUseCase(),
            authRepository = authRepository
        )
    }
}
