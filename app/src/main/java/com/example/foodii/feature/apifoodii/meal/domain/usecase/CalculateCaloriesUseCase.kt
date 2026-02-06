package com.example.foodii.feature.apifoodii.meal.domain.usecase

import com.example.foodii.feature.apifoodii.ingredient.domain.entity.CaloriesSummary
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.MealsByTime
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.repository.FoodiiMealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CalculateCaloriesUseCase(
    private val repository: FoodiiMealRepository
) {

    operator fun invoke(userId: String, date: String? = null): Flow<CaloriesSummary> {

        val mealsFlow = if (date != null) {
            repository.findByDate(date)
        } else {
            repository.findAll()
        }
        return mealsFlow.map { meals ->
            if (meals.isEmpty()) {
                return@map CaloriesSummary(0, 0, 0, MealsByTime())
            }

            val totalCalories = meals.sumOf { it.totalCalories }

            val counts = meals.groupingBy { meal ->
                meal.mealTime.toString()
            }.eachCount()

            CaloriesSummary(
                totalCalories = totalCalories,
                mealsCount = meals.size,
                averageCaloriesPerMeal = if (meals.isNotEmpty()) totalCalories / meals.size else 0,
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