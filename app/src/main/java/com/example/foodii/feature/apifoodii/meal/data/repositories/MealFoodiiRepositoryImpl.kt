package com.example.foodii.feature.apifoodii.meal.data.repositories

import android.util.Log
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.CreateMealIngredientPayloadDto
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.CreateMealRequestDto
import com.example.foodii.feature.apifoodii.meal.data.local.dao.MealRoomDao
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.mapper.toDomain as toDomainDto
import com.example.foodii.feature.apifoodii.meal.data.local.mapper.toDomain as toDomainEntity
import com.example.foodii.feature.apifoodii.meal.data.local.mapper.toRoomEntity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

class MealFoodiiRepositoryImpl @Inject constructor(
    private val foodiiApi: FoodiiAPI,
    private val mealDao: MealRoomDao
) : MealFoodiiRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    override fun findAll(userId: String): Flow<List<FoodiiMeal>> {
        return mealDao.getAllMeals()
            .onStart {
                Log.d("SYNC_MEALS", "Iniciando flujo findAll. Disparando sync...")
                syncMealsFromServer()
            }
            .map { entities ->
                Log.d("SYNC_MEALS", "Datos en Room: ${entities.size} registros")
                entities.map { it.toDomainEntity() }
            }
            .flowOn(Dispatchers.IO)
    }

    private fun syncMealsFromServer() {
        repositoryScope.launch {
            try {
                val response = foodiiApi.getMealsAPI()
                if (response.success == true && response.meals != null) {
                    Log.d("SYNC_MEALS", "API devolvió ${response.meals.size} comidas. Guardando...")
                    val meals = response.meals.map { it.toDomainDto() }
                    
                    // Limpiamos e insertamos los nuevos datos del servidor
                    meals.forEach { meal ->
                        mealDao.insertMeal(meal.toRoomEntity())
                    }
                    Log.d("SYNC_MEALS", "Sincronización exitosa.")
                } else {
                    Log.w("SYNC_MEALS", "La API no devolvió comidas o success=false")
                }
            } catch (e: Exception) {
                Log.e("SYNC_MEALS", "Error en la sincronización: ${e.message}", e)
            }
        }
    }

    override fun findByDate(date: String, userId: String): Flow<List<FoodiiMeal>> {
        return mealDao.getMealsByDate(date).map { entities ->
            entities.map { it.toDomainEntity() }
        }
    }

    override fun findByDateRange(startDate: String, endDate: String, userId: String): Flow<List<FoodiiMeal>> {
        return mealDao.getMealsByDateRange(startDate, endDate).map { entities ->
            entities.map { it.toDomainEntity() }
        }
    }

    override suspend fun saveMeal(meal: FoodiiMeal) {
        mealDao.insertMeal(meal.toRoomEntity())
    }

    override suspend fun createMealOnServer(
        name: String,
        date: LocalDate,
        mealTime: FoodiiMealTime,
        ingredients: List<Pair<String, Int>>,
        steps: List<String>,
        userId: String,
        image: String?,
        categories: List<String>
    ): Result<FoodiiMeal> = runCatching {
        val request = CreateMealRequestDto(
            userId = userId,
            name = name,
            date = date.toString(),
            mealTime = mealTime.name.lowercase(Locale.ROOT),
            ingredients = ingredients.map { CreateMealIngredientPayloadDto(it.first, it.second) },
            steps = steps,
            image = image,
            categories = categories
        )

        val response = foodiiApi.createMeal(request)
        val mealDto = response.meal ?: throw Exception("Error al crear comida")
        val meal = mealDto.toDomainDto()
        
        mealDao.insertMeal(meal.toRoomEntity())
        meal
    }

    override suspend fun getMealById(id: String, userId: String): FoodiiMeal? {
        val local = mealDao.getMealById(id)
        if (local != null) return local.toDomainEntity()
        
        return try {
            val response = foodiiApi.getMealById(id)
            response.meal?.toDomainDto()?.also {
                mealDao.insertMeal(it.toRoomEntity())
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteMeal(id: String, userId: String) {
        mealDao.deleteMeal(id)
    }
}
