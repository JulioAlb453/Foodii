package com.example.foodii.feature.apifoodii.meal.domain.usecase

import com.example.foodii.feature.apifoodii.ingredient.domain.entity.CaloriesSummary
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.MealsByTime
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculateCaloriesUseCase @Inject constructor(
    private val repository: MealFoodiiRepository
) {

    operator fun invoke(userId: String, date: String? = null): Flow<CaloriesSummary> {
        val mealsFlow = if (date != null) {
            repository.findByDate(date, userId)
        } else {
            repository.findAll(userId)
        }
        
        return mealsFlow.map { meals ->
            if (meals.isEmpty()) {
                return@map CaloriesSummary(0.0, 0, 0.0, MealsByTime())
            }

            val totalCalories = meals.sumOf { it.totalCalories }

            val counts = meals.groupingBy { meal ->
                meal.mealTime.toString().lowercase()
            }.eachCount()

            CaloriesSummary(
                totalCalories = totalCalories,
                mealsCount = meals.size,
                averageCaloriesPerMeal = if (meals.isNotEmpty()) (totalCalories.toDouble() / meals.size) else 0.0,
                mealsByTime = MealsByTime(
                    breakfast = counts["breakfast"] ?: 0,
                    lunch = counts["lunch"] ?: 0,
                    dinner = counts["dinner"] ?: 0,
                    snack = counts["snack"] ?: 0
                )
            )
        }
    }
}
