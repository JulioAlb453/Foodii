package com.example.foodii.feature.planner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planned_meals")
data class PlannedMealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mealId: String,
    val name: String,
    val imageUrl: String,
    val date: Long
)