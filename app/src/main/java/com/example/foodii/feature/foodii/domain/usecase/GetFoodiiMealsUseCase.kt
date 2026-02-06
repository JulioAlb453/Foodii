package com.example.foodii.feature.foodii.domain.usecase

import com.example.foodii.feature.apifoodii.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.domain.repository.FoodiiRepository
import kotlinx.coroutines.flow.Flow

class GetFoodiiMealsUseCase(
    private val repository: FoodiiRepository
) {
    operator fun invoke(): Flow<List<FoodiiMeal>> {
        return repository.getMeals()
    }
}
