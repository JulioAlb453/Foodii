package com.example.foodii.feature.apifoodii.meal.data.datasource.remote.mapper

import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealDto
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealIngredientDto
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealIngredient
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        totalCalories = this.total_calories ?: 0,
        createdBy = this.created_by ?: "",
        ingredients = this.ingredients?.map { it.toDomain() } ?: emptyList()
    )
}

fun FoodiiMealIngredientDto.toDomain(): FoodiiMealIngredient {
    return FoodiiMealIngredient(
        ingredientId = this.ingredient_id ?: "",
        name = this.name ?: "",
        amount = this.amount?: 0,
        calories = this.calories ?: 0
    )
}


fun FoodiiMeal.toDto(): FoodiiMealDto {
    return FoodiiMealDto(
        id = this.id,
        name = this.name,
        date = this.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
        meal_time = this.mealTime.name.lowercase(Locale.ROOT),
        total_calories = this.totalCalories,
        created_by = this.createdBy,
        ingredients = this.ingredients.map { it.toDto() }
    )
}

fun FoodiiMealIngredient.toDto(): FoodiiMealIngredientDto {
    return FoodiiMealIngredientDto(
        ingredient_id = this.ingredientId,
        name = this.name,
        amount = this.amount,
        calories = this.calories
    )
}