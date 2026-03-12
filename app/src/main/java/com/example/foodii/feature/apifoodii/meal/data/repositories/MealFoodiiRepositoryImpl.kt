package com.example.foodii.feature.apifoodii.meal.data.repositories

import android.util.Log
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.meal.data.local.dao.MealRoomDao
import com.example.foodii.feature.apifoodii.meal.data.local.mapper.toDomain
import com.example.foodii.feature.apifoodii.meal.data.local.mapper.toRoomEntity
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.mapper.toDomain as toDomainFromRemote
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MealFoodiiRepositoryImpl(
    private val api: FoodiiAPI,
    private val mealDao: MealRoomDao
) : MealFoodiiRepository {

    override fun findAll(userId: String): Flow<List<FoodiiMeal>> = flow {
        // Emitir primero lo que hay en caché local
        val localFlow = mealDao.getAllMeals(userId).map { list -> list.map { it.toDomain() } }
        
        try {
            // Intentar actualizar desde la red
            val response = api.getMealsAPI(userId = userId)
            if (response.success == true && response.meals != null) {
                val remoteMeals = response.meals.map { it.toDomainFromRemote() }
                // Guardar en Room para persistencia
                mealDao.insertMeals(remoteMeals.map { it.toRoomEntity() })
            }
        } catch (e: Exception) {
            Log.e("AWS_API", "Error al sincronizar con red: ${e.localizedMessage}")
        }
        
        // Seguir emitiendo los cambios de la base de datos local (fuente de verdad)
        emitAll(localFlow)
    }

    override fun findByDate(date: String, userId: String): Flow<List<FoodiiMeal>> = 
        mealDao.getMealsByDate(date, userId).map { list -> list.map { it.toDomain() } }

    override fun findByDateRange(startDate: String, endDate: String, userId: String): Flow<List<FoodiiMeal>> = 
        mealDao.getMealsByDateRange(startDate, endDate, userId).map { list -> list.map { it.toDomain() } }

    override suspend fun saveMeal(meal: FoodiiMeal) {
        try {
            mealDao.insertMeal(meal.toRoomEntity())
        } catch (e: Exception) {
            Log.e("ROOM_DB", "Error al guardar comida: ${e.message}")
        }
    }

    override suspend fun getMealById(id: String, userId: String): FoodiiMeal? {
        val localMeal = mealDao.getMealById(id, userId)
        if (localMeal != null) return localMeal.toDomain()

        return try {
            val response = api.getMealById(id = id)
            val meal = response.meal?.toDomainFromRemote()
            if (meal != null) {
                mealDao.insertMeal(meal.toRoomEntity())
            }
            meal
        } catch (e: Exception) {
            Log.e("AWS_API", "Error al obtener comida por ID: ${e.message}")
            null
        }
    }

    override suspend fun deleteMeal(id: String, userId: String) {
        try {
            mealDao.deleteMeal(id, userId)
        } catch (e: Exception) {
            Log.e("ROOM_DB", "Error al eliminar comida: ${e.message}")
        }
    }
}
