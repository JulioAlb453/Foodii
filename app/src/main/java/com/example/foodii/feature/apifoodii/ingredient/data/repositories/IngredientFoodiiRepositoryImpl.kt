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
        try {
            val response = api.getAllIngredientsAPI()
            if (response.success == true && response.ingredients != null) {
                Log.d(TAG, "Sincronizando ${response.ingredients.size} ingredientes para userId: $userId")
                val entities = response.ingredients.map { dto ->
                    val domain = dto.toDomain()
                    
                    // CORRECCIÓN: Si el servidor no envía createdBy, usamos el userId de la petición
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
            Log.d(TAG, "Encontrados localmente para $userId: ${localEntities.size} ingredientes")
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
            Log.e(TAG, "Error al leer ingredientes de Room: ${e.message}")
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
                // CORRECCIÓN: También aquí aseguramos el createdBy
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
            // Aseguramos que el ingrediente tenga el userId antes de mandarlo/guardarlo
            val ingredientWithUser = if (ingredient.createdBy.isEmpty()) ingredient.copy(createdBy = userId) else ingredient
            val response = api.updateIngredientAPI(id = ingredientWithUser.id, ingredient = ingredientWithUser.toDto())
            if (response.success == true) {
                ingredientDao.insertIngredient(
                    IngredientRoomEntity(
                        id = ingredientWithUser.id,
                        name = ingredientWithUser.name,
                        caloriesPer100g = ingredientWithUser.caloriesPer100g,
                        createdBy = ingredientWithUser.createdBy,
                        createdAt = ingredientWithUser.createdAt?.time
                    )
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al crear ingrediente"))
            }
        } catch (e: Exception) {
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
