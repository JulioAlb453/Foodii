package com.example.foodii.feature.apifoodii.meal.data.datasource.remote.mapper

import android.util.Log
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealDto
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.FoodiiMealIngredientDto
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.model.MealStepDto
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealIngredient
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealStep
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMealTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun FoodiiMealDto.toDomain(): FoodiiMeal {
    Log.d("MAPPER", "Mapeando meal: ${this.name}, pasos recibidos: ${this.steps?.size ?: 0}")
    
    val stepsDomain = this.steps.orEmpty()
        .mapIndexed { index, dto ->
            val desc = dto.description?.trim()
            if (desc.isNullOrEmpty()) {
                null
            } else {
                val order = if (dto.stepOrder != null && dto.stepOrder > 0) dto.stepOrder else (index + 1)
                FoodiiMealStep(stepOrder = order, description = desc)
            }
        }
        .filterNotNull()
        .sortedBy { it.stepOrder }

    return FoodiiMeal(
        id = this.id ?: "",
        name = this.name ?: "",
        date = try {
            val cleanDate = this.date?.substringBefore("T") ?: ""
            LocalDate.parse(cleanDate, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (_: Exception) {
            LocalDate.now()
        },
        mealTime = FoodiiMealTime.fromString(this.mealTime ?: "snack"),
        totalCalories = this.totalCalories ?: 0.0,
        createdBy = this.createdBy ?: "",
        steps = stepsDomain,
        image = this.image,
        ingredients = this.ingredients?.map { it.toDomain() } ?: emptyList(),
    )
}

fun FoodiiMealIngredientDto.toDomain(): FoodiiMealIngredient {
    val grams = (this.amount ?: 0.0).toInt()
    return FoodiiMealIngredient(
        ingredientId = this.ingredientId ?: "",
        name = this.ingredientName ?: "",
        amount = grams,
        calories = this.calories ?: 0.0,
    )
}

fun FoodiiMeal.toDto(): FoodiiMealDto {
    return FoodiiMealDto(
        id = this.id,
        name = this.name,
        date = this.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
        mealTime = this.mealTime.name.lowercase(Locale.ROOT),
        totalCalories = this.totalCalories,
        steps = this.steps.map { MealStepDto(it.stepOrder, it.description) },
        image = this.image,
        createdBy = this.createdBy,
        createdAt = null,
        ingredients = this.ingredients.map { it.toDto() },
    )
}

fun FoodiiMealIngredient.toDto(): FoodiiMealIngredientDto {
    return FoodiiMealIngredientDto(
        ingredientId = this.ingredientId,
        ingredientName = this.name,
        amount = this.amount.toDouble(),
        calories = this.calories,
    )
}
