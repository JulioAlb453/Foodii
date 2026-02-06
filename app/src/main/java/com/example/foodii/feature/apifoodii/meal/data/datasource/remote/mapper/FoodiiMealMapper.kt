package com.example.foodii.feature.apifoodii.meal.data.datasource.remote.mapper

import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealDto
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealIngredientDto
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealIngredient
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun FoodiiMealDto.toDomain(): FoodiiMeal {
    return FoodiiMeal(
        id = this.id ?: "",
        name = this.name ?: "",
        date = try {
            LocalDate.parse(this.date, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            LocalDate.now()
        },
        mealTime = FoodiiMealTime.fromString(this.meal_time ?: "snack"),
        totalCalories = this.total_calories ?: 0.0,
        createdBy = this.created_by ?: "",
        ingredients = this.ingredients?.map { it.toDomain() } ?: emptyList()
    )
}

fun FoodiiMealIngredientDto.toDomain(): FoodiiMealIngredient {
    return FoodiiMealIngredient(
        ingredientId = this.ingredient_id ?: "",
        name = this.name ?: "",
        amount = this.amount ?: 0.0
    )
}

fun List<FoodiiMealDto>.toDomainList(): List<FoodiiMeal> {
    return this.map { it.toDomain() }
}
