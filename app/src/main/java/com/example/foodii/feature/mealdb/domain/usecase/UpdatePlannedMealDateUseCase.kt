package com.example.foodii.feature.mealdb.domain.usecase

import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import javax.inject.Inject

class UpdatePlannedMealDateUseCase @Inject constructor(
    private val repository: PlannerRepository
) {
    suspend operator fun invoke(id: Int, newDate: Long, userId: String) {
        repository.updatePlannedMealDate(id, newDate, userId)
    }
}
