package com.example.foodii.feature.apifoodii.meal.data.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.meal.data.local.dao.MealRoomDao
import com.example.foodii.feature.apifoodii.meal.data.local.mapper.toDomain
import com.example.foodii.feature.apifoodii.meal.data.local.mapper.toRoomEntity
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.mapper.toDomain as toDomainFromRemote
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.repository.MealFoodiiRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class MealFoodiiRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: FoodiiAPI,
    private val mealDao: MealRoomDao
) : MealFoodiiRepository {

    override fun findAll(userId: String): Flow<List<FoodiiMeal>> = flow {
        try {
            val response = api.getMealsAPI(userId = userId)
            if (response.success == true && response.meals != null) {
                val roomEntities = response.meals.map { dto ->
                    dto.toDomainFromRemote().toRoomEntity().copy(
                        createdBy = userId
                    )
                }
                mealDao.insertMeals(roomEntities)
            }
        } catch (e: Exception) {
            Log.e("API_LOAD", "Error cargando lista")
        }
        
        emitAll(mealDao.getAllMeals(userId).map { list -> list.map { it.toDomain() } })
    }

    override fun findByDate(date: String, userId: String): Flow<List<FoodiiMeal>> = 
        mealDao.getMealsByDate(date, userId).map { list -> list.map { it.toDomain() } }

    override fun findByDateRange(startDate: String, endDate: String, userId: String): Flow<List<FoodiiMeal>> = 
        mealDao.getMealsByDateRange(startDate, endDate, userId).map { list -> list.map { it.toDomain() } }

    override suspend fun saveMeal(meal: FoodiiMeal) {
        withContext(Dispatchers.IO) {
            try {
                val userIdRB = meal.createdBy.toRequestBody("text/plain".toMediaTypeOrNull())
                val nameRB = meal.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val dateRB = meal.date.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val mealTimeRB = meal.mealTime.name.lowercase().toRequestBody("text/plain".toMediaTypeOrNull())
                
                val ingredientsList = meal.ingredients.map { 
                    mapOf("ingredientId" to it.ingredientId, "amount" to it.amount) 
                }
                val ingredientsRB = Gson().toJson(ingredientsList).toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null
                meal.image?.let { uriString ->
                    try {
                        val uri = Uri.parse(uriString)
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val tempFile = File(context.cacheDir, "upload_temp.jpg")
                        tempFile.outputStream().use { inputStream?.copyTo(it) }
                        val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
                    } catch (e: Exception) {
                    }
                }

                val response = api.createMealAPI(userIdRB, nameRB, dateRB, mealTimeRB, ingredientsRB, imagePart)
                
                if (response.success == true && response.meal != null) {
                    val domainMeal = response.meal.toDomainFromRemote().let { remote ->
                        if (remote.image == null && meal.image != null) {
                            remote.copy(image = meal.image, createdBy = meal.createdBy)
                        } else {
                            remote.copy(createdBy = meal.createdBy)
                        }
                    }
                    mealDao.insertMeals(listOf(domainMeal.toRoomEntity()))
                } else {
                    mealDao.insertMeal(meal.toRoomEntity())
                }
            } catch (e: Exception) {
                mealDao.insertMeal(meal.toRoomEntity())
            }
        }
    }

    override suspend fun getMealById(id: String, userId: String): FoodiiMeal? {
        val localMeal = mealDao.getMealById(id, userId)
        if (localMeal != null && !localMeal.image.isNullOrEmpty()) return localMeal.toDomain()

        return try {
            val response = api.getMealById(id = id)
            val meal = response.meal?.toDomainFromRemote()
            if (meal != null) {
                mealDao.insertMeal(meal.toRoomEntity())
                meal
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteMeal(id: String, userId: String) {
        try {
            mealDao.deleteMeal(id, userId)
        } catch (e: Exception) {
            Log.e("ROOM_DB", "Error eliminando")
        }
    }
}
