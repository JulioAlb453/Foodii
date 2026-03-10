package com.example.foodii.feature.apifoodii.meal.domain.usecase

import com.example.foodii.feature.apifoodii.meal.domain.entity.DailySummary
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GetMealsByDateRangeUseCase(
    private val mealRepository: MealFoodiiRepository
) {
    operator fun invoke(
        userId: String,
        startDate: String,
        endDate: String
    ): Flow<List<DailySummary>> {

        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)

        if (start.isAfter(end)) {
            throw IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin")
        }

        return mealRepository.findByDateRange(startDate, endDate, userId).map { meals ->
            val grouped = meals.groupBy { it.date.toString() }

            grouped.map { (dateKey, mealsInDate) ->
                val dailyTotal = mealsInDate.sumOf { it.totalCalories }

                val sortedMeals = mealsInDate.sortedBy { meal ->
                    when (meal.mealTime) {
                        FoodiiMealTime.BREAKFAST -> 1
                        FoodiiMealTime.LUNCH -> 2
                        FoodiiMealTime.DINNER -> 3
                        FoodiiMealTime.SNACK -> 4
                    }
                }

                DailySummary(
                    date = dateKey,
                    totalCalories = dailyTotal,
                    meals = sortedMeals
                )
            }.sortedBy { it.date }
        }
    }
}
