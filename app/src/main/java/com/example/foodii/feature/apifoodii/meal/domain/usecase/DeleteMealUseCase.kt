package com.example.foodii.feature.apifoodii.meal.domain.usecase

import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import javax.inject.Inject

class DeleteMealUseCase @Inject constructor(
    private val repository: MealFoodiiRepository
) {
    suspend operator fun invoke(id: String, userId: String) {
        repository.deleteMeal(id, userId)
    }
}
