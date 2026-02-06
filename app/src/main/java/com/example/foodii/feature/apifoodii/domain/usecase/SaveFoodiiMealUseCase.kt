package com.example.foodii.feature.apifoodii.domain.usecase

import com.example.foodii.feature.apifoodii.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.domain.repository.FoodiiRepository

class SaveFoodiiMealUseCase(
    private val repository: FoodiiRepository
) {
    suspend operator fun invoke(meal: FoodiiMeal) {
        repository.saveMeal(meal)
    }
}
