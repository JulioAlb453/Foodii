package com.example.foodii.feature.planner.domain.usecase

import com.example.foodii.feature.foods.domain.repositories.MelCategoryRepository
import com.example.foodii.feature.planner.domain.entity.MealDetail

class GetMealInstructionsUseCase(private val repository: MelCategoryRepository) {
    suspend operator fun invoke(letter:String): Result<List<MealDetail>> = runCatching {
        repository.getMealsByLetter(letter)
    }
}