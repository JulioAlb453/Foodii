package com.example.foodii.feature.mealdb.domain.usecase

import com.example.foodii.feature.mealdb.domain.entity.MealDetail
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import javax.inject.Inject

class GetMealInstructionsUseCase @Inject constructor(
    private val repository: PlannerRepository
) {
    suspend operator fun invoke(letter: String): Result<List<MealDetail>> = runCatching {
        repository.getMealsByLetter(letter)
    }
}
