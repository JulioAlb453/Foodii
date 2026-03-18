package com.example.foodii.feature.apifoodii.meal.data.local.mapper

import com.example.foodii.feature.apifoodii.meal.data.local.entity.MealRoomEntity
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealIngredient
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val gson = Gson()

fun MealRoomEntity.toDomain(): FoodiiMeal {
    val ingredientsType = object : TypeToken<List<FoodiiMealIngredient>>() {}.type
    val ingredients: List<FoodiiMealIngredient> = try {
        gson.fromJson(this.ingredientsJson, ingredientsType)
    } catch (e: Exception) {
        emptyList()
    }

    return FoodiiMeal(
        id = this.id,
        name = this.name,
        date = LocalDate.parse(this.date, DateTimeFormatter.ISO_LOCAL_DATE),
        mealTime = FoodiiMealTime.fromString(this.mealTime),
        totalCalories = this.totalCalories,
        instructions = this.instructions,
        createdBy = this.createdBy,
        ingredients = ingredients,
        image = this.image
    )
}

fun FoodiiMeal.toRoomEntity(): MealRoomEntity {
    return MealRoomEntity(
        id = this.id,
        name = this.name,
        date = this.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
        mealTime = this.mealTime.name,
        totalCalories = this.totalCalories,
        instructions = this.instructions,
        createdBy = this.createdBy,
        ingredientsJson = gson.toJson(this.ingredients),
        image = this.image
    )
}
