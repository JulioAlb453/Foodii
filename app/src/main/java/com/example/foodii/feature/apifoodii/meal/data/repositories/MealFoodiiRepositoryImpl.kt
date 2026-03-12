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
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MealFoodiiRepositoryImpl @Inject constructor(
    private val api: FoodiiAPI,
    private val mealDao: MealRoomDao
) : MealFoodiiRepository {

    override fun findAll(userId: String): Flow<List<FoodiiMeal>> = flow {
        Log.d("DIAGNOSTICO", "Buscando platillos para userId: $userId")
        
        // 1. Emitir lo que ya está en Room
        try {
            val localMeals = mealDao.getAllMeals(userId).first()
            emit(localMeals.map { it.toDomain() })
        } catch (e: Exception) {
            Log.e("AWS_API", "Error al leer local: ${e.message}")
        }

        // 2. Sincronizar y CORREGIR el userId si viene vacío del servidor
        try {
            val response = api.getMealsAPI(userId = userId)
            if (response.success == true && response.meals != null) {
                val roomEntities = response.meals.map { dto ->
                    var domain = dto.toDomainFromRemote()
                    
                    // Si el servidor no mandó el createdBy, le asignamos el que estamos usando
                    if (domain.createdBy.isEmpty()) {
                        domain = domain.copy(createdBy = userId)
                    }
                    
                    domain.toRoomEntity()
                }
                
                Log.d("DIAGNOSTICO", "Insertando ${roomEntities.size} platillos con userId corregido")
                mealDao.insertMeals(roomEntities)
            }
        } catch (e: Exception) {
            Log.e("AWS_API", "Error al sincronizar con red: ${e.localizedMessage}")
        }
        
        // 3. Emitir flujo constante
        emitAll(mealDao.getAllMeals(userId).map { list -> 
            list.map { it.toDomain() } 
        })
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
                // También corregimos aquí por si acaso
                val mealToSave = if (meal.createdBy.isEmpty()) meal.copy(createdBy = userId) else meal
                mealDao.insertMeal(mealToSave.toRoomEntity())
                mealToSave
            } else {
                null
            }
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
