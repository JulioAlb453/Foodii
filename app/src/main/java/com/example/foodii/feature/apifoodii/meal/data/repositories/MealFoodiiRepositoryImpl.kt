package com.example.foodii.feature.apifoodii.meal.data.repositories

import android.util.Log
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.mapper.toDomain
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MealFoodiiRepositoryImpl(
    private val api: FoodiiAPI
) : MealFoodiiRepository {

    override fun findAll(userId: String): Flow<List<FoodiiMeal>> = flow {
        try {
            val response = api.getMealsAPI(userId = userId)

            if (response.success == true && response.meals != null) {
                emit(response.meals.map { it.toDomain() })
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("AWS_API", "Error al obtener comidas: ${e.localizedMessage}")
            emit(emptyList())
        }
    }

    override fun findByDate(date: String, userId: String): Flow<List<FoodiiMeal>> = flow {
        try {
            val response = api.getMealsAPI(userId = userId, date = date)
            emit(response.meals?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun findByDateRange(startDate: String, endDate: String, userId: String): Flow<List<FoodiiMeal>> = flow {
        try {
            val response = api.getMealsByRange(userId = userId, startDate = startDate, endDate = endDate)
            emit(response.meals?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun saveMeal(meal: FoodiiMeal) {
        // Implementar guardado si es necesario
    }

    override suspend fun getMealById(id: String, userId: String): FoodiiMeal? {
        return try {
            val response = api.getMealById(id = id)
            response.meal?.toDomain()
        } catch (e: Exception) {
            Log.e("AWS_API", "Error al obtener comida por ID: ${e.message}")
            null
        }
    }

    override suspend fun deleteMeal(id: String, userId: String) {
        // Implementar eliminación si es necesario
    }
}
