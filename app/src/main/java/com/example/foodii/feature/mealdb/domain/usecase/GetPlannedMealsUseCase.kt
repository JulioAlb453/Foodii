package com.example.foodii.feature.mealdb.domain.usecase

import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlannedMealsUseCase @Inject constructor(
    private val repository: PlannerRepository
) {
    operator fun invoke(): Flow<List<PlannedMealEntity>> {
        return repository.getPlannedMeals()
    }
}
