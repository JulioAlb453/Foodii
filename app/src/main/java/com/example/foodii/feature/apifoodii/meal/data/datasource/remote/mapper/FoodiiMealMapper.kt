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
            val cleanDate = this.date?.substringBefore("T") ?: ""
            LocalDate.parse(cleanDate, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            LocalDate.now()
        },
        mealTime = FoodiiMealTime.fromString(this.mealTime ?: "snack"),
        totalCalories = (this.totalCalories ?: 0.0),
        createdBy = this.createdBy ?: "",
        ingredients = this.ingredients?.map { it.toDomain() } ?: emptyList()
    )
}

fun FoodiiMealIngredientDto.toDomain(): FoodiiMealIngredient {
    return FoodiiMealIngredient(
        ingredientId = this.ingredientId ?: "",
        name = this.ingredientName ?: "",
        amount = (this.amount ?: 0),
        calories = (this.calories ?: 0.0)
    )
}

fun FoodiiMeal.toDto(): FoodiiMealDto {
    return FoodiiMealDto(
        id = this.id,
        name = this.name,
        date = this.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
        mealTime = this.mealTime.name.lowercase(Locale.ROOT),
        totalCalories = this.totalCalories,
        createdBy = this.createdBy,
        createdAt = null,
        ingredients = this.ingredients.map { it.toDto() }
    )
}

fun FoodiiMealIngredient.toDto(): FoodiiMealIngredientDto {
    return FoodiiMealIngredientDto(
        ingredientId = this.ingredientId,
        ingredientName = this.name,
        amount = this.amount,
        calories = this.calories
    )
}
