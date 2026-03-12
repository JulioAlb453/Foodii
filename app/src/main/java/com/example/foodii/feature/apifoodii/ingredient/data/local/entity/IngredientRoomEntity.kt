package com.example.foodii.feature.apifoodii.ingredient.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class IngredientRoomEntity(
    @PrimaryKey val id: String,
    val name: String,
    val caloriesPer100g: Double,
    val createdBy: String,
    val createdAt: Long?
)
