package com.example.foodii.feature.apifoodii.meal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealRoomEntity(
    @PrimaryKey val id: String,
    val name: String,
    val date: String,
    val mealTime: String,
    val totalCalories: Double,
    val instructions: String,
    val createdBy: String,
    val ingredientsJson: String,
    val image: String? = null
)