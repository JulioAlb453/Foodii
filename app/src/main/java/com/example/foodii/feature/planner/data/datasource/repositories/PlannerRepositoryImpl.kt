package com.example.foodii.feature.planner.data.datasource.repositories

import com.example.foodii.feature.planner.data.local.dao.PlannedMealDao
import com.example.foodii.feature.planner.data.local.entity.PlannedMealEntity
import com.example.foodii.feature.planner.domain.entity.MealDetail
import com.example.foodii.feature.planner.domain.repository.PlannerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlannerRepositoryImpl(
    private val plannedMealDao: PlannedMealDao
) : PlannerRepository {

    override suspend fun planMeal(meal: MealDetail, date: Long) {
        withContext(Dispatchers.IO) {
            val entity = PlannedMealEntity(
                mealId = meal.id,
                name = meal.name,
                imageUrl = meal.imageUrl,
                date = date
            )
            plannedMealDao.insertPlannedMeal(entity)
        }
    }

    override fun getPlannedMeals() = plannedMealDao.getAllPlannedMeals()
}