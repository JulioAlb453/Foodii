package com.example.foodii.feature.apifoodii.meal.data.repositories

import android.util.Log
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.CreateMealIngredientPayloadDto
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.CreateMealRequestDto
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.DeleteMealRequestDto
import com.example.foodii.feature.apifoodii.meal.data.local.dao.MealRoomDao
import com.example.foodii.feature.apifoodii.meal.data.local.entity.MealRoomEntity
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.mapper.toDomain as toDomainDto
import com.example.foodii.feature.apifoodii.meal.data.local.mapper.toDomain as toDomainEntity
import com.example.foodii.feature.apifoodii.meal.data.local.mapper.toRoomEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

class MealFoodiiRepositoryImpl @Inject constructor(
    private val foodiiApi: FoodiiAPI,
    private val mealDao: MealRoomDao,
    private val authLocalDataSource: AuthLocalDataSource
) : MealFoodiiRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    override fun findAll(userId: String): Flow<List<FoodiiMeal>> {
        return mealDao.getAllMeals()
            .onStart { syncMealsFromServer() }
            .map { entities -> entities.map { it.toDomainEntity() } }
            .flowOn(Dispatchers.IO)
    }

    private fun syncMealsFromServer() {
        repositoryScope.launch {
            try {
                val response = foodiiApi.getMealsAPI()
                if (response.success == true && response.meals != null) {
                    val meals = response.meals.map { it.toDomainDto() }
                    meals.forEach { meal ->
                        val existing = mealDao.getMealById(meal.id)
                        mealDao.insertMeal(meal.toRoomEntity())
                    }
                }
            } catch (e: Exception) {
                Log.e("SYNC_MEALS", "Error: ${e.message}")
            }
        }
    }

    override fun findByDate(date: String, userId: String): Flow<List<FoodiiMeal>> =
        mealDao.getMealsByDate(date).map { it.map { e -> e.toDomainEntity() } }

    override fun findByDateRange(startDate: String, endDate: String, userId: String): Flow<List<FoodiiMeal>> =
        mealDao.getMealsByDateRange(startDate, endDate).map { it.map { e -> e.toDomainEntity() } }

    override suspend fun saveMeal(meal: FoodiiMeal) { mealDao.insertMeal(meal.toRoomEntity()) }

    override suspend fun createMealOnServer(
        name: String, date: LocalDate, mealTime: FoodiiMealTime, ingredients: List<Pair<String, Int>>,
        steps: List<String>, userId: String, image: String?, categories: List<String>
    ): Result<FoodiiMeal> = runCatching {
        val request = CreateMealRequestDto(
            userId = userId, name = name, date = date.toString(),
            mealTime = mealTime.name.lowercase(Locale.getDefault()),
            ingredients = ingredients.map { CreateMealIngredientPayloadDto(it.first, it.second) },
            steps = steps, image = image, categories = categories
        )
        val response = foodiiApi.createMeal(request)
        val meal = response.meal?.toDomainDto() ?: throw Exception("Error al crear")
        mealDao.insertMeal(meal.toRoomEntity())
        meal
    }

    override suspend fun getMealById(id: String, userId: String): FoodiiMeal? {
        val local = mealDao.getMealById(id)
        if (local != null) return local.toDomainEntity()
        return try {
            val response = foodiiApi.getMealById(id)
            response.meal?.toDomainDto()?.also { mealDao.insertMeal(it.toRoomEntity()) }
        } catch (e: Exception) { null }
    }

    override suspend fun deleteMeal(id: String, userId: String) {
        try {
            val user = authLocalDataSource.getUser().firstOrNull() 
            
            if (user == null) {
                Log.e("MealRepository", "ALERTA: No se pudo obtener el usuario de DataStore para la eliminación")
                throw Exception("No hay sesión activa")
            }
            
            Log.d("MealRepository", "Usuario obtenido para borrado: ${user.id}")
            val rawToken = user.token ?: ""
            
            if (rawToken.isBlank()) {
                Log.e("MealRepository", "ALERTA: El token recuperado está VACÍO")
            } else {
                Log.d("MealRepository", "TOKEN DATASTORE: $rawToken")
            }

            val cleanToken = rawToken.trim().replace("\"", "")
            val formattedToken = if (cleanToken.startsWith("Bearer ")) cleanToken else "Bearer $cleanToken"
            
            Log.d("MealRepository", "TOKEN FORMATEADO ENVIADO: $formattedToken")
            
            Log.d("MealRepository", "Intentando DELETE en API para platillo $id (Usuario: $userId)")
            val response = foodiiApi.deleteMealAPI(
                token = formattedToken,
                id = id,
                body = DeleteMealRequestDto(userId = userId)
            )

            if (response.success == true) {
                mealDao.deleteMeal(id)
                Log.d("MealRepository", "Eliminación exitosa en servidor y local")
            } else {
                Log.e("MealRepository", "Servidor retornó éxito=false: ${response.message}")
                throw Exception(response.message ?: "Error desconocido en el servidor")
            }
        } catch (e: Exception) {
            Log.e("MealRepository", "Fallo al eliminar platillo con ID $id", e)
            throw e
        }
    }
}
