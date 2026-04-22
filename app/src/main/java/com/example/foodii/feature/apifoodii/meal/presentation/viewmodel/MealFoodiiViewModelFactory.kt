package com.example.foodii.feature.apifoodii.meal.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodii.core.hardware.domain.CameraManager
import com.example.foodii.core.hardware.domain.ShakeDetector
import com.example.foodii.feature.apifoodii.meal.domain.repository.ImageRepository
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetFoodiiMealByIdUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsByDateRangeUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsUseCase
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.mealdb.domain.usecase.GetPlannedMealsUseCase
import com.example.foodii.feature.mealdb.domain.usecase.PlanMealUseCase
import com.example.foodii.feature.mealdb.domain.usecase.UpdatePlannedMealDateUseCase

class MealFoodiiViewModelFactory(
    private val saveFoodiiMealUseCase: SaveFoodiiMealUseCase,
    private val getMealsByDateRangeUseCase: GetMealsByDateRangeUseCase,
    private val getMealsUseCase: GetMealsUseCase,
    private val getFoodiiMealByIdUseCase: GetFoodiiMealByIdUseCase,
    private val planMealUseCase: PlanMealUseCase,
    private val getPlannedMealsUseCase: GetPlannedMealsUseCase,
    private val updatePlannedMealDateUseCase: UpdatePlannedMealDateUseCase,
    private val ingredientRepository: IngredientRepository,
    private val context: Context,
    private val shakeDetector: ShakeDetector,
    private val cameraManager: CameraManager,
    private val imageRepository: ImageRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealFoodiiViewModel::class.java)) {
            return MealFoodiiViewModel(
                saveFoodiiMealUseCase = saveFoodiiMealUseCase,
                getMealsByDateRangeUseCase = getMealsByDateRangeUseCase,
                getMealsUseCase = getMealsUseCase,
                getFoodiiMealByIdUseCase = getFoodiiMealByIdUseCase,
                planMealUseCase = planMealUseCase,
                getPlannedMealsUseCase = getPlannedMealsUseCase,
                updatePlannedMealDateUseCase = updatePlannedMealDateUseCase,
                ingredientRepository = ingredientRepository,
                context = context,
                shakeDetector = shakeDetector,
                cameraManager = cameraManager,
                imageRepository = imageRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
