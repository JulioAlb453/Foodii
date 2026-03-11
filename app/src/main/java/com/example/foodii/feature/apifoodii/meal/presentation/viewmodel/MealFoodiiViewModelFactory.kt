package com.example.foodii.feature.apifoodii.meal.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodii.core.hardware.data.AndroidShakeDetector
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetFoodiiMealByIdUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsByDateRangeUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.GetMealsUseCase
import com.example.foodii.feature.apifoodii.meal.domain.usecase.SaveFoodiiMealUseCase
import com.example.foodii.feature.mealdb.domain.usecase.GetPlannedMealsUseCase
import com.example.foodii.feature.mealdb.domain.usecase.PlanMealUseCase

class MealFoodiiViewModelFactory(
    private val saveFoodiiMealUseCase: SaveFoodiiMealUseCase,
    private val getMealsByDateRangeUseCase: GetMealsByDateRangeUseCase,
    private val getMealsUseCase: GetMealsUseCase,
    private val getFoodiiMealByIdUseCase: GetFoodiiMealByIdUseCase,
    private val planMealUseCase: PlanMealUseCase,
    private val getPlannedMealsUseCase: GetPlannedMealsUseCase,
    private val context: Context
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
                context = context,
                shakeDetector = AndroidShakeDetector(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
