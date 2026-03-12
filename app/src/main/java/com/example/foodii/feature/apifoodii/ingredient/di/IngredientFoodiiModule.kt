package com.example.foodii.feature.apifoodii.ingredient.di

import android.content.Context
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.usecase.*
import com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel.IngredientViewModelFactory
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetFoodiiMealByIdUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsByDateRangeUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModelFactory
import com.example.foodii.feature.auth.domain.repository.AuthRepository
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import com.example.foodii.feature.mealdb.domain.usecase.GetPlannedMealsUseCase
import com.example.foodii.feature.mealdb.domain.usecase.PlanMealUseCase

class IngredientFoodiiModule(
    private val mealRepository: MealFoodiiRepository,
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository,
    private val plannerRepository: PlannerRepository,
    private val context: Context
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

    private fun provideGetFoodiiMealByIdUseCase(): GetFoodiiMealByIdUseCase {
        return GetFoodiiMealByIdUseCase(mealRepository, ingredientRepository)
    }

    private fun provideGetIngredientsUseCase(): GetIngredientsUseCase {
        return GetIngredientsUseCase(ingredientRepository)
    }

    private fun provideCreateIngredientUseCase(): CreateIngredientUseCase {
        return CreateIngredientUseCase(ingredientRepository)
    }

    private fun provideUpdateIngredientUseCase(): UpdateIngredientUseCase {
        return UpdateIngredientUseCase(ingredientRepository)
    }

    private fun provideDeleteIngredientUseCase(): DeleteIngredientUseCase {
        return DeleteIngredientUseCase(ingredientRepository)
    }

    private fun provideCalculateCaloriesUseCase(): CalculateCaloriesUseCase {
        return CalculateCaloriesUseCase(ingredientRepository)
    }

    private fun providePlanMealUseCase(): PlanMealUseCase {
        return PlanMealUseCase(plannerRepository)
    }

    private fun provideGetPlannedMealsUseCase(): GetPlannedMealsUseCase {
        return GetPlannedMealsUseCase(plannerRepository)
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
            createIngredientUseCase = provideCreateIngredientUseCase(),
            updateIngredientUseCase = provideUpdateIngredientUseCase(),
            deleteIngredientUseCase = provideDeleteIngredientUseCase(),
            calculateCaloriesUseCase = provideCalculateCaloriesUseCase(),
            authRepository = authRepository
        )
    }
}
