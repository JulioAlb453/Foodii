package com.example.foodii.feature.apifoodii.ingredient.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodii.feature.apifoodii.ingredient.data.local.entity.IngredientRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientRoomDao {
    @Query("SELECT * FROM ingredients WHERE createdBy = :userId")
    fun getAllIngredients(userId: String): Flow<List<IngredientRoomEntity>>

    @Query("SELECT * FROM ingredients WHERE id = :id AND createdBy = :userId")
    suspend fun getIngredientById(id: String, userId: String): IngredientRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientRoomEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: IngredientRoomEntity)

    @Query("DELETE FROM ingredients WHERE id = :id AND createdBy = :userId")
    suspend fun deleteIngredientById(id: String, userId: String)

    @Query("DELETE FROM ingredients WHERE createdBy = :userId")
    suspend fun clearIngredients(userId: String)
}
