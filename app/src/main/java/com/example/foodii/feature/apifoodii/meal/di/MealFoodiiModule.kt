package com.example.foodii.feature.apifoodii.meal.di

import android.content.Context
import com.example.foodii.core.hardware.domain.CameraManager
import com.example.foodii.core.hardware.domain.ShakeDetector
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.meal.domain.repository.ImageRepository
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetFoodiiMealByIdUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsByDateRangeUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.DeleteMealUseCase
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModelFactory
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import com.example.foodii.feature.mealdb.domain.usecase.GetPlannedMealsUseCase
import com.example.foodii.feature.mealdb.domain.usecase.PlanMealUseCase
import com.example.foodii.feature.mealdb.domain.usecase.UpdatePlannedMealDateUseCase
import com.example.foodii.feature.mealdb.domain.usecase.DeletePlannedMealUseCase

class MealFoodiiModule(
    private val mealRepository: MealFoodiiRepository,
    private val ingredientRepository: IngredientRepository,
    private val plannerRepository: PlannerRepository,
    private val context: Context,
    private val shakeDetector: ShakeDetector,
    private val cameraManager: CameraManager,
    private val imageRepository: ImageRepository
) {

    private fun provideGetMealsUseCase() = GetMealsUseCase(mealRepository)
    private fun provideSaveFoodiiMealUseCase() = SaveFoodiiMealUseCase(mealRepository, ingredientRepository)
    private fun provideGetMealsByDateRangeUseCase() = GetMealsByDateRangeUseCase(mealRepository)
    private fun provideGetFoodiiMealByIdUseCase() = GetFoodiiMealByIdUseCase(mealRepository, ingredientRepository)
    private fun provideDeleteMealUseCase() = DeleteMealUseCase(mealRepository)
    
    private fun providePlanMealUseCase() = PlanMealUseCase(plannerRepository)
    private fun provideGetPlannedMealsUseCase() = GetPlannedMealsUseCase(plannerRepository)
    private fun provideUpdatePlannedMealDateUseCase() = UpdatePlannedMealDateUseCase(plannerRepository)
    private fun provideDeletePlannedMealUseCase() = DeletePlannedMealUseCase(plannerRepository)

    fun provideMealViewModelFactory(): MealFoodiiViewModelFactory {
        return MealFoodiiViewModelFactory(
            saveFoodiiMealUseCase = provideSaveFoodiiMealUseCase(),
            getMealsByDateRangeUseCase = provideGetMealsByDateRangeUseCase(),
            getMealsUseCase = provideGetMealsUseCase(),
            getFoodiiMealByIdUseCase = provideGetFoodiiMealByIdUseCase(),
            deleteMealUseCase = provideDeleteMealUseCase(),
            planMealUseCase = providePlanMealUseCase(),
            getPlannedMealsUseCase = provideGetPlannedMealsUseCase(),
            updatePlannedMealDateUseCase = provideUpdatePlannedMealDateUseCase(),
            deletePlannedMealUseCase = provideDeletePlannedMealUseCase(),
            ingredientRepository = ingredientRepository,
            context = context,
            shakeDetector = shakeDetector,
            cameraManager = cameraManager,
            imageRepository = imageRepository
        )
    }
}
