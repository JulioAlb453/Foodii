package com.example.foodii.feature.planner.domain.usecase

import com.example.foodii.feature.foods.domain.repositories.MelCategoryRepository
import com.example.foodii.feature.planner.domain.entity.MealDetail
import com.example.foodii.feature.planner.domain.repository.PlannerRepository

class PlanMealUseCase(
    private val repository: PlannerRepository
) {
    suspend operator fun invoke(meal: MealDetail, date: Long) {
        repository.planMeal(meal, date)
    }
}