package com.example.foodii.feature.foods.presentation.viewmodel

import com.example.foodii.feature.foods.domain.entity.MealDetail
import com.example.foodii.feature.foods.domain.repositories.MelCategoryRepository

class GetMealInstructionsUseCase(private val repository: MelCategoryRepository) {
    suspend operator fun invoke(letter:String): Result<List<MealDetail>> = runCatching {
        repository.getMealsByLetter(letter)
    }
}