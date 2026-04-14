package com.example.foodii.feature.apifoodii.meal.data.local.mapper

import com.example.foodii.feature.apifoodii.meal.data.local.entity.MealRoomEntity
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealIngredient
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealStep
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
    } catch (_: Exception) {
        emptyList()
    }

    val stepsType = object : TypeToken<List<FoodiiMealStep>>() {}.type
    val steps: List<FoodiiMealStep> = try {
        gson.fromJson(this.stepsJson, stepsType) ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }

    val categoriesType = object : TypeToken<List<String>>() {}.type
    val categories: List<String> = try {
        gson.fromJson(this.categoriesJson, categoriesType) ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }

    return FoodiiMeal(
        id = this.id,
        name = this.name,
        date = LocalDate.parse(this.date, DateTimeFormatter.ISO_LOCAL_DATE),
        mealTime = FoodiiMealTime.fromString(this.mealTime),
        totalCalories = this.totalCalories,
        createdBy = this.createdBy,
        steps = steps,
        image = this.image,
        ingredients = ingredients,
        categories = categories
    )
}

fun FoodiiMeal.toRoomEntity(): MealRoomEntity {
    return MealRoomEntity(
        id = this.id,
        name = this.name,
        date = this.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
        mealTime = this.mealTime.name,
        totalCalories = this.totalCalories,
        stepsJson = gson.toJson(this.steps),
        image = this.image,
        createdBy = this.createdBy,
        ingredientsJson = gson.toJson(this.ingredients),
        categoriesJson = gson.toJson(this.categories)
    )
}
