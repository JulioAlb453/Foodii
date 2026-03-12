package com.example.foodii.feature.apifoodii.ingredient.data.repositories

import android.util.Log
import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.mapper.toDomain
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.mapper.toDto
import com.example.foodii.feature.apifoodii.ingredient.data.local.dao.IngredientRoomDao
import com.example.foodii.feature.apifoodii.ingredient.data.local.entity.IngredientRoomEntity
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IngredientFoodiiRepositoryImpl @Inject constructor(
    private val api: FoodiiAPI,
    private val ingredientDao: IngredientRoomDao
) : IngredientRepository {

    private val TAG = "IngredientRepository"

    override suspend fun getAllIngredients(userId: String): List<Ingredient> {
        Log.d(TAG, "Iniciando obtención de ingredientes para el usuario: $userId")
        try {
            val response = api.getAllIngredientsAPI(userId = userId)
            if (response.success == true && response.ingredients != null) {
                val entities = response.ingredients.map { dto ->
                    val domain = dto.toDomain()
                    IngredientRoomEntity(
                        id = domain.id,
                        name = domain.name,
                        caloriesPer100g = domain.caloriesPer100g,
                        createdBy = domain.createdBy,
                        createdAt = domain.createdAt?.time
                    )
                }
                ingredientDao.insertIngredients(entities)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener ingredientes de la API: ${e.message}")
        }

        return try {
            ingredientDao.getAllIngredients(userId).first().map { entity ->
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
                ingredientDao.insertIngredient(
                    IngredientRoomEntity(
                        id = domain.id,
                        name = domain.name,
                        caloriesPer100g = domain.caloriesPer100g,
                        createdBy = domain.createdBy,
                        createdAt = domain.createdAt?.time
                    )
                )
            }
        } catch (e: Exception) {
            null
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
                Result.failure(Exception("Error al actualizar ingrediente en el servidor"))
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
                Result.failure(Exception("Error al eliminar ingrediente en el servidor"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
