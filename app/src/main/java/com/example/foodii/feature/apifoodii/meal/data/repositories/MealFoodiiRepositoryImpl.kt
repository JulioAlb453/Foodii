package com.example.foodii.feature.apifoodii.meal.data.repositories

import android.util.Log
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.CreateMealIngredientPayloadDto
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.CreateMealRequestDto
import com.example.foodii.feature.apifoodii.meal.data.local.dao.MealRoomDao
import com.example.foodii.feature.apifoodii.meal.data.local.mapper.toDomain
import com.example.foodii.feature.apifoodii.meal.data.local.mapper.toRoomEntity
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.mapper.toDomain as toDomainFromRemote
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class MealFoodiiRepositoryImpl @Inject constructor(
    private val api: FoodiiAPI,
    private val mealDao: MealRoomDao
) : MealFoodiiRepository {

    override fun findAll(userId: String): Flow<List<FoodiiMeal>> = flow {
        Log.d("REPOSITORY", "Buscando todos los platillos")
        
        // 1. Emitir lo que ya está en Room
        val localInitial = mealDao.getAllMeals().firstOrNull() ?: emptyList()
        emit(localInitial.map { it.toDomain() })

        // 2. Sincronizar protegiendo imágenes locales
        try {
            val response = api.getMealsAPI()
            if (response.success == true && response.meals != null) {
                // Obtenemos los platillos actuales para comparar
                val currentLocalList = mealDao.getAllMeals().firstOrNull() ?: emptyList()
                val localMap = currentLocalList.associateBy { it.id }

                val roomEntities = response.meals.map { dto ->
                    val remoteMeal = dto.toDomainFromRemote()
                    val existingLocal = localMap[remoteMeal.id]
                    
                    // Si el servidor manda imagen vacía pero nosotros tenemos una local, la mantenemos
                    val finalMeal = if (remoteMeal.image.isNullOrEmpty() && !existingLocal?.image.isNullOrEmpty()) {
                        remoteMeal.copy(image = existingLocal?.image)
                    } else {
                        remoteMeal
                    }
                    finalMeal.toRoomEntity()
                }
                mealDao.insertMeals(roomEntities)
                Log.d("REPOSITORY", "Sincronización completada. Total: ${roomEntities.size}")
            }
        } catch (e: Exception) {
            Log.e("REPOSITORY", "Error al sincronizar: ${e.localizedMessage}")
        }
        
        // 3. Emitir flujo constante
        emitAll(mealDao.getAllMeals().map { list -> list.map { it.toDomain() } })
    }

    override fun findByDate(date: String, userId: String): Flow<List<FoodiiMeal>> = 
        mealDao.getMealsByDate(date).map { list -> list.map { it.toDomain() } }

    override fun findByDateRange(startDate: String, endDate: String, userId: String): Flow<List<FoodiiMeal>> = 
        mealDao.getMealsByDateRange(startDate, endDate).map { list -> list.map { it.toDomain() } }

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
        image: String?
    ): Result<FoodiiMeal> {
        return try {
            val body = CreateMealRequestDto(
                userId = userId,
                name = name.trim(),
                date = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                mealTime = mealTime.name.lowercase(Locale.ROOT),
                ingredients = ingredients.map { (ingredientId, amount) ->
                    CreateMealIngredientPayloadDto(ingredientId = ingredientId, amount = amount)
                },
                steps = steps.map { it.trim() }.filter { it.isNotEmpty() },
                image = image
            )
            
            Log.d("AWS_API", "ENVIANDO AL SERVIDOR: ${body.name} con imagen: ${body.image ?: "NULL"}")

            val response = api.createMeal(body)
            if (response.success == true && response.meal != null) {
                var domain = response.meal.toDomainFromRemote()
                // ASEGURAR QUE LA IMAGEN NO SE PIERDA SI EL SERVIDOR NO LA REGRESA EN EL JSON DE RESPUESTA
                if (domain.image.isNullOrEmpty() && !image.isNullOrEmpty()) {
                    domain = domain.copy(image = image)
                }
                mealDao.insertMeal(domain.toRoomEntity())
                Result.success(domain)
            } else {
                Result.failure(Exception("No se pudo crear la comida"))
            }
        } catch (e: Exception) {
            Log.e("AWS_API", "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getMealById(id: String, userId: String): FoodiiMeal? {
        val local = mealDao.getMealById(id)
        if (local != null && !local.image.isNullOrEmpty()) return local.toDomain()

        return try {
            val response = api.getMealById(id = id)
            val meal = response.meal?.toDomainFromRemote()
            if (meal != null) {
                mealDao.insertMeal(meal.toRoomEntity())
                meal
            } else null
        } catch (e: Exception) { null }
    }

    override suspend fun deleteMeal(id: String, userId: String) {
        try { mealDao.deleteMeal(id) } catch (e: Exception) {}
    }
}
