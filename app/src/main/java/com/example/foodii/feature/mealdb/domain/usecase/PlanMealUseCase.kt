package com.example.foodii.feature.mealdb.domain.usecase

import com.example.foodii.feature.mealdb.domain.entity.MealDetail
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import javax.inject.Inject

class PlanMealUseCase @Inject constructor(
    private val plannerRepository: PlannerRepository
) {
    suspend operator fun invoke(meal: MealDetail, date: Long) {
        plannerRepository.planMeal(meal, date)
    }
}
