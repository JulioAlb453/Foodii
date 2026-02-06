package com.example.foodii.feature.apifoodii.meal.domain.usecase
import com.example.foodii.feature.apifoodii.meal.domain.entity.DailySummary
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.repository.FoodiiMealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.Locale

class GetMealsByDateRangeUseCase(
    private val mealRepository: FoodiiMealRepository
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

        return mealRepository.findByDateRange(startDate, endDate).map { meals: List<FoodiiMeal> ->
            val grouped = meals.groupBy { meal ->
                meal.date.toString().substringBefore("T")
            }

            grouped.map { (dateKey, mealsInDate) ->
                val dailyTotal = mealsInDate.sumOf { it.totalCalories }

                val sortedMeals = mealsInDate.sortedWith(compareBy { meal ->
                    val time = meal.mealTime.toString()

                    when {
                        time.equals("breakfast", ignoreCase = true) -> 1
                        time.equals("lunch", ignoreCase = true) -> 2
                        time.equals("dinner", ignoreCase = true) -> 3
                        time.equals("snack", ignoreCase = true) -> 4
                        else -> 5
                    }
                })

                DailySummary(
                    date = dateKey,
                    totalCalories = dailyTotal,
                    meals = sortedMeals
                )
            }.sortedBy { it.date }
        }
    }
}