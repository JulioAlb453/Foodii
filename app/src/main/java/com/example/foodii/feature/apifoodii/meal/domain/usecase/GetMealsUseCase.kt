package com.example.foodii.feature.apifoodii.meal.domain.usecase

import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import kotlinx.coroutines.flow.Flow

class GetMealsUseCase(
    private val mealRepository: MealFoodiiRepository
) {

    operator fun invoke(userId: String): Flow<List<FoodiiMeal>> {
        return mealRepository.findAll(userId)
    }
}
