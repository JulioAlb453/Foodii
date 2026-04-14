package com.example.foodii.feature.mealdb.data.datasource.repositories

import com.example.foodii.feature.mealdb.data.datasource.api.MealDbApi
import com.example.foodii.feature.mealdb.data.datasource.remote.mapper.toDomain
import com.example.foodii.feature.mealdb.data.local.dao.PlannedMealDao
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import com.example.foodii.feature.mealdb.domain.entity.MealDetail
import com.example.foodii.feature.mealdb.domain.repository.PlannerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlannerRepositoryImpl @Inject constructor(
    private val mealApi: MealDbApi,
    private val plannedMealDao: PlannedMealDao
) : PlannerRepository {

    override suspend fun planMeal(meal: MealDetail, date: Long, userId: String) {
        withContext(Dispatchers.IO) {
            val entity = PlannedMealEntity(
                mealId = meal.id,
                name = meal.name,
                imageUrl = meal.imageUrl,
                instructions = meal.instructions,
                date = date,
                userId = userId
            )
            plannedMealDao.insertPlannedMeal(entity)
        }
    }

    override suspend fun getMealsByLetter(letter: String): List<MealDetail> {
        return try {
            val response = mealApi.searchMealsByFirstLetter(letter)
            response.meals?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getPlannedMeals(userId: String) = plannedMealDao.getAllPlannedMeals(userId)

    override suspend fun getPlannedMealsForDateRange(userId: String, start: Long, end: Long): List<PlannedMealEntity> {
        return withContext(Dispatchers.IO) {
            plannedMealDao.getPlannedMealsInRange(userId, start, end)
        }
    }

    override suspend fun updatePlannedMealDate(id: Int, newDate: Long, userId: String) {
        withContext(Dispatchers.IO) {
            plannedMealDao.updateMealDate(id, newDate, userId)
        }
    }
}
