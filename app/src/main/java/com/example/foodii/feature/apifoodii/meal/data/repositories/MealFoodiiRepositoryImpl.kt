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
                Log.d("AWS_API", "ÉXITO: Se recibieron ${response.meals.size} platillos para el usuario $userId")
                emit(response.meals.map { it.toDomain() })
            } else {
                Log.w("AWS_API", "ADVERTENCIA: La API respondió success=false o data vacía")
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("AWS_API", "ERROR CRÍTICO al obtener comidas: ${e.localizedMessage}")
            e.printStackTrace()
            emit(emptyList())
        }
    }

    override fun findByDate(date: String, userId: String): Flow<List<FoodiiMeal>> = flow {
        try {
            val response = api.getMealsAPI(date = date, userId = userId)
            emit(response.meals?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            Log.e("AWS_API", "Error al buscar por fecha: ${e.message}")
            emit(emptyList())
        }
    }

    override fun findByDateRange(startDate: String, endDate: String, userId: String): Flow<List<FoodiiMeal>> = flow {
        try {
            val response = api.getMealsByRange(startDate = startDate, endDate = endDate)
            emit(response.meals?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            Log.e("AWS_API", "Error en rango de fechas: ${e.message}")
            emit(emptyList())
        }
    }

    override suspend fun saveMeal(meal: FoodiiMeal) {
        // Implementar guardado si es necesario
    }

    override suspend fun getMealById(id: String, userId: String): FoodiiMeal? {
        return try {
            val response = api.getMealById(id = id)
            response.meals?.firstOrNull()?.toDomain()
        } catch (e: Exception) {
            Log.e("AWS_API", "Error al obtener comida por ID: ${e.message}")
            null
        }
    }

    override suspend fun deleteMeal(id: String, userId: String) {
        // Implementar eliminación si es necesario
    }
}
