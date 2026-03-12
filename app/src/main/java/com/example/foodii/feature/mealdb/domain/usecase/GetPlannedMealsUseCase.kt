package com.example.foodii.feature.mealdb.domain.usecase

import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlannedMealsUseCase @Inject constructor(
    private val repository: PlannerRepository
) {
    operator fun invoke(userId: String): Flow<List<PlannedMealEntity>> {
        return repository.getPlannedMeals(userId)
    }

    suspend fun getNextPlannedMeal(userId: String): PlannedMealEntity? {
        val now = System.currentTimeMillis()
        val futureMeals = repository.getPlannedMealsForDateRange(userId,
            now, now + (7 * 24 * 60 * 60 * 1000L))
        return futureMeals.firstOrNull()
    }

    suspend fun getForTomorrow(userId: String): List<PlannedMealEntity> {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        val end = calendar.timeInMillis
        
        return repository.getPlannedMealsForDateRange(userId, start, end)
    }
}
