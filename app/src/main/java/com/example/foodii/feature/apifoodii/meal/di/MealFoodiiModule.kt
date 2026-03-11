package com.example.foodii.feature.apifoodii.meal.di

import android.content.Context
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.CalculateCaloriesUseCase
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.GetIngredientsUseCase
import com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel.IngredientViewModelFactory
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetFoodiiMealByIdUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsByDateRangeUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModelFactory
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import com.example.foodii.feature.mealdb.domain.usecase.GetPlannedMealsUseCase
import com.example.foodii.feature.mealdb.domain.usecase.PlanMealUseCase

class FoodiiFeatureModule(
    private val mealRepository: MealFoodiiRepository,
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository,
    private val plannerRepository: PlannerRepository,
    private val context: Context
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

    private fun provideGetFoodiiMealByIdUseCase(): GetFoodiiMealByIdUseCase {
        return GetFoodiiMealByIdUseCase(
            mealRepository = mealRepository,
            ingredientRepository = ingredientRepository
        )
    }

    // Caso de uso para agendar comidas
    private fun providePlanMealUseCase(): PlanMealUseCase {
        return PlanMealUseCase(plannerRepository)
    }

    private fun provideGetPlannedMealsUseCase(): GetPlannedMealsUseCase {
        return GetPlannedMealsUseCase(plannerRepository)
    }
    
    // Casos de uso de Ingredientes
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
            getFoodiiMealByIdUseCase = provideGetFoodiiMealByIdUseCase(),
            planMealUseCase = providePlanMealUseCase(),
            getPlannedMealsUseCase = provideGetPlannedMealsUseCase(),
            context = context
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
