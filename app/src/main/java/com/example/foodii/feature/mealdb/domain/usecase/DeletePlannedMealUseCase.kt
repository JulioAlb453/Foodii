package com.example.foodii.feature.mealdb.domain.usecase

import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import javax.inject.Inject

class DeletePlannedMealUseCase @Inject constructor(
    private val repository: PlannerRepository
) {
    suspend operator fun invoke(id: Int, userId: String) {
        repository.deletePlannedMeal(id, userId)
    }
}
