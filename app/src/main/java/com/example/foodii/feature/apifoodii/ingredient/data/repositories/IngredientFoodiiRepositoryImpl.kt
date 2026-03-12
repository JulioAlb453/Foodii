package com.example.foodii.feature.apifoodii.ingredient.data.repositories

import android.util.Log
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.mapper.toDomain
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.mapper.toDto
import com.example.foodii.feature.apifoodii.ingredient.data.local.dao.IngredientRoomDao
import com.example.foodii.feature.apifoodii.ingredient.data.local.entity.IngredientRoomEntity
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IngredientFoodiiRepositoryImpl @Inject constructor(
    private val api: FoodiiAPI,
    private val ingredientDao: IngredientRoomDao
) : IngredientRepository {

    private val TAG = "IngredientRepository"
    private val gson = Gson()

    override suspend fun getAllIngredients(userId: String): List<Ingredient> {
        try {
            val response = api.getAllIngredientsAPI()
            if (response.success == true && response.ingredients != null) {
                Log.d(TAG, "Sincronizando ${response.ingredients.size} ingredientes para userId: $userId")
                val entities = response.ingredients.map { dto ->
                    val domain = dto.toDomain()
                    val finalCreatedBy = if (domain.createdBy.isEmpty()) userId else domain.createdBy
                    IngredientRoomEntity(
                        id = domain.id,
                        name = domain.name,
                        caloriesPer100g = domain.caloriesPer100g,
                        createdBy = finalCreatedBy,
                        createdAt = domain.createdAt?.time
                    )
                }
                ingredientDao.insertIngredients(entities)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener ingredientes de red: ${e.message}")
        }

        return try {
            val localEntities = ingredientDao.getAllIngredients(userId).first()
            localEntities.map { entity ->
                Ingredient(
                    id = entity.id,
                    name = entity.name,
                    caloriesPer100g = entity.caloriesPer100g,
                    createdBy = entity.createdBy,
                    createdAt = entity.createdAt?.let { java.util.Date(it) }
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun findById(id: String, userId: String): Ingredient? {
        val local = ingredientDao.getIngredientById(id, userId)
        if (local != null) {
            return Ingredient(
                id = local.id,
                name = local.name,
                caloriesPer100g = local.caloriesPer100g,
                createdBy = local.createdBy,
                createdAt = local.createdAt?.let { java.util.Date(it) }
            )
        }

        return try {
            val response = api.getIngredientByIdAPI(id = id)
            response.ingredient?.toDomain()?.also { domain ->
                val finalCreatedBy = if (domain.createdBy.isEmpty()) userId else domain.createdBy
                ingredientDao.insertIngredient(
                    IngredientRoomEntity(
                        id = domain.id,
                        name = domain.name,
                        caloriesPer100g = domain.caloriesPer100g,
                        createdBy = finalCreatedBy,
                        createdAt = domain.createdAt?.time
                    )
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createIngredient(ingredient: Ingredient, userId: String): Result<Unit> {
        return try {
            val ingredientWithUser = if (ingredient.createdBy.isEmpty()) ingredient.copy(createdBy = userId) else ingredient
            val dto = ingredientWithUser.toDto()
            
            Log.d(TAG, "Iniciando POST /api/ingredients")
            Log.d(TAG, "Body enviado: ${gson.toJson(dto)}")

            val response = api.createIngredientAPI(ingredient = dto)
            
            Log.d(TAG, "Respuesta recibida (success: ${response.success})")
            Log.d(TAG, "Data recibida: ${gson.toJson(response.ingredient)}")

            if (response.success == true) {
                val createdIngredient = response.ingredient?.toDomain() ?: ingredientWithUser
                ingredientDao.insertIngredient(
                    IngredientRoomEntity(
                        id = createdIngredient.id,
                        name = createdIngredient.name,
                        caloriesPer100g = createdIngredient.caloriesPer100g,
                        createdBy = userId,
                        createdAt = createdIngredient.createdAt?.time ?: System.currentTimeMillis()
                    )
                )
                Log.d(TAG, "Ingrediente guardado en base de datos local exitosamente")
                Result.success(Unit)
            } else {
                Log.e(TAG, "El servidor devolvió success=false en la creación")
                Result.failure(Exception("Error al crear ingrediente"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fatal en POST /api/ingredients: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updateIngredient(ingredient: Ingredient, userId: String): Result<Unit> {
        return try {
            val response = api.updateIngredientAPI(id = ingredient.id, ingredient = ingredient.toDto())
            if (response.success == true) {
                ingredientDao.insertIngredient(
                    IngredientRoomEntity(
                        id = ingredient.id,
                        name = ingredient.name,
                        caloriesPer100g = ingredient.caloriesPer100g,
                        createdBy = ingredient.createdBy,
                        createdAt = ingredient.createdAt?.time
                    )
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al actualizar ingrediente"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteIngredient(id: String, userId: String): Result<Unit> {
        return try {
            val response = api.deleteIngredientAPI(id = id)
            if (response.success == true) {
                ingredientDao.deleteIngredientById(id, userId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar ingrediente"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
