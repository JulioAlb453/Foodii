package com.example.foodii.feature.apifoodii.data.datasource.remote.mapper

import com.example.foodii.feature.apifoodii.data.datasource.remote.model.FoodiiMealDto
import com.example.foodii.feature.apifoodii.data.datasource.remote.model.FoodiiMealIngredientDto
import com.example.foodii.feature.apifoodii.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.domain.entity.FoodiiMealIngredient
import com.example.foodii.feature.apifoodii.domain.entity.FoodiiMealTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun FoodiiMealDto.toDomain(): FoodiiMeal {
    return FoodiiMeal(
        id = this.id ?: "",
        name = this.name ?: "Comida sin nombre",
        date = try {
            LocalDate.parse(this.date, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            LocalDate.now()
        },
        mealTime = FoodiiMealTime.fromString(this.mealTime ?: "snack"),
        totalCalories = this.totalCalories ?: 0.0,
        createdBy = this.createdBy ?: "",
        ingredients = this.ingredients?.map { it.toDomain() } ?: emptyList()
    )
}

fun FoodiiMealIngredientDto.toDomain(): FoodiiMealIngredient {
    return FoodiiMealIngredient(
        ingredientId = this.ingredientId ?: "",
        name = this.name ?: "Ingrediente",
        amount = this.amount ?: 0.0
    )
}


fun List<FoodiiMealDto>.toDomainList(): List<FoodiiMeal> {
    return this.map { it.toDomain() }
}
